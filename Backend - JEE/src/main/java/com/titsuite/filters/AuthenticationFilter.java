package com.titsuite.filters;

import com.titsuite.dao.UserDAO;
import com.titsuite.managers.TokenManager;
import com.titsuite.users.User;
import com.titsuite.utils.ResponseBuilder;
import org.apache.log4j.Logger;
import org.jose4j.jwt.consumer.InvalidJwtException;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    final static Logger logger = Logger.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    public static final String HEADER_PROPERTY_ID = "id";
    public static final String HEADER_PROPERTY_ROLE = "role";
    public static final String AUTHORIZATION_PROPERTY = "token";

    private static final String ACCESS_INVALID_TOKEN = "Token is invalid. Please authenticate again!";
    private static final String ACCESS_DENIED = "Not allowed to access this resource!";
    private static final String ACCESS_FORBIDDEN = "Access forbidden!";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        if (!method.isAnnotationPresent(PermitAll.class)) {
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(
                    ResponseBuilder.createResponse(Response.Status.FORBIDDEN, ACCESS_FORBIDDEN)
                );
                return;
            }

            Cookie cookie = null;
            for (Cookie c : requestContext.getCookies().values())
            {
                if (c.getName().equals(AUTHORIZATION_PROPERTY)) {
                    cookie = c;
                    break;
                }
            }

            if (cookie == null || cookie.getValue().isEmpty()) {
                logger.warn("No token has been provided!");
                requestContext.abortWith(
                    ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_INVALID_TOKEN)
                );
                return;
            }

            String id;
            String role;
            String jwt = cookie.getValue();

            try {
                Map<String, Object> jwtMap = TokenManager.validateJWT(jwt,
                    TokenManager.KeySelection.AUTHENTICATION_KEY);
                id = (String) jwtMap.get("id");
                role = (String) jwtMap.get("role");
            } catch (InvalidJwtException e) {
                logger.warn("Invalid token provided!");
                requestContext.abortWith(
                        ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_INVALID_TOKEN)
                );
                return;
            }

            UserDAO userDao = new UserDAO();
            User user = null;

            try {
                if (userDao.roleValidityCheck(role))
                    user = userDao.findUserById(role, Long.parseLong(id));
                else {
                    logger.warn("Invalid token provided!");
                    requestContext.abortWith(
                            ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_INVALID_TOKEN + " here")
                    );
                }
            } catch (SQLException e) {
                logger.warn("Invalid token provided!");
                requestContext.abortWith(
                        ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_INVALID_TOKEN)
                );
            }

            if (user == null) {
                logger.warn("Invalid token provided!");
                requestContext.abortWith(
                        ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_INVALID_TOKEN)
                );
                return;
            }

            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

                if (!isUserAllowed(role, rolesSet)) {
                    logger.warn("User does not have the rights to access this resource!");
                    requestContext.abortWith(
                            ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, ACCESS_DENIED)
                    );
                    return;
                }
            }

            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
            headers.put(HEADER_PROPERTY_ID, Collections.singletonList(id));
            headers.put(HEADER_PROPERTY_ROLE, Collections.singletonList(role));
        }
    }

    private boolean isUserAllowed(final String userRole, final Set<String> rolesSet) {
        boolean isAllowed = false;
        if (rolesSet.contains(userRole))
            isAllowed = true;

        return isAllowed;
    }

}
