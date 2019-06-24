package com.fireinsidethemountain.whereto.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramClient {

    private static ProgramClient _instance = null;
    private User _currentUser;
    private DatabaseReference _databaseReference = FirebaseDatabase.getInstance().getReference();


    private ProgramClient() {

    }

    public static ProgramClient getInstance() {
        if (_instance == null) {
             _instance = new ProgramClient();
        }
        return _instance;
    }



    public void logInUser(User user) {
        _currentUser = user;
    }

    public void writeNewPost(String content, Enquire.EnquireType type) {
        if (_currentUser != null) {
            String key = _databaseReference.child("Enquires").push().getKey();
            Enquire enquire = new Enquire(key, _currentUser.getUserID(), _currentUser.getUsername(), type ,
                    content,
                    Calendar.getInstance().getTime());

            Map<String, Object> enquireValues = enquire.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Enquires/" + key, enquireValues);
            _databaseReference.updateChildren(childUpdates);
        }
    }

    // TODO: FUTURE FIX FOR POSSIBLE TRANSACTION FUCKUP, BUT CURRENTLY NOT WORKING AT ALL
    /*public void addNewAnswer(final String enquireID, final String placeID, final String placeName, final LatLng placePosition) {
        _databaseReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                MutableData enquireData = mutableData.child("Enquires").child(enquireID);
                final Enquire e = enquireData.getValue(Enquire.class);
                if (e == null) {
                    return Transaction.success(mutableData);
                }

                e.setAnswerCount(e.getAnswerCount() + 1);
                Answer answer = new Answer(enquireID, e.getContent(), placeID, e.getAuthorID(), e.getAuthorID());
                DatabaseReference answers = _databaseReference.child("Answers");
                final String answerID = answers.push().getKey();
                Map<String, Object> answerValues = answer.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Answers/" + answerID, answerValues);
                _databaseReference.updateChildren(childUpdates);
                e.getAnswersIDs().put(answerID, answerID);

                // Set value and report transaction success
                enquireData.setValue(e);

                AnsweredPlace answeredPlace;
                Map<String, String> answersIDs;
                Map<String, Integer> enquiresIDsCount;
                Map<String, Object> answeredPlaceValues;

                MutableData answeredPlacesData = mutableData.child("AnsweredPlaces");

                for (MutableData answeredPlaceData : answeredPlacesData.getChildren()) {
                    String answeredPlaceID = answeredPlaceData.getKey();
                    if (answeredPlaceID.equals(placeID)) {
                        answeredPlace = answeredPlaceData.getValue(AnsweredPlace.class);
                        answersIDs = answeredPlace.getAnswersIDs();
                        answersIDs.put(answerID, enquireID);
                        enquiresIDsCount = answeredPlace.getEnquireIDsCount();
                        if (enquiresIDsCount.containsKey(enquireID)) {
                            enquiresIDsCount.put(enquireID, enquiresIDsCount.get(enquireID) + 1);
                        } else {
                            enquiresIDsCount.put(enquireID, 1);
                        }
                        Set<String> enquiresIDs = enquiresIDsCount.keySet();
                        Pair<String, Integer> max = new Pair<>(null, 0);
                        for (String enquireID1 : enquiresIDs) {
                            Integer current = enquiresIDsCount.get(enquireID1);
                            if (current > max.second) {
                                max = new Pair<>(enquireID1, current);
                            }
                        }
                        String mostPopularEnquireID = max.first;
                        answeredPlace.setMostPopularEnquireID(mostPopularEnquireID);
                        MutableData mostPopularEnquireData = mutableData.child("Enquires").child(mostPopularEnquireID);
                        Enquire mostPopularEnquire = mostPopularEnquireData.getValue(Enquire.class);
                        answeredPlace.setMostPopularEnquireContent(mostPopularEnquire.getContent());
                        answeredPlace.setPlaceName(placeName);
                        answeredPlace.setLatPos(placePosition.latitude);
                        answeredPlace.setLngPos(placePosition.longitude);
                        answeredPlace.setMostPopularEnquireType(mostPopularEnquire.getType());
                        answeredPlaceData.setValue(answeredPlace);
                        return Transaction.success(mutableData);
                    }
                }
                Map<String, Object> childUpdates1 = new HashMap<>();
                answeredPlace = new AnsweredPlace(enquireID, e.getType(), e.getContent(), placeName, placePosition.latitude, placePosition.longitude);
                answersIDs = answeredPlace.getAnswersIDs();
                answersIDs.put(answerID, enquireID);
                enquiresIDsCount = answeredPlace.getEnquireIDsCount();
                enquiresIDsCount.put(enquireID, 1);
                answeredPlaceValues = answeredPlace.toMap();
                childUpdates1.put("/AnsweredPlaces/" + placeID, answeredPlaceValues);
                _databaseReference.updateChildren(childUpdates1);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                                   @Nullable DataSnapshot dataSnapshot) {
                // Transaction completed

            }
        });
    }*/

    public void addNewAnswer(final String enquireID, final String placeID, final String placeName, final LatLng placePosition) {
        final String answerID;
        final DatabaseReference enquire = _databaseReference.child("Enquires").child(enquireID);
        enquire.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                final Enquire e = mutableData.getValue(Enquire.class);
                if (e == null) {
                    return Transaction.success(mutableData);
                }

                e.setAnswerCount(e.getAnswerCount() + 1);
                Answer answer = new Answer(enquireID, e.getContent(), placeID, e.getAuthorID(), e.getAuthorID());
                final DatabaseReference answers = _databaseReference.child("Answers");
                final String answerID = answers.push().getKey();
                Map<String, Object> answerValues = answer.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Answers/" + answerID, answerValues);
                _databaseReference.updateChildren(childUpdates);
                e.getAnswersIDs().put(answerID, answerID);

                // Set value and report transaction success
                mutableData.setValue(e);

                _databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AnsweredPlace answeredPlace;
                        Map<String, String> answersIDs;
                        Map<String, Integer> enquiresIDsCount;
                        Map<String, Object> answeredPlaceValues;
                        Map<String, Object> childUpdates1 = new HashMap<>();

                        DataSnapshot answeredPlaceData = dataSnapshot.child("AnsweredPlaces").child(placeID);

                        if (answeredPlaceData.exists()) {
                            answeredPlace = answeredPlaceData.getValue(AnsweredPlace.class);
                            //Log.d("tag","onComplete: " + answeredPlace.getMostPopularEnquireID());
                            //Log.d("tag","onComplete: " + answeredPlace.getPlaceName());
                            //Log.d("tag","onComplete: " + answeredPlace.getAnswersIDs().toString());
                            answersIDs = answeredPlace.getAnswersIDs();

                            answersIDs.put(answerID, enquireID);

                            enquiresIDsCount = answeredPlace.getEnquireIDsCount();
                            if (enquiresIDsCount.containsKey(enquireID)) {
                                enquiresIDsCount.put(enquireID, enquiresIDsCount.get(enquireID) + 1);
                                //Log.d("tag","onComplete: KKKKKKKKKKK");
                            } else {
                                enquiresIDsCount.put(enquireID, 1);
                                //Log.d("tag","onComplete: BBBBBBBBBB");
                            }
                            Set<String> enquiresIDs = enquiresIDsCount.keySet();
                            Pair<String, Integer> max = new Pair<>(null, 0);
                            for (String enquireID1 : enquiresIDs) {
                                Integer current = enquiresIDsCount.get(enquireID1);
                                if (current > max.second) {
                                    max = new Pair<>(enquireID1, current);
                                }
                            }
                            String mostPopularEnquireID = max.first;
                            answeredPlace.setMostPopularEnquireID(mostPopularEnquireID);
                            Enquire mostPopularEnquire = dataSnapshot.child("Enquires").child(mostPopularEnquireID).getValue(Enquire.class);
                            answeredPlace.setMostPopularEnquireContent(mostPopularEnquire.getContent());
                            answeredPlace.setPlaceName(placeName);
                            answeredPlace.setLatPos(placePosition.latitude);
                            answeredPlace.setLngPos(placePosition.longitude);
                            answeredPlace.setMostPopularEnquireType(mostPopularEnquire.getType());
                            answeredPlaceValues = answeredPlace.toMap();
                            childUpdates1.put("/AnsweredPlaces/" + placeID, answeredPlaceValues);
                            _databaseReference.updateChildren(childUpdates1);
                        } else {
                            //Log.d("tag","onComplete: IIIIIIII");
                            answeredPlace = new AnsweredPlace(enquireID, e.getType(), e.getContent(), placeName, placePosition.latitude, placePosition.longitude);
                            answersIDs = answeredPlace.getAnswersIDs();
                            answersIDs.put(answerID, enquireID);
                            enquiresIDsCount = answeredPlace.getEnquireIDsCount();
                            enquiresIDsCount.put(enquireID, 1);
                            answeredPlaceValues = answeredPlace.toMap();
                            childUpdates1.put("/AnsweredPlaces/" + placeID, answeredPlaceValues);
                            _databaseReference.updateChildren(childUpdates1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                                   @Nullable DataSnapshot dataSnapshot) {
                // Transaction completed

            }
        });
    }
}
