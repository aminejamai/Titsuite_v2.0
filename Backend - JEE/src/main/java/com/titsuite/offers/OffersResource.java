package com.titsuite.offers;

import com.titsuite.dao.ConnectionFactory;
import com.titsuite.filters.AuthenticationFilter;
import com.titsuite.managers.TokenManager;
import com.titsuite.users.Customer;
import com.titsuite.users.User;
import com.titsuite.utils.DateMapper;
import org.jose4j.jwt.consumer.InvalidJwtException;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DeclareRoles({ "customer", "freelancer", "staff" })
@Path("/offers")
public class OffersResource {
    private Connection connection = null;


    @POST
    @Path("/new")
    @RolesAllowed({"customer", "freelancer", "staff"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createNewOffer(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id, Offer offer) {
        //int refCustomer= Integer.parseInt(id);
        System.out.println(
                "your id : " + id
        );

        Offer newOffer = new Offer(offer.getDescription(), offer.getCity(), offer.getMinimumWage(), offer.getStatus(), id, offer.getStartDay(), offer.getActivity());
        System.out.println(newOffer.getRefCustomer() + "   " + newOffer.getStatus());
        try {


            connection = ConnectionFactory.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO OFFERS(DESCRIPTION, CITY, MINIMUMWAGE, STATUS, REFCUSTOMER, STARTDAY, ACTIVITY) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setString(1, newOffer.getDescription());
            preparedStatement.setString(2, newOffer.getCity());
            preparedStatement.setInt(3, newOffer.getMinimumWage());
            preparedStatement.setString(4, newOffer.getStatus());
            preparedStatement.setString(5, newOffer.getRefCustomer());
            preparedStatement.setDate(6, DateMapper.javaToSqlDate(newOffer.getStartDay()));
            preparedStatement.setString(7, newOffer.getActivity());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return Response.status(200)
                .entity("Your new offer has been created successfully ")
                .build();
    }


    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Offer> getAllOffers() {
        Statement statement = null;
        ResultSet resultSet = null;

        ArrayList<Offer> allOffers = new ArrayList<Offer>();
        //step2 create  the connection object
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.createStatement();


            resultSet = statement.executeQuery("SELECT * FROM OFFERS WHERE STATUS != 'prise en charge' ");


            while (resultSet.next()) {
                Offer temp = new Offer(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getString(6), DateMapper.sqlToJavaDate(resultSet.getDate(7)), resultSet.getString(8));
                allOffers.add(temp);


            }

            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return allOffers;
    }

    ;

    @Path("/city={ville}")
    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Offer> getOfferByCity(@PathParam("ville") String city) {
        ArrayList<Offer> filteredOffersByCity = new ArrayList<Offer>();
        try {
            connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select * from offers where city=(?)");
            stmt.setString(1, city);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Offer data = new Offer(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getString(6), DateMapper.sqlToJavaDate(resultSet.getDate(7)), resultSet.getString(8));
                filteredOffersByCity.add(data);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return filteredOffersByCity;
    }

    ;

    @Path("/activity={activity}")
    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Offer> getOfferById(@PathParam("activity") int id) {

        ArrayList<Offer> filteredOffersById = new ArrayList<Offer>();
        try {
            connection = ConnectionFactory.getConnection();
            PreparedStatement statement2 = connection.prepareStatement("select * from offers where ACTIVITY=(?)");
            statement2.setInt(1, id);
            ResultSet resultSet = statement2.executeQuery();

            while (resultSet.next()) {
                Offer data = new Offer(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getString(6), DateMapper.sqlToJavaDate(resultSet.getDate(7)), resultSet.getString(8));
                filteredOffersById.add(data);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return filteredOffersById;

    }


    @POST
    @Path("/update")
    @RolesAllowed("freelancer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOfferStatus(offerResponse responseToOffer, @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id) {

        String response = responseToOffer.getResponse();//to be : traité from the frontend
        int offerID = responseToOffer.getID();

        System.out.println(response + " ...................................");
        int ID = Integer.parseInt(id);
        try {
            connection = ConnectionFactory.getConnection();
            PreparedStatement updateStat = connection.prepareStatement("UPDATE OFFERS SET STATUS=(?) where ID=(?)");
            updateStat.setString(1, response);
            updateStat.setInt(2, offerID);
            ResultSet resultSet = updateStat.executeQuery();

            if(response.equalsIgnoreCase("prise en charge")){
                PreparedStatement addJob = connection.prepareStatement("INSERT INTO JOB(REFFREELANCER,REFRATE,REFOFFER) VALUES(?,?,?)");

                addJob.setInt(1, ID);
                addJob.setInt(2, 1);
                addJob.setInt(3, offerID);
                addJob.executeQuery();

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return  Response.status(200).entity("Your response has been saved, Thank you for your time!").build();
    }

    @Path("/myoffers")
    @GET
    @RolesAllowed({"customer"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Offer> getCustomerOffers(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id) {

        ArrayList<Offer> filteredOffersById = new ArrayList<Offer>();
        int ID=Integer.parseInt(id);
        try {
            connection = ConnectionFactory.getConnection();
            PreparedStatement statement2 = connection.prepareStatement("select * from offers where REFCUSTOMER=(?) and status = (?)");
            statement2.setInt(1, ID);
            statement2.setString(2, "en attente");
            ResultSet resultSet = statement2.executeQuery();

            while (resultSet.next()) {
                Offer data = new Offer(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getString(6), DateMapper.sqlToJavaDate(resultSet.getDate(7)), resultSet.getString(8));
                filteredOffersById.add(data);
                System.out.println(resultSet.getString(5));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return filteredOffersById;
    }

    @Path("/mypreviousoffers")
    @GET
    @RolesAllowed({"customer"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Offer> getCustomerPreviousOffers(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id) {

        ArrayList<Offer> filteredOffersById = new ArrayList<Offer>();
        int ID=Integer.parseInt(id);
        try {
            connection = ConnectionFactory.getConnection();
            PreparedStatement statement2 = connection.prepareStatement("select * from offers where REFCUSTOMER=(?)");
            statement2.setInt(1, ID);
            ResultSet resultSet = statement2.executeQuery();

            while (resultSet.next()) {
                Offer data = new Offer(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getString(6), DateMapper.sqlToJavaDate(resultSet.getDate(7)), resultSet.getString(8));
                filteredOffersById.add(data);
                System.out.println(resultSet.getString(5));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return filteredOffersById;
    }
    @GET
    @RolesAllowed({"freelancer"})//freelancers are the only ones to be allowed...
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Offer> getAvailableOffersByActivity(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String id) {
        Statement statement = null;
        Statement getFreelancerActivityStatement = null;
        ResultSet resultSet = null;
        ResultSet freelancerResult=null;
        String activity=null;

        ArrayList<Offer> allOffers = new ArrayList<Offer>();
        //step2 create  the connection object

        int ID =  Integer.parseInt(id);
        try{
            connection=ConnectionFactory.getConnection();

            getFreelancerActivityStatement=connection.createStatement();

            freelancerResult=getFreelancerActivityStatement.executeQuery("SELECT ACTIVITY FROM FREELANCER WHERE ID="+ID);

            while (freelancerResult.next()) {
               activity=freelancerResult.getString(1);
                System.out.println("my activity    : "+ activity);

            }

            getFreelancerActivityStatement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        try {
            connection = ConnectionFactory.getConnection();

            statement = connection.createStatement();


            System.out.println("jjjjjjjj"+ activity);
          resultSet = statement.executeQuery("SELECT * FROM OFFERS WHERE STATUS='en attente' AND ACTIVITY='"+activity+"'");


            while (resultSet.next()) {
                Offer temp = new Offer(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getString(6), DateMapper.sqlToJavaDate(resultSet.getDate(7)), resultSet.getString(8));
                allOffers.add(temp);


            }

            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return allOffers;
    }

}