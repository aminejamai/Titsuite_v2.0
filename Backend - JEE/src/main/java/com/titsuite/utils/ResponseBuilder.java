package com.titsuite.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    public static Response createResponse(Response.Status status) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message", status.toString());
        }
        catch(JSONException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(status).entity(jsonObject.toString()).build();
    }

    public static Response createResponse(Response.Status status, String message) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message", message);
        }
        catch(JSONException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(status).entity(jsonObject.toString()).build();
    }

    public static Response createResponse(Response.Status status, JsonSerializable json) throws JSONException {
        return Response.status(status).entity(json.toJson().toString()).build();
    }

    public static Response createResponse(Response.Status status, List<? extends JsonSerializable> json)
            throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (JsonSerializable jsonSerializable : json)
            jsonArray.put(jsonSerializable.toJson());

        return Response.status(status).entity(jsonArray.toString()).build();
    }

    public static JSONArray buildJSONArray(List<? extends JsonSerializable> json) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (JsonSerializable jsonSerializable : json)
            jsonArray.put(jsonSerializable.toJson());

        return jsonArray;
    }

    public static Response createResponse(Response.Status status, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();

        try {
            for (Map.Entry<String,Object> entry : map.entrySet())
                jsonObject.put(entry.getKey(), entry.getValue());
        }
        catch(JSONException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(status).entity(jsonObject.toString()).build();
    }

    public static Response createResponse(Response.Status status, NewCookie authCookie, String message) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message", message);
        }
        catch(JSONException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(status).cookie(authCookie).entity(jsonObject.toString()).build();
    }

    public static Response createResponse(Response.Status status, NewCookie authCookie, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();

        if (map != null) {
            try {
                for (Map.Entry<String,Object> entry : map.entrySet())
                    jsonObject.put(entry.getKey(), entry.getValue());
            }
            catch(JSONException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }

        return Response.status(status).cookie(authCookie).entity(jsonObject.toString()).build();
    }

}
