package com.titsuite.users;

import com.titsuite.dao.DiplomaDAO;
import com.titsuite.dao.UserDAO;
import com.titsuite.diplomas.Diploma;
import com.titsuite.exceptions.UserExistsException;
import com.titsuite.exceptions.UserNotFoundException;
import com.titsuite.filters.AuthenticationFilter;
import com.titsuite.managers.PasswordManager;
import com.titsuite.managers.TokenManager;
import com.titsuite.models.AuthCredentials;
import com.titsuite.models.MessageModel;
import com.titsuite.utils.Mailer;
import com.titsuite.utils.RandomStringGenerator;
import com.titsuite.utils.ResponseBuilder;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@DeclareRoles({ "customer", "freelancer", "staff" })
@Path("/users")
public class UserResource {

    @POST
    @PermitAll
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(final AuthCredentials credentials) {
        UserDAO userDao = new UserDAO();

        try {
            User newUser = new User();
            newUser.setEmail(credentials.getEmail());
            newUser.setHashedPassword(PasswordManager.hashPassword(credentials.getPassword()));
            newUser.setAddress(credentials.getAddress());
            newUser.setCity(credentials.getCity());
            newUser.setPhoneNumber(credentials.getPhoneNumber());

            RandomStringGenerator rsg = new RandomStringGenerator(32);
            String verificationCode = rsg.nextString();
            newUser.setVerificationCode(verificationCode);

            if (userDao.isCustomer(credentials.getRole()))
                userDao.createUser(new Customer(newUser));
            else if (userDao.isFreelancer(credentials.getRole()))
                userDao.createUser(new Freelancer(newUser));
            else if (userDao.isStaff(credentials.getRole()))
                userDao.createUser(new Staff(newUser));
            else
                return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, "Invalid role: "
                    + credentials.getRole());

            new Thread(() -> {
                String body = "Hello there,\nThank you for registering!\n\n" +
                    "Please verify your email address by clicking the following link.\n" +
                    "Verification Link: http://localhost:8080/Titsuite-1.0-SNAPSHOT/api/users/" +
                    "activateAccount?role=" + credentials.getRole() + "&email=" + credentials.getEmail() + "&code="
                    + verificationCode + "\n\nUnverified accounts are automatically deleted " +
                    "after a 24 hours period.\nHave a pleasant day!";

                Properties properties = new Properties();
                try {
                    properties.load(UserResource.class.getClassLoader().getResourceAsStream("config.properties"));
                    Mailer.sendGmail(properties.getProperty("mailUsername"), properties.getProperty("mailPassword"),
                        credentials.getEmail(), "Account Verification", body);
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }).start();

            return ResponseBuilder.createResponse(Response.Status.CREATED, "User created successfully!");
        } catch (UserExistsException e) {
            return ResponseBuilder.createResponse(Response.Status.CONFLICT, e.getMessage());
        } catch (SQLException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GET
    @PermitAll
    @Path("/activateAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateAccount(@QueryParam("role") final String role, @QueryParam("email") final String email,
        @QueryParam("code") final String receivedCode) {
        UserDAO userDao = new UserDAO();

        if (role == null || email == null || receivedCode == null)
            return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, "Invalid route!");

        if (!userDao.roleValidityCheck(role))
            return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, "Invalid role: " + role);

        try {
            User foundUser = userDao.findUserByEmail(role, email);
            if (foundUser.getIsActive() == 1)
                return ResponseBuilder.createResponse(Response.Status.CONFLICT, "Account is already verified!");
            if (!foundUser.getVerificationCode().equals(receivedCode))
                return ResponseBuilder.createResponse(Response.Status.CONFLICT, "Invalid verification code!");

            Map<String, Object> conditionMap = new HashMap<>(2, 1f);
            conditionMap.put("EMAIL", email);
            Map<String, Object> dataMap = new HashMap<>(4, 1f);
            dataMap.put("VERIFICATION_CODE", null);
            dataMap.put("IS_ACTIVE", 1);
            dataMap.put("RESEND_TIMEOUT", null);

            userDao.updateUser(role, conditionMap, dataMap);

            return ResponseBuilder.createResponse(Response.Status.ACCEPTED,
        "User account has been verified successfully!");
        } catch (UserNotFoundException e) {
            return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } catch (SQLException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @POST
    @PermitAll
    @Path("/resendVerification")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resendVerification(final AuthCredentials credentials) {
        UserDAO userDao = new UserDAO();

        if (!userDao.roleValidityCheck(credentials.getRole()))
            return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, "Invalid role: "
                + credentials.getRole());

        try {
            User foundUser = userDao.findUserByEmail(credentials.getRole(), credentials.getEmail());
            if (!PasswordManager.validatePassword(credentials.getPassword(), foundUser.getHashedPassword()))
                return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, "Wrong password!");

            if (foundUser.getIsActive() == 1)
                return ResponseBuilder.createResponse(Response.Status.CONFLICT, "Account is already verified!");
            Date resendTimeout = foundUser.getResendTimeout();
            if (resendTimeout != null && resendTimeout.getTime() > new Date().getTime())
                return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED,
            "Please wait until the resend timeout!");

            RandomStringGenerator rsg = new RandomStringGenerator(32);
            String verificationCode = rsg.nextString();

            Map<String, Object> conditionMap = new HashMap<>(2, 1f);
            conditionMap.put("EMAIL", credentials.getEmail());

            Map<String, Object> dataMap = new HashMap<>(2, 1f);
            dataMap.put("VERIFICATION_CODE", verificationCode);
            dataMap.put("RESEND_TIMEOUT", new Date(new Date().getTime() + 300000));

            userDao.updateUser(credentials.getRole(), conditionMap, dataMap);

            new Thread(() -> {
                String body = "Hello there,\nPer your request, we've sent you a new verification link!\n\n" +
                    "Please verify your email address by clicking the following link.\n" +
                    "Verification Link: http://localhost:8080/Titsuite-1.0-SNAPSHOT/api/users/" +
                    "activateAccount?role=" + credentials.getRole() + "&email=" + credentials.getEmail() + "&code="
                    + verificationCode + "\n\nUnverified accounts are automatically deleted " +
                    "after a 24 hours interval.\nHave a pleasant day!";

                Properties properties = new Properties();
                try {
                    properties.load(TokenManager.class.getClassLoader().getResourceAsStream("config.properties"));
                    Mailer.sendGmail(properties.getProperty("mailUsername"), properties.getProperty("mailPassword"),
                        credentials.getEmail(), "Account Verification", body);
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }).start();

            return ResponseBuilder.createResponse(Response.Status.OK, "A new verification link is being sent!");
        } catch (UserNotFoundException e) {
            return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @POST
    @PermitAll
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(final AuthCredentials credentials) {
        UserDAO userDao = new UserDAO();
        User foundUser;

        try {
            if (!userDao.roleValidityCheck(credentials.getRole()))
                return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, "Invalid role: "
                    + credentials.getRole());

            foundUser = userDao.findUserByEmail(credentials.getRole(), credentials.getEmail());
            if (!PasswordManager.validatePassword(credentials.getPassword(), foundUser.getHashedPassword()))
                return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, "Wrong password!");

            if (foundUser.getIsActive() == 0)
                return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED,
            "Account is not yet verified!");

            // Authentication token to be sent back to caller (Valid for 17 minutes)
            Map<String, Object> authDataMap = new HashMap<>(4, 1f);
            authDataMap.put("id", Long.toString(foundUser.getId()));
            authDataMap.put("role", credentials.getRole());
            String authToken = TokenManager.generateJWT(authDataMap, TokenManager.KeySelection.AUTHENTICATION_KEY);

            // Refresh token to be stored in database (Valid for 1 week)
            Map<String, Object> refreshDataMap = new HashMap<>(4, 1f);
            refreshDataMap.put("email", foundUser.getEmail());
            refreshDataMap.put("role", credentials.getRole());
            String refreshToken = TokenManager.generateJWT(refreshDataMap, TokenManager.KeySelection.REFRESH_KEY);

            Map<String, Object> updateConditionMap = new HashMap<>(2, 1f);
            updateConditionMap.put("EMAIL", credentials.getEmail());
            Map<String, Object> updateDataMap = new HashMap<>(2, 1f);
            updateDataMap.put("REFRESH_TOKEN", refreshToken);
            userDao.updateUser(credentials.getRole(), updateConditionMap, updateDataMap);

            Map<String, Object> claimsMap = new HashMap<>(2, 1f);
            claimsMap.put(AuthenticationFilter.AUTHORIZATION_PROPERTY, authToken);

            NewCookie authCookie = new NewCookie(AuthenticationFilter.AUTHORIZATION_PROPERTY, authToken, "/",
            "", null, TokenManager.getAuthTTL() * 60, false, true);

            return ResponseBuilder.createResponse(Response.Status.ACCEPTED, authCookie, claimsMap);
        } catch (UserNotFoundException e) {
            return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } catch (SQLException | JoseException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GET
    @RolesAllowed({ "customer", "freelancer", "staff" })
    @Path("/refreshToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshAuthToken(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) final String id,
        @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ROLE) final String role) {
        UserDAO userDao = new UserDAO();

        try {
            User foundUser = userDao.findUserById(role, Long.parseLong(id));
            String refreshToken = foundUser.getRefreshToken();
            TokenManager.validateJWT(refreshToken, TokenManager.KeySelection.REFRESH_KEY);

            Map<String, Object> authDataMap = new HashMap<>(4, 1f);
            authDataMap.put("id", id);
            authDataMap.put("role", role);
            String authToken = TokenManager.generateJWT(authDataMap, TokenManager.KeySelection.AUTHENTICATION_KEY);

            Map<String, Object> claimsMap = new HashMap<>(2, 1f);
            claimsMap.put(AuthenticationFilter.AUTHORIZATION_PROPERTY, authToken);

            NewCookie authCookie = new NewCookie(AuthenticationFilter.AUTHORIZATION_PROPERTY, authToken, "/",
            "", null, TokenManager.getAuthTTL() * 60, false, true);

            return ResponseBuilder.createResponse(Response.Status.ACCEPTED, authCookie, claimsMap);
        } catch (UserNotFoundException e) {
            return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } catch (InvalidJwtException e) {
            return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED,
            "Your session has expired. Please login again!");
        } catch (SQLException | NumberFormatException | JoseException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @POST
    @RolesAllowed({ "customer", "freelancer", "staff" })
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) final String id,
        @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ROLE) final String role) {
        NewCookie authCookie = new NewCookie(AuthenticationFilter.AUTHORIZATION_PROPERTY, "", "/",
        "", null,0, false, true);

        new Thread(() -> {
            UserDAO userDao = new UserDAO();
            Map<String, Object> conditionMap = new HashMap<>(2, 1f);
            conditionMap.put("ID", Long.parseLong(id));
            Map<String, Object> dataMap = new HashMap<>(2, 1f);
            dataMap.put("REFRESH_TOKEN", null);

            try {
                userDao.updateUser(role, conditionMap, dataMap);
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseBuilder.createResponse(Response.Status.OK, authCookie, "Logged out successfully!");
    }

    @GET
    @RolesAllowed({ "customer", "freelancer", "staff" })
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) final String id,
        @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ROLE) final String role) {
        UserDAO userDao = new UserDAO();

        try {
            long userId = Long.parseLong(id);
            User foundUser = userDao.findUserById(role, userId);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("email", foundUser.getEmail());
            responseMap.put("firstName", foundUser.getFirstName());
            responseMap.put("lastName", foundUser.getLastName());
            responseMap.put("phoneNumber", foundUser.getPhoneNumber());
            if (foundUser.getBirthDate() != null)
                responseMap.put("birthDate", foundUser.getBirthDate().getTime());
            responseMap.put("city", foundUser.getCity());
            responseMap.put("address", foundUser.getAddress());

            if (userDao.isCustomer(role))
                responseMap.put("subscription", ((Customer) foundUser).getSubscription());
            else if (userDao.isFreelancer(role)) {
                DiplomaDAO diplomaDao = new DiplomaDAO();
                List<Diploma> diplomaList = diplomaDao.findFreelancerDiplomas(userId);
                responseMap.put("activity", ((Freelancer) foundUser).getActivity());
                responseMap.put("minimumWage", ((Freelancer) foundUser).getMinimumWage());
                responseMap.put("diplomas", ResponseBuilder.buildJSONArray(diplomaList).toString());
            }
            else if (userDao.isStaff(role))
                responseMap.put("role", ((Staff) foundUser).getRole());

            return ResponseBuilder.createResponse(Response.Status.OK, responseMap);
        } catch (UserNotFoundException e) {
            return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } catch (SQLException | NumberFormatException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @POST
    @RolesAllowed({ "customer", "freelancer", "staff" })
    @Path("/profile/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProfile(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) final String id,
        @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ROLE) final String role, AuthCredentials credentials) {
        UserDAO userDao = new UserDAO();

        try {
            Map<String, Object> conditionMap = new HashMap<>(2, 1f);
            conditionMap.put("ID", Long.parseLong(id));

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("FIRST_NAME", credentials.getFirstName());
            dataMap.put("LAST_NAME", credentials.getLastName());
            dataMap.put("PHONE_NUMBER", credentials.getPhoneNumber());
            dataMap.put("BIRTH_DATE", credentials.getBirthDate());
            dataMap.put("CITY", credentials.getCity());
            dataMap.put("ADDRESS", credentials.getAddress());

            if (userDao.isCustomer(role))
                dataMap.put("SUBSCRIPTION", credentials.getSubscription());
            else if (userDao.isFreelancer(role)) {
                dataMap.put("ACTIVITY", credentials.getActivity());
                dataMap.put("MINIMUM_WAGE", credentials.getMinimumWage());
            }
            else if (userDao.isStaff(role))
                dataMap.put("ROLE", credentials.getRole());
            userDao.updateUser(role, conditionMap, dataMap);

            return ResponseBuilder.createResponse(Response.Status.OK, "User profile updated successfully!");
        } catch (SQLException | NumberFormatException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @POST
    @RolesAllowed({ "customer", "freelancer" })
    @Path("/sendComplaint")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendComplaint(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id,
        @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ROLE) String role, final MessageModel message) {
        new Thread(() -> {
            UserDAO userDao = new UserDAO();

            Properties properties = new Properties();
            try {
                User foundUser = userDao.findUserById(role, Long.parseLong(id));
                String body = "Message from " + role + ": " + foundUser.getEmail() + "\n\n";
                body += message.getBody();

                List<Staff> staffList = (List<Staff>) userDao.findUsersByRole("staff");
                Staff selectedStaff = staffList.get(new Random().nextInt(staffList.size()));

                properties.load(TokenManager.class.getClassLoader().getResourceAsStream("config.properties"));
                Mailer.sendGmail(properties.getProperty("mailUsername"), properties.getProperty("mailPassword"),
                    selectedStaff.getEmail(), message.getTitle(), body);
            } catch (UserNotFoundException | MessagingException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseBuilder.createResponse(Response.Status.OK, "Complaint is being sent to staff!");
    }

    @GET
    @RolesAllowed({ "staff" })
    @Path("/{role}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByRole(@PathParam("role") final String role) {
        UserDAO userDao = new UserDAO();
        List<? extends User> userList;

        try {
            if (userDao.roleValidityCheck(role))
                userList = userDao.findUsersByRole(role);
            else
                return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, "Invalid role: " + role);

            return ResponseBuilder.createResponse(Response.Status.OK, userList);
        } catch (SQLException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
