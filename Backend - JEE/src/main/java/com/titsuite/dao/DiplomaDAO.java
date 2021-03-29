package com.titsuite.dao;

import com.titsuite.exceptions.DiplomaNotFoundException;
import com.titsuite.diplomas.Diploma;
import com.titsuite.exceptions.UnauthorizedDiplomaChange;
import com.titsuite.utils.DateMapper;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DiplomaDAO {

    public Diploma findDiplomaById(long id) throws DiplomaNotFoundException, SQLException {
        Connection connection = ConnectionFactory.getConnection();
        String searchQuery = "SELECT * FROM DIPLOMA WHERE ID = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(searchQuery);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Diploma diploma = null;
        if (resultSet.next()) {
            diploma = new Diploma(id, resultSet.getString(2),
                DateMapper.sqlToJavaDate(resultSet.getDate(3)), resultSet.getString(4),
                resultSet.getLong(5));
        }

        connection.close();

        if (diploma == null)
            throw new DiplomaNotFoundException(id);

        return diploma;
    }

    public List<Diploma> findFreelancerDiplomas(long refId) throws SQLException {
        List<Diploma> diplomaList = new ArrayList<>();

        Connection connection = ConnectionFactory.getConnection();
        String searchQuery = "SELECT * FROM DIPLOMA WHERE FREELANCER_REF = ?";

        PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
        searchStatement.setLong(1, refId);
        ResultSet resultSet = searchStatement.executeQuery();

        while (resultSet.next()) {
            Diploma foundDiploma = new Diploma(resultSet.getLong(1), resultSet.getString(2),
                DateMapper.sqlToJavaDate(resultSet.getDate(3)), resultSet.getString(4), refId);
            diplomaList.add(foundDiploma);
        }

        connection.close();

        return diplomaList;
    }

    public long createDiploma(Diploma diploma) throws SQLException {
        Connection connection = ConnectionFactory.getConnection();
        String insertQuery = "BEGIN INSERT INTO DIPLOMA (NAME, ACQUISITION_DATE, FIELD, FREELANCER_REF) VALUES (?, ?, ?, ?) RETURNING id INTO ?; END;";

        CallableStatement insertStatement = connection.prepareCall(insertQuery);
        insertStatement.setString(1, diploma.getName());
        insertStatement.setDate(2, DateMapper.javaToSqlDate(diploma.getAcquisitionDate()));
        insertStatement.setString(3, diploma.getField());
        insertStatement.setLong(4, diploma.getFreelancerRef());
        insertStatement.registerOutParameter(5, Types.NUMERIC);

        insertStatement.executeUpdate();
        long generatedId = insertStatement.getLong(5);
        connection.close();

        return generatedId;
    }

    public void updateDiploma(Map<String, ?> conditionMap, Map<String, ?> dataMap) throws DiplomaNotFoundException,
        SQLException {
        Connection connection = ConnectionFactory.getConnection();

        StringBuilder updateQueryBuilder = new StringBuilder("UPDATE DIPLOMA SET ");
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
                updateQueryBuilder.append("AND ");
        }

        PreparedStatement updateStatement = connection.prepareStatement(updateQueryBuilder.toString());

        int i = 1;
        for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
            if (entry.getValue() instanceof String)
                updateStatement.setString(i, (String) entry.getValue());
            else if (entry.getValue() instanceof Date)
                updateStatement.setDate(i, DateMapper.javaToSqlDate((Date) entry.getValue()));
            else if (entry.getValue() instanceof Long)
                updateStatement.setLong(i, (Long) entry.getValue());
            i++;
        }
        for (Map.Entry<String, ?> entry : conditionMap.entrySet()) {
            if (entry.getValue() instanceof String)
                updateStatement.setString(i, (String) entry.getValue());
            else if (entry.getValue() instanceof Date)
                updateStatement.setDate(i, DateMapper.javaToSqlDate((Date) entry.getValue()));
            else if (entry.getValue() instanceof Long)
                updateStatement.setLong(i, (Long) entry.getValue());
            i++;
        }

        updateStatement.executeUpdate();

        connection.close();
    }

    public void removeDiplomaById(long id, long refId) throws DiplomaNotFoundException, UnauthorizedDiplomaChange,
        SQLException {
        Connection connection = ConnectionFactory.getConnection();

        String searchQuery = "SELECT * FROM DIPLOMA WHERE ID = ?";
        PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
        searchStatement.setLong(1, id);

        ResultSet resultSet = searchStatement.executeQuery();
        if (!resultSet.next()) {
            connection.close();
            throw new DiplomaNotFoundException();
        }

        Diploma foundDiploma = new Diploma(id, resultSet.getString(2),
            DateMapper.sqlToJavaDate(resultSet.getDate(3)), resultSet.getString(4),
            resultSet.getLong(5));

        if (foundDiploma.getFreelancerRef() != refId)
            throw new UnauthorizedDiplomaChange(id);

        String deleteQuery = "DELETE FROM DIPLOMA WHERE ID = ?";

        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
        deleteStatement.setLong(1, id);

        deleteStatement.executeUpdate();

        connection.close();
    }

}
