package com.projet.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projet.common.model.Message;

public class JSONUtils {

    // L'objet Gson est thread-safe, on peut le garder en static
    private static final Gson gson = new GsonBuilder().create();

    /**
     * Transforme un objet Message en une chaîne de caractères JSON (Sérialisation)
     */
    public static String serialize(Message message) {
        return gson.toJson(message);
    }

    /**
     * Transforme une chaîne JSON en un objet Message (Désérialisation)
     */
    public static Message deserialize(String json) {
        return gson.fromJson(json, Message.class);
    }
}
