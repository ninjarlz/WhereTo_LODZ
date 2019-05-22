package com.fireinsidethemountain.whereto.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.util.Constants;
import com.fireinsidethemountain.whereto.model.User;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import org.w3c.dom.Text;
import java.util.ArrayList;

public class MainScreen extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private boolean _locationPermissionGranted = false;
    private MapView _mapView;
    private FirebaseAuth _auth = FirebaseAuth.getInstance();
    private User _currentUser;
    private TextView _username;
    private TextView _email;
    private FusedLocationProviderClient _fusedLocationClient;
    private Button _logOut;
    private ActionBarDrawerToggle _toggle;
    private DrawerLayout _drawerLayout;
    private View _header;

    private void initGoogleMaps(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        _mapView = (MapView) findViewById(R.id.map);
        _mapView.onCreate(mapViewBundle);
        _mapView.getMapAsync(this);
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        _fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location == null) {
                        return;
                    }
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("tag", "onComplete: latitude: " + latLng.latitude);
                    Log.d("tag", "onComplete: longtitude: " + latLng.longitude);

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (_toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _toggle = new ActionBarDrawerToggle(this, _drawerLayout, R.string.open, R.string.close);
        _drawerLayout.addDrawerListener(_toggle);
        _toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _header = getLayoutInflater().inflate(R.layout.header, null);
        initGoogleMaps(savedInstanceState);
        _username = (TextView) findViewById(R.id.username);
        _email = (TextView) findViewById(R.id.email);
        _logOut = (Button) findViewById(R.id.sign_out);
        _logOut.setOnClickListener(this);
        String email = _auth.getCurrentUser().getEmail();
        _currentUser = new User(email, email);
        _username.setText(_currentUser.getUsername());
        _email.setText(_currentUser.getEmail());
        FacebookSdk.sdkInitialize(getApplicationContext());
    }


    private boolean checkMapServices() {
        if (isServicesOk() && isGPSEnabled())
            return true;
        return false;
    }

    public boolean isServicesOk()
    {
        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        int available = gaa.isGooglePlayServicesAvailable(MainScreen.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            return true;
        }
        else if (gaa.isUserResolvableError(available)) {
            // an error occured but it could be resolved
            Dialog dialog = gaa.getErrorDialog(MainScreen.this, available, Constants.ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(MainScreen.this, "Cannot make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isGPSEnabled()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, you have to enable it.");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableGpsIntent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS);
                //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (!_locationPermissionGranted) {
                    getLocationPermission();
                } else{
                    getLastKnownLocation();
                }
                // else DO STH
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        _locationPermissionGranted = false;
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // if request is cancelled, the result array is empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _locationPermissionGranted = true;
                }
            }
        }
    }

    private void getLocationPermission()
    {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            _locationPermissionGranted = true;
            // DO STH;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == _logOut) {
            _auth.signOut();
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken(null);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _mapView.onResume();
        if (checkMapServices() && _locationPermissionGranted) {
            // Do STH
            getLastKnownLocation();
        } else {
            getLocationPermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        _mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        _mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    protected void onPause() {
        _mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        _mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        _mapView.onLowMemory();
    }
}
