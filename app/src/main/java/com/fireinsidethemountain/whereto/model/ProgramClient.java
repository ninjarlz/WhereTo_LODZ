package com.fireinsidethemountain.whereto.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramClient {

    private static ProgramClient _instance = null;
    private List<Enquire> _currentlyLoadedEnquires = new ArrayList<>();
    private final int ENQUIRES_ON_ONE_PAGE = 10;
    private User _currentUser;
    private DatabaseReference _databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth _firebaseAuth = FirebaseAuth.getInstance();

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

    public void writeNewPost() {
        if (_currentUser != null) {
            Enquire enquire = new Enquire(_currentUser.getUserID(), _currentUser.getUsername(), Enquire.EnquireType.Events ,  "Where can I find a wild party tonight?", Calendar.getInstance().getTime(), new ArrayList<Integer>() , 0);
            String key = _databaseReference.child("Enquires").push().getKey();
            Map<String, Object> enquireValues = enquire.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Enquires/" + key, enquireValues);
            //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
            _databaseReference.updateChildren(childUpdates);
            Log.d("tag","onComplete: DATABSE 1");
    }
    }
}
