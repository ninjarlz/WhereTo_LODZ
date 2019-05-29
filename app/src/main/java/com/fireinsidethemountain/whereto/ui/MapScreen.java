package com.fireinsidethemountain.whereto.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.ProgramClient;
import com.fireinsidethemountain.whereto.model.User;
import com.fireinsidethemountain.whereto.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MapScreen extends Fragment implements View.OnClickListener, OnMapReadyCallback {


    private FirebaseAuth _auth = FirebaseAuth.getInstance();
    private boolean _locationPermissionGranted = false;
    private MapView _mapView;
    private GoogleMap _map;
    private FusedLocationProviderClient _fusedLocationClient;
    private boolean _mapIsReady;


    private Button _logOut;
    private Button _database;
    private Button _app;
    private Button _aboutLodz;
    private Button _settings;
    private ProgramClient _programClient = ProgramClient.getInstance();
    private AlphaAnimation _buttonClick = new AlphaAnimation(1f, 0.8f);


    private User _currentUser;
    private TextView _username;
    private TextView _email;



    private void initGoogleMaps(Bundle savedInstanceState, View view) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        _mapView = (MapView) view.findViewById(R.id.map);
        _mapView.onCreate(mapViewBundle);
        _mapView.getMapAsync(this);
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }



    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), Constants.DEFAULT_ZOOM);
                }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }



    private boolean checkMapServices() {
        if (isServicesOk() && isGPSEnabled())
            return true;
        return false;
    }

    public boolean isServicesOk() {
        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        int available = gaa.isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            return true;
        } else if (gaa.isUserResolvableError(available)) {
            // an error occured but it could be resolved
            Dialog dialog = gaa.getErrorDialog(getActivity(), available, Constants.ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Cannot make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This application requires GPS to work properly, you have to enable it.");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableGpsIntent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS);
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (!_locationPermissionGranted) {
                    getLocationPermission();
                } else {
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

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            _locationPermissionGranted = true;
            // DO STH;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.map_screen_module, container, false);
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
        String id = _auth.getCurrentUser().getUid();
        _currentUser = new User(id, email, email);
        _programClient.logInUser(_currentUser);
        _username.setText(_currentUser.getUsername());
        _email.setText(_currentUser.getEmail());
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        initGoogleMaps(savedInstanceState, view);

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
            Log.d("tag", "onComplete: kurwa1");
            _programClient.writeNewPost();
            view.startAnimation(_buttonClick);
        }  else if (view == _app) {
            Log.d("tag", "onComplete: kurwa2");
            view.startAnimation(_buttonClick);
        } else if (view == _aboutLodz) {
            view.startAnimation(_buttonClick);
        } else if (view == _settings) {
            view.startAnimation(_buttonClick);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        _mapView.onResume();
        if (_mapIsReady) {
            if (checkMapServices() && _locationPermissionGranted) {
                // Do STH
                getLastKnownLocation();
            } else {
                getLocationPermission();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        _mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        _mapView.onStop();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        _map = map;
        if (checkMapServices() && _locationPermissionGranted) {
            // Do STH
            getLastKnownLocation();
        } else {
            getLocationPermission();
        }
        _map.setMyLocationEnabled(true);
        _mapIsReady = true;
    }

    @Override
    public void onPause() {
        _mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        _mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        _mapView.onLowMemory();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (checkMapServices() && _locationPermissionGranted) {
                // Do STH
                getLastKnownLocation();
            } else {
                getLocationPermission();
            }
        }
    }

}
