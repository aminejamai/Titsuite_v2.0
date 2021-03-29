package com.titsuite.dao;

import com.titsuite.exceptions.UserExistsException;
import com.titsuite.exceptions.UserNotFoundException;
import com.titsuite.users.Customer;
import com.titsuite.users.Freelancer;
import com.titsuite.users.Staff;
import com.titsuite.users.User;
import com.titsuite.utils.DateMapper;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class UserDAO {

    private static final String CUSTOMER_ROLE = "customer";
    private static final String FREELANCER_ROLE = "freelancer";
    private static final String STAFF_ROLE = "staff";

    public boolean isCustomer(final String role) { return role.equalsIgnoreCase(CUSTOMER_ROLE); }

    public boolean isFreelancer(final String role) { return role.equalsIgnoreCase(FREELANCER_ROLE); }

    public boolean isStaff(final String role) { return role.equalsIgnoreCase(STAFF_ROLE); }

    public boolean roleValidityCheck(final String role) {
        return isCustomer(role) || isFreelancer(role) || isStaff(role);
    }

    private void createQuery(final String role, User user, Object... args) throws UserExistsException,
        SQLException {
        String tableName = null;
        String insertQuery = null;
        if (isCustomer(role)) {
            tableName = "CUSTOMER";
            insertQuery = "INSERT INTO CUSTOMER (EMAIL, HASHED_PASSWORD, FIRST_NAME, LAST_NAME, " +
                "PHONE_NUMBER, BIRTH_DATE, CITY, ADDRESS, SUBSCRIPTION, REFRESH_TOKEN, VERIFICATION_CODE, IS_ACTIVE)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        else if (isFreelancer(role)) {
            tableName = "FREELANCER";
            insertQuery = "INSERT INTO FREELANCER (EMAIL, HASHED_PASSWORD, FIRST_NAME, LAST_NAME, " +
                "PHONE_NUMBER, BIRTH_DATE, CITY, ADDRESS, ACTIVITY, MINIMUM_WAGE, REFRESH_TOKEN, VERIFICATION_CODE,"
                + " IS_ACTIVE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        else if (isStaff(role)) {
            tableName = "STAFF";
            insertQuery = "INSERT INTO STAFF (EMAIL, HASHED_PASSWORD, FIRST_NAME, LAST_NAME, " +
                "PHONE_NUMBER, BIRTH_DATE, CITY, ADDRESS, ROLE, REFRESH_TOKEN, VERIFICATION_CODE, IS_ACTIVE) VALUES"
                + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        Connection connection = ConnectionFactory.getConnection();
        final String searchQuery = "SELECT * FROM " + tableName + " WHERE EMAIL = ?";
        PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
        searchStatement.setString(1, user.getEmail());

        if (searchStatement.executeQuery().next()) {
            connection.close();
            throw new UserExistsException(user.getEmail());
        }

        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setString(1, user.getEmail());
        insertStatement.setString(2, user.getHashedPassword());
        insertStatement.setString(3, user.getFirstName());
        insertStatement.setString(4, user.getLastName());
        insertStatement.setString(5, user.getPhoneNumber());
        insertStatement.setDate(6, DateMapper.javaToSqlDate(user.getBirthDate()));
        insertStatement.setString(7, user.getCity());
        insertStatement.setString(8, user.getAddress());

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String)
                insertStatement.setString(i+9, (String) args[i]);
            else if (args[i] instanceof Float)
                insertStatement.setFloat(i+9, (Float) args[i]);
            else if (args[i] instanceof Integer)
                insertStatement.setInt(i+9, (Integer) args[i]);
            else if (args[i] == null)
                insertStatement.setString(i+9, null);
        }

        insertStatement.executeUpdate();

        connection.close();
    }

    public void createUser(User user) throws UserExistsException, SQLException {
        if (user instanceof Customer)
            createQuery(CUSTOMER_ROLE, user, ((Customer) user).getSubscription(), null,
                user.getVerificationCode(), user.getIsActive());
        else if (user instanceof Freelancer)
            createQuery(FREELANCER_ROLE, user, ((Freelancer) user).getActivity(),
                ((Freelancer) user).getMinimumWage(), null, user.getVerificationCode(), user.getIsActive());
        else if (user instanceof Staff)
            createQuery(STAFF_ROLE, user, ((Staff) user).getRole(), null, user.getVerificationCode(),
                user.getIsActive());
    }

    private List<? extends User> findQuery(final String role, final String findBy, final String arg)
        throws UserNotFoundException, SQLException {
        List<User> userList = new ArrayList<>();

        String tableName = null;
        if (isCustomer(role))
            tableName = "CUSTOMER";
        else if (isFreelancer(role))
            tableName = "FREELANCER";
        else if (isStaff(role))
            tableName = "STAFF";

        Connection connection = ConnectionFactory.getConnection();
        String searchQuery = "SELECT * FROM " + tableName;
        if (findBy != null && arg != null)
            searchQuery += " WHERE " + findBy + " = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(searchQuery);
        if (findBy != null && arg != null)
            preparedStatement.setString(1, arg);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            User foundUser = null;
            if (isCustomer(role)) {
                foundUser = new Customer(
                    resultSet.getLong(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4),
                    resultSet.getString(5), resultSet.getString(6),
                    DateMapper.sqlToJavaDate(resultSet.getDate(7)),
                    resultSet.getString(8), resultSet.getString(9),
                    resultSet.getString(10), resultSet.getString(11),
                    resultSet.getString(12), resultSet.getInt(13),
                    DateMapper.sqlToJavaDate(resultSet.getDate(15))
                );
            }
            else if (isFreelancer(role)) {
                foundUser = new Freelancer(
                    resultSet.getLong(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4),
                    resultSet.getString(5), resultSet.getString(6),
                    DateMapper.sqlToJavaDate(resultSet.getDate(7)),
                    resultSet.getString(8), resultSet.getString(9),
                    resultSet.getString(10), resultSet.getFloat(11),
                    resultSet.getString(12), resultSet.getString(13),
                    resultSet.getInt(14), DateMapper.sqlToJavaDate(resultSet.getDate(16))
                );
            }
            else if (isStaff(role)) {
                foundUser = new Staff(
                    resultSet.getLong(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4),
                    resultSet.getString(5), resultSet.getString(6),
                    DateMapper.sqlToJavaDate(resultSet.getDate(7)),
                    resultSet.getString(8), resultSet.getString(9),
                    resultSet.getString(10), resultSet.getString(11),
                    resultSet.getString(12), resultSet.getInt(13),
                    DateMapper.sqlToJavaDate(resultSet.getDate(15))
                );
            }
            userList.add(foundUser);
        }

        connection.close();

        if (findBy != null && arg != null && userList.size() == 0)
            throw new UserNotFoundException();

        return userList;
    }

    public User findUserById(final String role, long id) throws UserNotFoundException, SQLException {
        return findQuery(role, "ID", Long.toString(id)).get(0);
    }

    public User findUserByEmail(final String role, String email) throws UserNotFoundException,
        SQLException {
        return findQuery(role, "EMAIL", email).get(0);
    }

    public List<? extends User> findUsersByRole(final String role) throws UserNotFoundException, SQLException {
        return findQuery(role, null, null);
    }

    public void updateUser(final String role, Map<String, ?> conditionMap, Map<String, ?> dataMap)
        throws SQLException {
        Connection connection = ConnectionFactory.getConnection();

        StringBuilder updateQueryBuilder = new StringBuilder("UPDATE " + role + " SET ");
        for (Iterator<? extends Map.Entry<String, ?>> it = dataMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, ?> entry = it.next();
            updateQueryBuilder.append(entry.getKey()).append(" = ?");
            if (it.hasNext())
                updateQueryBuilder.append(", ");
        }

        updateQueryBuilder.append(" WHERE ");
        for (Iterator<? extends Map.Entry<String, ?>> it = conditionMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, ?> entry = it.next();
            updateQueryBuilder.append(entry.getKey()).append(" = ?");
            if (it.hasNext())
                updateQueryBuilder.append(" AND ");
        }

        PreparedStatement updateStatement = connection.prepareStatement(updateQueryBuilder.toString());

        int i = 1;
        for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
            if (entry.getValue() instanceof String || entry.getValue() == null)
                updateStatement.setString(i, (String) entry.getValue());
            else if (entry.getValue() instanceof Float)
                updateStatement.setFloat(i, (Float) entry.getValue());
            else if (entry.getValue() instanceof Integer)
                updateStatement.setInt(i, (Integer) entry.getValue());
            else if (entry.getValue() instanceof Long)
                updateStatement.setLong(i, (Long) entry.getValue());
            else if (entry.getValue() instanceof Date)
                updateStatement.setDate(i, DateMapper.javaToSqlDate((Date) entry.getValue()));
            i++;
        }
        for (Map.Entry<String, ?> entry : conditionMap.entrySet()) {
            if (entry.getValue() instanceof String || entry.getValue() == null)
                updateStatement.setString(i, (String) entry.getValue());
            else if (entry.getValue() instanceof Float)
                updateStatement.setFloat(i, (Float) entry.getValue());
            else if (entry.getValue() instanceof Integer)
                updateStatement.setInt(i, (Integer) entry.getValue());
            else if (entry.getValue() instanceof Long)
                updateStatement.setLong(i, (Long) entry.getValue());
            else if (entry.getValue() instanceof Date)
                updateStatement.setDate(i, DateMapper.javaToSqlDate((Date) entry.getValue()));
            i++;
        }

        updateStatement.executeUpdate();

        connection.close();
    }

}
