package com.titsuite.utils;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonSerializable {
    public JSONObject toJson() throws JSONException;
}
