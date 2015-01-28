package com.questland.pojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Situation {
    private int questID;
    private int situationID;
    private String imageURL;
    private String smallDescription;
    private String bigDescription;
    private boolean draft;
    private List<String> choices;
    private List<Integer> moves;

    public Situation(int questID, int situationID, String imageURL,
                     String smallDescription, String bigDescription, boolean draft,
                     List<String> choices, List<Integer> moves) {
        this.questID = questID;
        this.situationID = situationID;
        this.imageURL = imageURL;
        this.smallDescription = smallDescription;
        this.bigDescription = bigDescription;
        this.draft = draft;
        this.choices = choices;
        this.moves = moves;
    }

    public int getQuestID() {
        return questID;
    }

    public int getSituationID() {
        return situationID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getSmallDescription() {
        return smallDescription;
    }

    public String getBigDescription() {
        return bigDescription;
    }

    public boolean isDraft() {
        return draft;
    }

    public List<String> getChoices() {
        return choices;
    }

    public List<Integer> getMoves() {
        return moves;
    }

    public JSONObject toJSON() {
        JSONObject sitObj = new JSONObject();

        sitObj.put("questID", questID);
        sitObj.put("situationID", situationID);
        sitObj.put("imageURL", imageURL);
        sitObj.put("smallDescription", smallDescription);
        sitObj.put("bigDescription", bigDescription);
        sitObj.put("draft", draft);

        JSONArray choiceArr = new JSONArray();
        JSONArray moveArr = new JSONArray();

        for (int i = 0; i < choices.size(); i++) {
            choiceArr.put(i, choices.get(i));
            moveArr.put(i, moves.get(i));
        }

        sitObj.put("choices", choiceArr);
        sitObj.put("moves", moveArr);

        return sitObj;
    }
}
