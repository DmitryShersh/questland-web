package com.questland.pojo;

import org.json.JSONObject;

public class Author {
    private int id;
    private String login;
    private String pass;
    private String email;
    private String description;
    private String avatarURL;

    public Author(int id, String login, String pass, String email,
                  String description, String avatarURL) {
        this.id = id;
        this.login = login;
        this.pass = pass;
        this.email = email;
        this.description = description;
        this.avatarURL = avatarURL;
    }

    public int getId() {
        return id;
    }

    public JSONObject toJSON() {
        JSONObject authorJSON = new JSONObject();

        authorJSON.put("id", id);
        authorJSON.put("login", login);
        authorJSON.put("description", description);
        authorJSON.put("avatarURL", avatarURL);

        return authorJSON;
    }
}
