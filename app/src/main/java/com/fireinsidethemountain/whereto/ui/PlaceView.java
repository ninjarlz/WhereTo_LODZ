package com.fireinsidethemountain.whereto.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.Enquire;
import com.fireinsidethemountain.whereto.model.RecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlaceView extends Fragment  {
    /*private RecyclerView _recyclerView;
    private RecyclerViewAdapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;
    private DatabaseReference _answeredPlacesReference = FirebaseDatabase.getInstance().getReference("AnsweredPlaces");
    private MapScreen _mapScreen;
    private PlaceView _foodModuleFragment = this;
    private MainScreen _mainScreen;
    private List<String> _enquiresIDs;
    private Spinner _spinner;
    private String[] _spinnerPaths;
    private LayoutInflater _inflater;
    private Button _allButton;
    private Button _topButton;
    private Button _addEnquireButton;
    private AlphaAnimation _buttonClick = new AlphaAnimation(1f, 0.8f);
    private EnquireView _enquireViewFragment;
    private List<String> _dataset;
    private ValueEventListener _currentListener;




    private class PlaceViewItemClickListener implements  RecyclerViewAdapter.ItemClickListener {
        @Override
        public void onItemClick(View v,int pos) {
            _enquireViewFragment.setEnquire(_enquiresIDs.get(pos), _dataset.get(pos));
            _enquireViewFragment.setPreviousFragment(_foodModuleFragment);
            _mainScreen.setCurrentFragment(_enquireViewFragment);
        }
    }

    private class PlaceViewAllAssingedEnquiresEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            _dataset = new ArrayList<>();
            _enquiresIDs = new ArrayList<>();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                Enquire e = childSnapshot.getValue(Enquire.class);
                if (e.getType() == Enquire.EnquireType.Food) {
                    _dataset.add(e.toString(getActivity()));
                    _enquiresIDs.add(childSnapshot.getKey());
                }
            }
            Collections.reverse(_enquiresIDs);
            Collections.reverse(_dataset);
            _adapter = new RecyclerViewAdapter(_inflater, _dataset);
            _recyclerView.setAdapter(_adapter);
            _adapter.setClickListener(_foodModuleItemClickListener);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            databaseError.toException().printStackTrace();
        }
    }


    private PlaceViewItemClickListener _foodModuleItemClickListener = new PlaceViewItemClickListener();
    private PlaceViewAllAssingedEnquiresEventListener _foodModuleAllEnquiresEventListener = new PlaceViewAllAssingedEnquiresEventListener();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _inflater = inflater;
        return inflater.inflate(R.layout.food_module, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _spinnerPaths = new String[]{
                getResources().getString(R.string.All),
                getResources().getString(R.string.this_day),
                getResources().getString(R.string.this_week),
                getResources().getString(R.string.this_month),
                getResources().getString(R.string.top_spinner)};
        _mainScreen = (MainScreen) getActivity();
        _mapScreen = _mainScreen.getMapScreenFragment();
        _enquireViewFragment = _mainScreen.getEnquireViewFragment();
        _recyclerView = view.findViewById(R.id.food_recycler_view);
        _recyclerView.setHasFixedSize(true);
        _layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(_layoutManager);
        _currentListener = _foodEnquiresReference.addValueEventListener(_foodModuleAllEnquiresEventListener);
        _spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, _spinnerPaths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinner.setAdapter(adapter);
        _spinner.setOnItemSelectedListener(this);
        _topButton = view.findViewById(R.id.top);
        _topButton.setOnClickListener(this);
        _allButton = view.findViewById(R.id.all);
        _allButton.setOnClickListener(this);
        _addEnquireButton = view.findViewById(R.id.add_enquire);
        _addEnquireButton.setOnClickListener(this);
        _buttonClick.setDuration(300);
    }

    @Override
    public void onClick(View view) {
        if (view == _topButton) {
            view.startAnimation(_buttonClick);
            _spinner.setSelection(4);
        } else if (view == _allButton) {
            view.startAnimation(_buttonClick);
            _spinner.setSelection(0);
        } else if (view == _addEnquireButton) {
            EnquireCreator enquireCreator = _mainScreen.getEnquireCreatorFragment();
            enquireCreator.setPreviousFragment(this);
            enquireCreator.getSpinner().setSelection(0);
            enquireCreator.resetEnquireContent();
            _mainScreen.setCurrentFragment(enquireCreator);
            view.startAnimation(_buttonClick);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

        }
    }*/
}
