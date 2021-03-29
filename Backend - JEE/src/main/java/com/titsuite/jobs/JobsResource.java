package com.titsuite.jobs;


import com.titsuite.dao.ConnectionFactory;
import com.titsuite.filters.AuthenticationFilter;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@DeclareRoles({ "customer", "freelancer", "staff" })
@Path("/myjobs")
public class JobsResource {

Connection connection=null;

    @GET
    @Path("/all")
    @RolesAllowed("freelancer")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Job> getAllJobs( @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id ){  //needs to return details about the offer, name of customer, date, description
        ArrayList<Job> myjobs=new ArrayList<Job>();
        int ID=Integer.parseInt(id);

try{

    connection= ConnectionFactory.getConnection();
    Statement query= connection.createStatement();
    ResultSet resultSet= query.executeQuery("   select O.city  , O.minimumWage  ,O.STARTDAY, C.first_name,  C.LAST_NAME, R.RATE, R.REVIEW, O.DESCRIPTION FROM OFFERS O\n" +
            "                                                                          RIGHT JOIN CUSTOMER C ON O.REFCUSTOMER=C.ID\n" +
            "                                                                          RIGHT JOIN JOB J on J.REFOFFER=O.ID\n" +
            "                                                                          RIGHT JOIN RATE R ON J.REFRATE=R.ID\n" +
            "      where J.REFFREELANCER=" +ID
            );


    while(resultSet.next()){
        Job myjob=new Job(resultSet.getString(1),resultSet.getInt(2),resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),resultSet.getInt(6), resultSet.getString(7) ,resultSet.getString(8) );
      myjobs.add(myjob);
    }
} catch (SQLException throwables) {
    throwables.printStackTrace();
}


        return myjobs;
    }

    @GET
    @Path("/completed")
    @RolesAllowed("customer")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Job> getCustomerJobs( @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id ){  //needs to return details about the offer, name of customer, date, description
        ArrayList<Job> myjobs=new ArrayList<Job>();
        int ID=Integer.parseInt(id);

        try{

            connection= ConnectionFactory.getConnection();
            Statement query= connection.createStatement();
            ResultSet resultSet= query.executeQuery("   select O.city  , O.minimumWage  ,O.STARTDAY, F.first_name,  F.LAST_NAME, R.RATE, R.REVIEW, O.DESCRIPTION FROM OFFERS O\n" +
                    "                                                                         " +
                    "                                                                          RIGHT JOIN JOB J on J.REFOFFER=O.ID\n" +
                    "                                                                          RIGHT JOIN RATE R ON J.REFRATE=R.ID\n" +" RIGHT JOIN FREELANCER F ON J.REFFREELANCER=F.ID\n"+
                    "      where O.REFCUSTOMER=" +ID
            );


            while(resultSet.next()){
                Job myjob=new Job(resultSet.getString(1),resultSet.getInt(2),resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),resultSet.getInt(6), resultSet.getString(7) ,resultSet.getString(8) );
                myjobs.add(myjob);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return myjobs;
    }
}
