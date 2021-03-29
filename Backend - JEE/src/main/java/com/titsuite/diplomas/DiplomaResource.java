package com.titsuite.diplomas;

import com.titsuite.dao.DiplomaDAO;
import com.titsuite.exceptions.DiplomaNotFoundException;
import com.titsuite.exceptions.UnauthorizedDiplomaChange;
import com.titsuite.filters.AuthenticationFilter;
import com.titsuite.utils.ResponseBuilder;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@DeclareRoles({ "customer", "freelancer", "staff" })
@Path("/diplomas")
public class DiplomaResource {

    @POST
    @RolesAllowed({ "freelancer" })
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDiploma(@HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) final String id,
        final Diploma diploma) {
        DiplomaDAO diplomaDao = new DiplomaDAO();

        try {
            diploma.setFreelancerRef(Long.parseLong(id));
            Long generatedId = diplomaDao.createDiploma(diploma);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("id", generatedId);

            return ResponseBuilder.createResponse(Response.Status.CREATED, responseMap);
        } catch (SQLException | NumberFormatException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GET
    @RolesAllowed({ "customer", "freelancer", "staff" })
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiploma(@QueryParam("id") final String id) {
        if (id == null)
            return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, "Invalid route!");

        DiplomaDAO diplomaDao = new DiplomaDAO();

        try {
            Diploma foundDiploma = diplomaDao.findDiplomaById(Long.parseLong(id));

            return ResponseBuilder.createResponse(Response.Status.OK, foundDiploma);
        } catch (DiplomaNotFoundException e) {
            return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @POST
    @RolesAllowed({ "freelancer" })
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDiploma(final Diploma diploma) {
        DiplomaDAO diplomaDao = new DiplomaDAO();

        try {
            Map<String, Object> conditionMap = new HashMap<>(2, 1f);
            conditionMap.put("id", diploma.getId());
            Map<String, Object> dataMap = new HashMap<>(4, 1f);
            dataMap.put("NAME", diploma.getName());
            dataMap.put("ACQUISITION_DATE", diploma.getAcquisitionDate());
            dataMap.put("FIELD", diploma.getField());

            diplomaDao.updateDiploma(conditionMap, dataMap);

            return ResponseBuilder.createResponse(Response.Status.OK, "Diploma updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DELETE
    @RolesAllowed({ "freelancer" })
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDiploma(Diploma diploma, @HeaderParam(AuthenticationFilter.HEADER_PROPERTY_ID) String refId) {
        DiplomaDAO diplomaDao = new DiplomaDAO();

        try {
            diplomaDao.removeDiplomaById(diploma.getId(), Long.parseLong(refId));

            return ResponseBuilder.createResponse(Response.Status.OK, "Diploma deleted successfully!");
        } catch (DiplomaNotFoundException e) {
            return ResponseBuilder.createResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedDiplomaChange e) {
            return ResponseBuilder.createResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return ResponseBuilder.createResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
