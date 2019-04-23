package com.fireinsidethemountain.whereto.model;

public class User {

    private String _username;

    private String _email;

    public User (final String username, final String email) {
        _username = username;
        _email = email;
    }

    public final String getUsername () {
        return "Username: " + _username;
    }

    public final String getEmail() {
        return "Email: " + _email;
    }

}
