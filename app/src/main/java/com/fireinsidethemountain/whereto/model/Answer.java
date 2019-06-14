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

    public String getEnquireID() {
        return _enquireID;
    }

    public void setEnquireID(String enquireID) {
        _enquireID = enquireID;
    }

    public String getEnquireContent() {
        return _enquireContent;
    }

    public void setEnquireContent(String enquireContent) {
        _enquireContent = enquireContent;
    }

    public String getPlaceID() {
        return _placeID;
    }

    public void setPlaceID(String placeID) {
        _placeID = placeID;
    }

    public String getAuthorID() {
        return _authorID;
    }

    public void setAuthorID(String authorID) {
        _authorID = authorID;
    }

    public String getAuthorNickname() {
        return _authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        _authorNickname = authorNickname;
    }


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
