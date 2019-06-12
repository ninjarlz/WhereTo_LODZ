package com.fireinsidethemountain.whereto.ui;
import com.fireinsidethemountain.whereto.BuildConfig;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.util.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
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
import android.widget.Button;
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
    private boolean _isButtonVisible;
    private Place _currentPlace;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.answer_creator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _postAnswer = view.findViewById(R.id.postAnswer);
        _postAnswer.setOnClickListener(this);
        _postAnswer.setVisibility(View.GONE);
        // Initialize Places.
        Places.initialize(getActivity(), BuildConfig.GoogleSecAPIKEY);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity());
        _fragmentManager = getChildFragmentManager();
        _mapScreen = (MapScreen) getParentFragment();
        _autocompleteFragment = (AutocompleteSupportFragment) _fragmentManager.findFragmentById(R.id.autocomplete_fragment);
        _fragmentTransaction = _fragmentManager.beginTransaction();
        _fragmentTransaction.hide(_autocompleteFragment);
        _fragmentTransaction.commit();
        _autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                LODZ_REGION_LEFT_CORNER,
                LODZ_REGION_RIGHT_CORNER));
        _autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        _autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                _mapScreen.moveCamera(place.getLatLng(), Constants.PLACE_PICKER_ZOOM);
                _currentPlace = place;
            }

            @Override
            public void onError(Status status) {
                Log.i("Error: ", "An error occurred: " + status);
            }
        });
    }


    public void ShowAutocomplete(boolean isVisible) {
        if (isVisible) {
            if (!_isButtonVisible) {
                _postAnswer.setVisibility(View.VISIBLE);
                _isButtonVisible = true;
            }
            _currentPlace = null;
            _fragmentTransaction = _fragmentManager.beginTransaction();
            _fragmentTransaction.show(_autocompleteFragment);
            _fragmentTransaction.commit();
        } else {
            _fragmentTransaction = _fragmentManager.beginTransaction();
            _fragmentTransaction.hide(_autocompleteFragment);
            _fragmentTransaction.commit();
        }
    }

    public AutocompleteSupportFragment getAutocompleteFragment() {
        return _autocompleteFragment;
    }

    @Override
    public void onClick(View view) {
        if (view == _postAnswer) {
            if (_currentPlace == null) {
                Toast.makeText(getContext(), "You have to choose a place to answer", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "You have choosen " + _currentPlace.getName(), Toast.LENGTH_LONG).show();
            }
        }
    }
}