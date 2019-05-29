package com.fireinsidethemountain.whereto.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public EnquireType getType() {
        return _type;
    }

    public void setType(EnquireType type) {
        _type = type;
    }

    public String getContent() {
        return _content;
    }

    public void setContent(String content) {
        _content = content;
    }

    public Date getCreationDate() {
        return _creationDate;
    }

    public void setCreationDate(Date creationDate) {
        _creationDate = creationDate;
    }

    public List<Integer> getAnswersIDs() {
        return _answersIDs;
    }

    public void setAnswersIDs(List<Integer> answersIDs) {
        _answersIDs = answersIDs;
    }

    public int getHowUseful() {
        return _howUseful;
    }

    public void setHowUseful(int howUseful) {
        _howUseful = howUseful;
    }

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

    @Override
    public String toString() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = sdf.format(_creationDate);

        return "Author: " + _authorNickname + "\n" +
                "Posted: " + date + "\n" +
                "Content: " + _content;
    }





}
