package com.tidzamapp.sabri.tidzam;

import org.json.JSONObject;

/**
 * Created by Sabri on 09/01/2018.
 */

public class Predicitions {

    private String name;
    private JSONObject jsonObject;

    public Predicitions(String name, JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

}
