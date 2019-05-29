package com.fireinsidethemountain.whereto.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String userID;
    private String username;
    private String email;

    public User (final String userID, final String username, final String email) {
        this.userID = userID;
        this.username = username;
        this.email = email;
    }

    public final String getUsername () {
        return username;
    }

    public final String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }
}
