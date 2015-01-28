package com.questland.pojo;

import org.json.JSONObject;

public class Quest {
    private int id;
    private String name;
    private int authorID;
    private String author;
    private int universeID;
    private String universe;
    private String category;
    private String genres;
    private String characters;
    private String description;
    private String imageURL;
    private int rate;

    public Quest(int id, String name, int authorID, String author, int universeID,
                 String universe, String category, String genres,
                 String characters, String description, String imageURL, int rate)
    {
        this.id = id;
        this.name = name;
        this.authorID = authorID;
        this.author = author;
        this.universeID = universeID;
        this.universe = universe;
        this.category = category;
        this.genres = genres;
        this.characters = characters;
        this.description = description;
        this.imageURL = imageURL;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAuthorID() {
        return authorID;
    }

    public String getAuthor() {
        return author;
    }

    public int getUniverseID() {
        return universeID;
    }

    public String getUniverse() {
        return universe;
    }

    public String getCategory() {
        return category;
    }

    public String getGenres() {
        return genres;
    }

    public String getCharacters() {
        return characters;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public JSONObject toJSON() {
        JSONObject questObj = new JSONObject();

        questObj.put("id", getId());
        questObj.put("name", getName());
        questObj.put("authorID", getAuthorID());
        questObj.put("author", getAuthor());
        questObj.put("universeID", getUniverseID());
        questObj.put("universe", getUniverse());
        questObj.put("category", getCategory());
        questObj.put("genre", getGenres());
        questObj.put("characters", getCharacters());
        questObj.put("description", getDescription());
        questObj.put("imageURL", getImageURL());
        questObj.put("rate", rate);

        return questObj;
    }
}
