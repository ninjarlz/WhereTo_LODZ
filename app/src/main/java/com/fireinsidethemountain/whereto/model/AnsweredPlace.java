package com.fireinsidethemountain.whereto.model;
import java.util.HashMap;
import java.util.Map;

public class AnsweredPlace {

    private String _mostPopularEnquireID;
    private Enquire.EnquireType _mostPopularEnquireType;
    private String _mostPopularEnquireContent;
    private double _latPos;
    private double _lngPos;
    private String _placeName;
    private Map<String, String> _answersIDs = new HashMap<>(); // consists of AnswerID : EnquireID
    private Map<String, Integer> _enquireIDsCount = new HashMap<>(); // consists of EnquireID : number of answers regarding this Enquire


    public double getLatPos() {
        return _latPos;
    }

    public void setLatPos(double latPos) {
        _latPos = latPos;
    }

    public double getLngPos() {
        return _lngPos;
    }

    public void setLngPos(double lngPos) {
        _lngPos = lngPos;
    }

    public String getMostPopularEnquireContent() {
        return _mostPopularEnquireContent;
    }

    public void setMostPopularEnquireContent(String mostPopularEnquireContent) {
        _mostPopularEnquireContent = mostPopularEnquireContent;
    }

    public String getPlaceName() {
        return _placeName;
    }

    public void setPlaceName(String placeName) {
        _placeName = placeName;
    }




    public Map<String, Integer> getEnquireIDsCount() {
        return _enquireIDsCount;
    }

    public void setEnquireIDsCount(Map<String, Integer> enquireIDsCount) {
        _enquireIDsCount = enquireIDsCount;
    }

    public String getMostPopularEnquireID() {
        return _mostPopularEnquireID;
    }

    public void setMostPopularEnquireID(String mostPopularEnquireID) {
        _mostPopularEnquireID = mostPopularEnquireID;
    }

    public Enquire.EnquireType getMostPopularEnquireType() {
        return _mostPopularEnquireType;
    }

    public void setMostPopularEnquireType(Enquire.EnquireType mostPopularEnquireType) {
        _mostPopularEnquireType = mostPopularEnquireType;
    }

    public Map<String, String> getAnswersIDs() {
        return _answersIDs;
    }

    public void setAnswersIDs(Map<String, String> answersIDs) {
        _answersIDs = answersIDs;
    }

    public AnsweredPlace() {

    }

    public AnsweredPlace(String mostPopularEnquireID, Enquire.EnquireType mostPopularEnquireType, String mostPopularEnquireContent, String placeName, double latPos, double lngPos) {
        _mostPopularEnquireID = mostPopularEnquireID;
        _mostPopularEnquireType = mostPopularEnquireType;
        _mostPopularEnquireContent = mostPopularEnquireContent;
        _placeName = placeName;
        _latPos = latPos;
        _lngPos = lngPos;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mostPopularEnquireID", _mostPopularEnquireID);
        result.put("mostPopularEnquireType", _mostPopularEnquireType);
        result.put("mostPopularEnquireContent", _mostPopularEnquireContent);
        result.put("latPos", _latPos);
        result.put("lngPos", _lngPos);
        result.put("placeName", _placeName);
        result.put("answersIDs", _answersIDs);
        result.put("enquireIDsCount", _enquireIDsCount);
        return result;
    }
}
