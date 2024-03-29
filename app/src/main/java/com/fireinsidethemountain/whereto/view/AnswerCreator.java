package com.fireinsidethemountain.whereto.view;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.controller.ProgramClient;
import com.fireinsidethemountain.whereto.util.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class AnswerCreator extends Fragment implements View.OnClickListener {

    FragmentManager _fragmentManager;
    FragmentTransaction _fragmentTransaction;
    AutocompleteSupportFragment _autocompleteFragment;
    private final LatLng LODZ_REGION_LEFT_CORNER = new LatLng(51.846180, 19.260541);
    private final LatLng LODZ_REGION_RIGHT_CORNER = new LatLng(51.846180, 19.260541);
    private MapScreen _mapScreen;
    private Button _postAnswer;
    private MainScreen _mainScreen;
    private Place _currentPlace;
    private ProgramClient _programClient = ProgramClient.getInstance();
    private Fragment _previousFragment;
    private String _currentEnquireID;
    private AlphaAnimation _buttonClick = new AlphaAnimation(1f, 0.8f);
    private TextView _selectedPlace;


    public String getCurrentEnquireID() {
        return _currentEnquireID;
    }

    public void setPreviousFragment(Fragment previousFragment) {
        _previousFragment = previousFragment;
    }

    public Fragment getPreviousFragment() {
        return _previousFragment;
    }

    public void setCurrentEnquireID(String currentEnquireID) {
        _currentEnquireID = currentEnquireID;
    }


    public Place getCurrentPlace() {
        return _currentPlace;
    }

    public void setCurrentPlace(Place place) {
        _currentPlace = place;
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.answer_creator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _mainScreen = (MainScreen) getActivity();
        _selectedPlace = view.findViewById(R.id.selectedPlace);
        _selectedPlace.setText(getResources().getString(R.string.selectedPlace) + ": " + getResources().getString(R.string.nothing));
        _buttonClick.setDuration(300);
        _postAnswer = view.findViewById(R.id.postAnswer);
        _postAnswer.setOnClickListener(this);
        _fragmentManager = getChildFragmentManager();
        _mapScreen = (MapScreen) getParentFragment();
        _autocompleteFragment = (AutocompleteSupportFragment) _fragmentManager.findFragmentById(R.id.autocomplete_fragment);
        _autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                LODZ_REGION_LEFT_CORNER,
                LODZ_REGION_RIGHT_CORNER));
        _autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        _autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                _selectedPlace.setText(getResources().getString(R.string.selectedPlace) + ": " + place.getName());
                _mapScreen.moveCamera(place.getLatLng(), Constants.PLACE_PICKER_ZOOM);
                _currentPlace = place;
            }

            @Override
            public void onError(Status status) {
                Log.i("Error: ", "An error occurred: " + status);
            }
        });
    }


    public void resetAnswerCreator() {
        _selectedPlace.setText(getResources().getString(R.string.selectedPlace) + ": " + getResources().getString(R.string.nothing));
        _currentPlace = null;
        _currentEnquireID = null;
    }

    public AutocompleteSupportFragment getAutocompleteFragment() {
        return _autocompleteFragment;
    }

    @Override
    public void onClick(View view) {
        if (view == _postAnswer) {
            view.startAnimation(_buttonClick);
            if (_currentPlace == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.answerChoose), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.youHaveChosen) + " "+ _currentPlace.getName() + " "+ getResources().getString(R.string.asAnAnswer), Toast.LENGTH_SHORT).show();
                _programClient.addNewAnswer(_currentEnquireID, _currentPlace.getId(), _currentPlace.getName(), _currentPlace.getLatLng());
                _mainScreen.setCurrentFragment(_mainScreen.getEnquireViewFragment().getPreviousFragment());
            }
        }
    }
}