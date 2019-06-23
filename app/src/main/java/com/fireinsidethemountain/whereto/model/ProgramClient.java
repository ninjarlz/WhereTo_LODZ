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
    private List<Enquire> _currentlyLoadedEnquires = new ArrayList<>();
    private final int ENQUIRES_ON_ONE_PAGE = 10;
    private User _currentUser;
    private FirebaseDatabase _database = FirebaseDatabase.getInstance();
    private DatabaseReference _databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth _firebaseAuth = FirebaseAuth.getInstance();
    public static boolean _loggedViaFb = false;

    private ProgramClient() {

    }

    public static ProgramClient getInstance() {
        if (_instance == null) {
             _instance = new ProgramClient();
        }
        return _instance;
    }


    public boolean add(Enquire enquire) {
        // one is not able to put in the same document once again
        if (_currentlyLoadedEnquires.contains(enquire)) {
            return false;
        }
        return _currentlyLoadedEnquires.add(enquire);
    }

    public List<Enquire> getAllLoadedEnquires() {
        return Collections.unmodifiableList(_currentlyLoadedEnquires);
    }


    public void logInUser(User user) {
        _currentUser = user;
    }

    public void writeNewPost(String content, Enquire.EnquireType type) {
        if (_currentUser != null) {
            Enquire enquire = new Enquire(_currentUser.getUserID(), _currentUser.getUsername(), type ,
                    content,
                    Calendar.getInstance().getTime());
            String key = _databaseReference.child("Enquires").push().getKey();
            Map<String, Object> enquireValues = enquire.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Enquires/" + key, enquireValues);
            _databaseReference.updateChildren(childUpdates);
            Log.d("tag","onComplete: DATABSE 1");
        }
    }

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
                            answeredPlace = dataSnapshot.getValue(AnsweredPlace.class);
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

    public void readPosts(List<String> dataset, Enquire.EnquireType type) {
        switch (type) {
            case Food:

                break;
            case Facilities:

                break;
            case Accomodation:

                break;
            case Events:

                break;
        }
    }
}
