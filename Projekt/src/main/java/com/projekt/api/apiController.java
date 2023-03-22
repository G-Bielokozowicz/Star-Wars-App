package com.projekt.api;

import com.google.gson.JsonObject;

public class apiController {
    private api api;

    public apiController(api api) {
        this.api = api;
    }

    public JsonObject get(String path, String query){
        JsonObject jsonObject = new JsonObject();

        try {
            jsonObject = api.requestBuilder(path, query.replace(" ","%20"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JsonObject getFromURL(String url) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject = api.fromURLRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

