package com.fireinsidethemountain.whereto.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.fireinsidethemountain.whereto.model.Answer;
import com.fireinsidethemountain.whereto.model.AnsweredPlace;
import com.fireinsidethemountain.whereto.model.Enquire;
import com.fireinsidethemountain.whereto.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Calendar;
import java.util.HashMap;
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

    /*public void addNewAnswer(final String enquireID, final String placeID, final String placeName, final LatLng placePosition) {
        Log.d("tag","onComplete: IIIIIIII111");
        _databaseReference.runTransaction(new Transaction.Handler() {

            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                MutableData enquireData = mutableData.child("Enquires").child(enquireID);
                Enquire enquire = enquireData.getValue(Enquire.class);
                if (enquire == null) {
                    Log.d("tag","onComplete: KUUURRRRRWAAJJAJAJA");
                    return Transaction.success(mutableData);
                }
                Log.d("tag","onComplete: IIIIIIII");
                enquire.setAnswerCount(enquire.getAnswerCount() + 1);
                Answer answer = new Answer(enquireID, enquire.getContent(), placeID, enquire.getAuthorID(), enquire.getAuthorID());
                DatabaseReference answers = _databaseReference.child("Answers");
                String answerID = answers.push().getKey();
                Map<String, Object> answerValues = answer.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Answers/" + answerID, answerValues);
                _databaseReference.updateChildren(childUpdates);
                enquire.getAnswersIDs().put(answerID, answerID);
                // Set value and report transaction success
                enquireData.setValue(enquire);
                AnsweredPlace answeredPlace;
                Map<String, String> answersIDs;
                Map<String, Integer> enquiresIDsCount;
                for (MutableData answeredPlaceData : mutableData.getChildren()) {
                    if (answeredPlaceData.getKey().equals(placeID)) {
                        answeredPlace = answeredPlaceData.getValue(AnsweredPlace.class);
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
                        Enquire mostPopularEnquire = mutableData.child("Enquires").child(mostPopularEnquireID).getValue(Enquire.class);
                        answeredPlace.setMostPopularEnquireContent(mostPopularEnquire.getContent());
                        answeredPlace.setPlaceName(placeName);
                        answeredPlace.setLatPos(placePosition.latitude);
                        answeredPlace.setLngPos(placePosition.longitude);
                        answeredPlace.setMostPopularEnquireType(mostPopularEnquire.getType());
                        answeredPlaceData.setValue(answeredPlace);
                        return Transaction.success(mutableData);
                    }
                }
                answeredPlace = new AnsweredPlace(enquireID, enquire.getType(), enquire.getContent(), placeName, placePosition.latitude, placePosition.longitude);
                answersIDs = answeredPlace.getAnswersIDs();
                answersIDs.put(answerID, enquireID);
                enquiresIDsCount = answeredPlace.getEnquireIDsCount();
                enquiresIDsCount.put(enquireID, 1);
                mutableData.child(placeID).setValue(answeredPlace);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }*/



    // TODO: NEW CONCURRENT SAFE VERSION OF POSTING ANSWERS
    public void addNewAnswer(final String enquireID, final String placeID, final String placeName, final LatLng placePosition) {
        Log.d("tag","onComplete: IIIIIIII111");
        DatabaseReference enquireRef = _databaseReference.child("Enquires");
        enquireRef.runTransaction(new Transaction.Handler() {

            private String _answerID;
            private Enquire _enquire;
            private MutableData _enquiresData;

            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                _enquiresData = mutableData;
                MutableData enquireData = mutableData.child(enquireID);
                _enquire = enquireData.getValue(Enquire.class);
                if (_enquire == null) {
                    Log.d("tag","onComplete: KUUURRRRRWAAJJAJAJA");
                    return Transaction.success(mutableData);
                }
                Log.d("tag","onComplete: IIIIIIII");
                _enquire.setAnswerCount(_enquire.getAnswerCount() + 1);
                Answer answer = new Answer(enquireID, _enquire.getContent(), placeID, _enquire.getAuthorID(), _enquire.getAuthorID());
                DatabaseReference answers = _databaseReference.child("Answers");
                _answerID = answers.push().getKey();
                Map<String, Object> answerValues = answer.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Answers/" + _answerID, answerValues);
                _databaseReference.updateChildren(childUpdates);
                _enquire.getAnswersIDs().put(_answerID, _answerID);

                // Set value and report transaction success
                enquireData.setValue(_enquire);

                DatabaseReference answeredPlacesRef = _databaseReference.child("AnsweredPlaces");
                answeredPlacesRef.runTransaction(new Transaction.Handler() {

                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        AnsweredPlace answeredPlace;
                        Map<String, String> answersIDs;
                        Map<String, Integer> enquiresIDsCount;
                        for (MutableData answeredPlaceData : mutableData.getChildren()) {
                            if (answeredPlaceData.getKey().equals(placeID)) {
                                answeredPlace = answeredPlaceData.getValue(AnsweredPlace.class);
                                answersIDs = answeredPlace.getAnswersIDs();

                                answersIDs.put(_answerID, enquireID);

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
                                Enquire mostPopularEnquire = _enquiresData.child(mostPopularEnquireID).getValue(Enquire.class);
                                answeredPlace.setMostPopularEnquireContent(mostPopularEnquire.getContent());
                                answeredPlace.setPlaceName(placeName);
                                answeredPlace.setLatPos(placePosition.latitude);
                                answeredPlace.setLngPos(placePosition.longitude);
                                answeredPlace.setMostPopularEnquireType(mostPopularEnquire.getType());
                                answeredPlaceData.setValue(answeredPlace);
                                return Transaction.success(mutableData);
                            }
                        }
                        answeredPlace = new AnsweredPlace(enquireID, _enquire.getType(), _enquire.getContent(), placeName, placePosition.latitude, placePosition.longitude);
                        answersIDs = answeredPlace.getAnswersIDs();
                        answersIDs.put(_answerID, enquireID);
                        enquiresIDsCount = answeredPlace.getEnquireIDsCount();
                        enquiresIDsCount.put(enquireID, 1);
                        mutableData.child(placeID).setValue(answeredPlace);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    // TODO: NOT SAFE VERSION OF POSTING ANSWERS
    /*public void addNewAnswer(final String enquireID, final String placeID, final String placeName, final LatLng placePosition) {
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
    }*/
}
