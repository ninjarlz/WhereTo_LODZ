package com.fireinsidethemountain.whereto.model;

import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.view.MainScreen;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;

@IgnoreExtraProperties
public class Enquire implements Comparable<Enquire> {

    @Override
    public int compareTo(Enquire o) {
        if (o._answerCount > _answerCount) {
            return -1;
        } else if (o._answerCount == _answerCount) {
            return _creationDate.compareTo(o._creationDate);
        } else {
            return 1;
        }
    }

    public enum EnquireType {Food, Accomodation, Events, Facilities};
    private String _authorID;
    private String _authorNickname;
    private EnquireType _type;
    private String _content;
    private Date _creationDate;
    private String _enquireID;
    private Map<String, String> _answersIDs = new HashMap<>(); // consists of AnswerID : AnswerID
    private int _answerCount;

    public String getEnquireID() {
        return  _enquireID;
    }

    public void setEnquireID (String enquireID) {
        _enquireID = enquireID;
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

    public Map<String, String> getAnswersIDs() {
        return _answersIDs;
    }

    public void setAnswersIDs(Map<String, String> answersIDs) {
        _answersIDs = answersIDs;
    }

    public int getAnswerCount() {
        return _answerCount;
    }

    public void setAnswerCount(int answerCount) {
        _answerCount = answerCount;
    }
    public Enquire() {

    }

    public Enquire(String enquireID, String authorID, String authorNickname,  EnquireType type,  String content, Date creationDate) {
        _enquireID = enquireID;
        _authorID = authorID;
        _authorNickname = authorNickname;
        _type = type;
        _content = content;
        _creationDate = creationDate;
    }




    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("enquireID", _enquireID);
        result.put("authorID", _authorID);
        result.put("authorNickname", _authorNickname);
        result.put("type", _type);
        result.put("content", _content);
        result.put("creationDate", _creationDate);
        result.put("answersIDs", _answersIDs);
        result.put("answersCount", _answerCount);
        return result;
    }


    /*@Override
    public String toString() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = sdf.format(_creationDate);

        return "Author: " + _authorNickname + "\n" +
                "Posted: " + date + "\n" +
                "Content: " + _content + "\n" +
                "Number of answers: " + _answerCount;
    }*/

    @Override
    public String toString() {
        Resources resources = MainScreen.getContext().getResources();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = sdf.format(_creationDate);

        return resources.getString(R.string.author) + ": " + _authorNickname + "\n" +
                resources.getString(R.string.posted) + ": " + date + "\n" +
                resources.getString(R.string.enquireContent) + ": " + _content + "\n" +
                resources.getString(R.string.numberOfAnswers) + ": " + _answerCount;
    }




}
