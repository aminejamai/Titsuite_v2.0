package com.titsuite.utils;

public class DateMapper {

    public static java.sql.Date javaToSqlDate(java.util.Date javaDate) {
        java.sql.Date sqlDate = null;
        if (javaDate != null)
            sqlDate = new java.sql.Date(javaDate.getTime());

        return sqlDate;
    }

    public static java.util.Date sqlToJavaDate(java.sql.Date sqlDate) {
        java.util.Date javaDate = null;
        if (sqlDate != null)
            javaDate = new java.util.Date(sqlDate.getTime());

        return javaDate;
    }

}
