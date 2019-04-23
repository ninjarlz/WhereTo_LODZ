package com.fireinsidethemountain.whereto.ui;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fireinsidethemountain.whereto.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInScreen extends AppCompatActivity implements View.OnClickListener{

    private Button _logIn, _registration;
    private EditText _email, _password;
    private FirebaseAuth _auth = FirebaseAuth.getInstance();
    private Intent _mainScreen;
    private ProgressDialog _progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);
        _mainScreen = new Intent(this, MainScreen.class);
        _registration = findViewById(R.id.register);
        _email = findViewById(R.id.email);
        _logIn = findViewById(R.id.logIn);
        _password = findViewById(R.id.password);
        _progressDialog = new ProgressDialog(this);

        _registration.setOnClickListener(this);
        _logIn.setOnClickListener(this);

    }

    private void registerUser() {
        final String email = _email.getText().toString().trim();
        final String password = _password.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(LogInScreen.this, "Please, enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LogInScreen.this, "Please, enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6)
        {
            Toast.makeText(LogInScreen.this, "Password is too short", Toast.LENGTH_SHORT).show();
            return;
        }

        _progressDialog.setMessage("Registering user...");
        _progressDialog.show();

        _auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LogInScreen.this, "Sing up error!", Toast.LENGTH_SHORT).show();
                    _progressDialog.dismiss();
                } else {
                    String userId = _auth.getCurrentUser().getUid();
                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    currentUserDb.setValue(true);
                    _progressDialog.dismiss();
                    finish();
                    startActivity(_mainScreen);
                }

            }
        });
    }

    private void logInUser()
    {
        final String email = _email.getText().toString().trim();
        final String password = _password.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(LogInScreen.this, "Please, enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LogInScreen.this, "Please, enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        _progressDialog.setMessage("Logging user...");
        _progressDialog.show();

        _auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LogInScreen.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LogInScreen.this, "Sing in error!", Toast.LENGTH_SHORT).show();
                    _progressDialog.dismiss();
                } else {
                    _progressDialog.dismiss();
                    finish();
                    startActivity(_mainScreen);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == _registration) {
            registerUser();
        }
        else if (view == _logIn) {
            logInUser();
        }
    }





}
