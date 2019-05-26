package com.fireinsidethemountain.whereto.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Enquire {

    public enum EnquireType {Food, Accomodation, Events, Facilities};
    private String _authorID;
    private String _authorNickname;
    private EnquireType _type;
    private String _content;
    private Date _creationDate;
    private List<Integer> _answersIDs;
    private int _howUseful;


    public Enquire() {

    }

    public Enquire(String authorID, String authorNickname,  EnquireType type,  String content, Date creationDate, List<Integer> answersIDs, int howUseful) {
        _authorID = authorID;
        _authorNickname = authorNickname;
        _type = type;
        _content = content;
        _creationDate = creationDate;
        _answersIDs = answersIDs;
        _howUseful = howUseful;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("authorID", _authorID);
        result.put("authorNickname", _authorNickname);
        result.put("type", _type);
        result.put("content", _content);
        result.put("creationDate", _creationDate);
        result.put("answersIDs", _answersIDs);
        result.put("howUseful", _howUseful);
        return result;
    }





}
