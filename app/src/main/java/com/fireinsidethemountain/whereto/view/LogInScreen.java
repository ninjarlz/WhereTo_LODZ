package com.fireinsidethemountain.whereto.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.controller.ProgramClient;
import com.fireinsidethemountain.whereto.util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInScreen extends AppCompatActivity implements View.OnClickListener {

    private SignInButton _googleBtn;
    private LoginButton _facebookBtn;
    private FirebaseAuth _auth = FirebaseAuth.getInstance();
    private GoogleSignInClient _googleSignInClient;
    private Intent _mainScreen;
    private ProgressDialog _progressDialog;
    private CallbackManager _callbackManager;
    private AlertDialog _error;
    private ProgramClient _programClient = ProgramClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.signInError));
        builder.setMessage(getResources().getString(R.string.signInErrorMsg));
        builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        _error = builder.create();

        _mainScreen = new Intent(this, MainScreen.class);



        setContentView(R.layout.activity_log_in_screen);
        _googleBtn = findViewById(R.id.google);
        _facebookBtn = findViewById(R.id.facebook);
        _progressDialog = new ProgressDialog(this);

        _googleBtn.setOnClickListener(this);
        _facebookBtn.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("856883367856-4c2vcjpuffk7k94vgv8qu4brff1vgdi4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        _googleSignInClient = GoogleSignIn.getClient(this, gso);

        _callbackManager = CallbackManager.Factory.create();

        _facebookBtn.setReadPermissions("email", "public_profile");

        _facebookBtn.registerCallback(_callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {


            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        if (_auth.getCurrentUser() != null) {
            startActivity(_mainScreen);
        }


    }


    private void logInGoogle() {

        Intent signInIntent = _googleSignInClient.getSignInIntent();
        _progressDialog.setMessage(getResources().getString(R.string.logging));
        _progressDialog.show();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        _callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                _progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(LogInScreen.this, getResources().getString(R.string.signInError), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        _auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            String userId = _auth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            currentUserDb.setValue(true);
                            //currentUserDb.
                            _progressDialog.dismiss();
                            startActivity(_mainScreen);

                        } else {
                            // If sign in fails, display a message to the user.
                            _progressDialog.dismiss();
                            Toast.makeText(LogInScreen.this, getResources().getString(R.string.signInError), Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }


    private void logInFacebook() {
        _progressDialog.setMessage(getResources().getString(R.string.logging));
        _progressDialog.show();
    }

    @Override
    public void onClick(View view) {

        if (view == _facebookBtn) {
            logInFacebook();
        } else if (view == _googleBtn) {
            logInGoogle();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        _googleSignInClient.signOut();
        if (isFbLoggedIn()) {
            facebookLogout();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        _auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            String userId = _auth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            currentUserDb.setValue(true);
                            _progressDialog.dismiss();
                            startActivity(_mainScreen);
                        } else {
                            // If sign in fails, display a message to the user.
                            _progressDialog.dismiss();
                            _error.show();
                            if (isFbLoggedIn()) {
                                facebookLogout();
                            }
                        }

                    }
                });
    }

    public boolean isFbLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void facebookLogout()
    {
        LoginManager.getInstance().logOut();
        AccessToken.setCurrentAccessToken(null);
    }

}
