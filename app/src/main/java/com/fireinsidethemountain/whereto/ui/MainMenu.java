package com.fireinsidethemountain.whereto.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.ProgramClient;
import com.fireinsidethemountain.whereto.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends Fragment implements View.OnClickListener {

    private ProgramClient _programClient = ProgramClient.getInstance();
    private FirebaseAuth _auth = FirebaseAuth.getInstance();
    private Button _logOut;
    private Button _database;
    private Button _app;
    private Button _aboutLodz;
    private Button _settings;
    private AlphaAnimation _buttonClick = new AlphaAnimation(1f, 0.8f);
    private TextView _email;
    private TextView _username;
    private User _currentUser;
    private MainScreen _mainScreen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _logOut = view.findViewById(R.id.sign_out);
        _logOut.setOnClickListener(this);
        _logOut = view.findViewById(R.id.sign_out);
        _logOut.setOnClickListener(this);
        _database = view.findViewById(R.id.database);
        _database.setOnClickListener(this);
        _app = view.findViewById(R.id.this_app);
        _app.setOnClickListener(this);
        _aboutLodz = view.findViewById(R.id.about);
        _aboutLodz.setOnClickListener(this);
        _settings = view.findViewById(R.id.settings);
        _settings.setOnClickListener(this);
        _buttonClick.setDuration(300);

        _username = (TextView) view.findViewById(R.id.username);
        _email = (TextView) view.findViewById(R.id.email);

        String email = _auth.getCurrentUser().getEmail();
        String username = _auth.getCurrentUser().getDisplayName();
        String id = _auth.getCurrentUser().getUid();
        _currentUser = new User(id, email, email);
        _programClient.logInUser(_currentUser);
        _username.setText(getResources().getString(R.string.username) + ": " + _currentUser.getUsername());
        _email.setText("Email: " + _currentUser.getEmail());
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        _mainScreen = (MainScreen) getActivity();
    }

    @Override
    public void onClick(View view) {
        if (view == _logOut) {
            _auth.signOut();
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken(null);
            view.startAnimation(_buttonClick);
            getActivity().finish();
        } else if (view == _database) {
            view.startAnimation(_buttonClick);
            Log.d("tag", "onComplete: kurwa1");
            EnquireCreator enquireCreator = _mainScreen.getEnquireCreatorFragment();
            enquireCreator.setPreviousFragment(this);
            enquireCreator.resetEnquireContent();
            _mainScreen.setCurrentFragment(enquireCreator);
            //_programClient.writeNewPost();
        }  else if (view == _app) {
            Log.d("tag", "onComplete: kurwa2");
            view.startAnimation(_buttonClick);
        } else if (view == _aboutLodz) {
            view.startAnimation(_buttonClick);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.wiki)));
            startActivity(browserIntent);
        } else if (view == _settings) {
            view.startAnimation(_buttonClick);
        }
    }
}
