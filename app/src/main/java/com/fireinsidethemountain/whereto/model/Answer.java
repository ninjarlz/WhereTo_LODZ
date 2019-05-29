package com.fireinsidethemountain.whereto.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Answer {

    private String _enquireID;
    private String _enquireContent;
    private String _placeID;
    private String _authorID;
    private String _authorNickname;

    public Answer() {

    }


    public Answer(String enquireID, String enquireContent, String placeID, String authorID, String authorNickname) {
        _enquireID = enquireID;
        _enquireContent = enquireContent;
        _placeID = placeID;
        _authorID = authorID;
        _authorNickname = authorNickname;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("enquireID", _enquireID);
        result.put("enquireContent", _enquireContent);
        result.put("place", _placeID);
        result.put("authorID", _authorID);
        result.put("authorNickname", _authorNickname);
        return result;
    }
}
