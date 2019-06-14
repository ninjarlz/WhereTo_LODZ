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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fireinsidethemountain.whereto.BuildConfig;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.AnsweredPlace;
import com.fireinsidethemountain.whereto.model.Enquire;
import com.fireinsidethemountain.whereto.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapScreen extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private boolean _locationPermissionGranted = false;
    private MapView _mapView;
    private GoogleMap _map;
    private FusedLocationProviderClient _fusedLocationClient;
    private boolean _mapIsReady;
    private Fragment _mainMenu = new MainMenu();
    private Fragment _answerCreator = new AnswerCreator();
    private Fragment _currentFragment;
    private LatLng _yourPos;
    private FragmentManager _fragmentManager;
    private FragmentTransaction _fragmentTransaction;
    private List<Marker> _markers;
    private DatabaseReference _answeredPlacesReference = FirebaseDatabase.getInstance().getReference("AnsweredPlaces");
    private PlacesClient _placesClient;
    private Place _place;

    public Fragment getMainMenu() {
        return _mainMenu;
    }

    public void setMainMenu(Fragment mainMenu) {
        _mainMenu = mainMenu;
    }

    public Fragment getAnswerCreator() {
        return _answerCreator;
    }

    public void setAnswerCreator(Fragment answerCreator) {
        _answerCreator = answerCreator;
    }

    public Fragment getCurrentFragment() {
        return _currentFragment;
    }

    public void setCurrentFragment(Fragment fragment) {
        if (fragment != null && fragment != _currentFragment) {
            _fragmentTransaction = _fragmentManager.beginTransaction();
            _fragmentTransaction.hide(_currentFragment);
            _currentFragment = fragment;
            if (_currentFragment == _answerCreator) {
                getLastKnownLocation();
                AnswerCreator answerCreator = (AnswerCreator) _answerCreator;
                answerCreator.ShowAutocomplete(true);
                answerCreator.getAutocompleteFragment().setText("");
                setMarkersVisible(false);
            } else {
                ((AnswerCreator) _answerCreator).ShowAutocomplete(false);
                setMarkersVisible(true);
            }
            getLastKnownLocation();
            _fragmentTransaction.show(_currentFragment);
            _fragmentTransaction.commit();
        }
    }

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



    public void getLastKnownLocation() {

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
                    _yourPos = new LatLng(location.getLatitude(), location.getLongitude());
                    moveCamera(_yourPos, Constants.DEFAULT_ZOOM);

                }
            }
        });

    }

    public void moveCamera(LatLng latLng, float zoom) {
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
            if (_currentFragment != _answerCreator) {
                getLastKnownLocation();
            }
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
        // Initialize Places.
        Places.initialize(getActivity(), BuildConfig.GoogleSecAPIKEY);
        _placesClient = Places.createClient(getActivity());
        initGoogleMaps(savedInstanceState, view);
        _fragmentManager = getChildFragmentManager();
        _fragmentTransaction = _fragmentManager.beginTransaction();
        _fragmentTransaction.add(R.id.map_screen_fragment_container, _mainMenu);
        _fragmentTransaction.add(R.id.map_screen_fragment_container, _answerCreator);
        _currentFragment = _mainMenu;
        _fragmentTransaction.show(_mainMenu);
        _fragmentTransaction.commit();
        //Log.d("CurrentState: ", _currentFragment == _answerCreator ? "ANSWER" : "MAIN");
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onResume() {
        super.onResume();
        _mapView.onResume();
        if (_mapIsReady) {
            if (checkMapServices() && _locationPermissionGranted &&
            _currentFragment != _answerCreator) {
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
        _map = map;
        _answeredPlacesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                _map.clear();
                _markers = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    AnsweredPlace p = childSnapshot.getValue(AnsweredPlace.class);
                    String placeID = childSnapshot.getKey();
                    addMarkerAt(new LatLng(p.getLatPos(), p.getLngPos()), p.getMostPopularEnquireType(), p.getPlaceName(), p.getMostPopularEnquireContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //AddMarkerAt(new LatLng(51.756269, 19.460543), Enquire.EnquireType.Food);
        getLocationPermission();
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
            if (checkMapServices() && _locationPermissionGranted &&
                _currentFragment != _answerCreator) {
                // Do STH
                getLastKnownLocation();
            } else {
                getLocationPermission();
            }
        }
    }

    private void addMarkerAt(LatLng pos, Enquire.EnquireType type, String placeName,String enquireContent) {

        float colour = 0f;

        switch (type)
        {
            case Events:
                colour = Constants.EVENTS_RED;
                break;
            case Facilities:
                colour = Constants.FACILITIES_BROWN;
                break;
            case Food:
                colour = Constants.FOOD_GREEN;
                break;
            case Accomodation:
                colour = Constants.STAY_BLUE;
                break;
        }
        Marker marker = _map.addMarker(new MarkerOptions()
                .position(pos)
                .title(placeName)
                .snippet(enquireContent)
                .icon(BitmapDescriptorFactory.defaultMarker(colour)));
        if (_currentFragment == _answerCreator) {
            marker.setVisible(false);
        }
        _markers.add(marker);
    }


    public void setMarkersVisible(boolean isVisible) {
        for (Marker marker : _markers) {
            marker.setVisible(isVisible);
        }
    }

    /*Place getPlace(String placeID) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeID, placeFields).build();
        _placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                _place = response.getPlace();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    _place = null;
                }
            }
        });
        return _place;
    }*/


}
