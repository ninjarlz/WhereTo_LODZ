package com.fireinsidethemountain.whereto.model;
import android.content.res.Resources;

import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.view.MainScreen;

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

    public AnsweredPlace(String mostPopularEnquireID, Enquire.EnquireType mostPopularEnquireType, String mostPopularEnquireContent, String placeName,double latPos, double lngPos) {
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

    public class PlaceNameWithCount implements Comparable<PlaceNameWithCount> {

        private String _placeName;

        private int _enquireCount;

        public double getLat() {
            return _lat;
        }

        public void setLat(double lat) {
            _lat = lat;
        }

        public double getLng() {
            return _lng;
        }

        public void setLng(double lng) {
            _lng = lng;
        }

        private double _lat;
        private double _lng;

        public PlaceNameWithCount(String placeName, int enquireCount) {
            _placeName = placeName;
            _enquireCount = enquireCount;
        }

        @Override
        public int compareTo(PlaceNameWithCount o) {
            return Integer.compare(_enquireCount, o._enquireCount);
        }


        public String toString(boolean withName) {
            Resources resources = MainScreen.getContext().getResources();
            if (withName) {
                return resources.getString(R.string.placeName) + ": " + _placeName + "\n" +
                        resources.getString(R.string.chosenAsAnswer) + " " + new Integer(_enquireCount).toString() + " " +
                        resources.getString(R.string.times);
            }
            return resources.getString(R.string.chosenAsAnswer) + " " + new Integer(_enquireCount).toString() + " " +
                    resources.getString(R.string.times);
        }


    }

    public PlaceNameWithCount getObjectContatiningNumberOfAnswersForEnquire(String enquireID) {
        if (_enquireIDsCount.containsKey(enquireID)) {
            return new PlaceNameWithCount(_placeName, _enquireIDsCount.get(enquireID));
        } else {
            return null;
        }
    }

    public String toString() {
        Resources resources = MainScreen.getContext().getResources();
        String type = "";
        switch (_mostPopularEnquireType) {
            case Events:
                type = resources.getString(R.string.events);
                break;
            case Food:
                type = resources.getString(R.string.food);
                break;
            case Facilities:
                type = resources.getString(R.string.facilities);
                break;
            case Accomodation:
                type = resources.getString(R.string.stay);
                break;
        }

        return resources.getString(R.string.placeName) + ": " + _placeName + "\n" +
                resources.getString(R.string.placeType) + ": " + type;
    }
}
