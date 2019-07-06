package com.fireinsidethemountain.whereto.ui;

import android.content.res.Resources;
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
import android.widget.Adapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;


public class Module extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView _recyclerView;
    private RecyclerViewAdapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;
    private DatabaseReference _foodEnquiresReference = FirebaseDatabase.getInstance().getReference("Enquires");
    private MapScreen _mapScreen;
    private Module _moduleFragment = this;
    private Enquire.EnquireType _moduleType;
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
    private Resources _resources;


    public static Module newIstance(int moduleType) {
        Module module = new Module();
        Bundle args = new Bundle();
        args.putInt("moduleType", moduleType);
        module.setArguments(args);
        return module;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               final int position, long id) {

        DatabaseReference foodEnquiresReference = FirebaseDatabase.getInstance().getReference("Enquires");
        Log.d("tag", "onComplete: kurwaaaaaa");
        foodEnquiresReference.removeEventListener(_currentListener);
        if (position != 4) {
            _currentListener = foodEnquiresReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    _dataset = new ArrayList<>();
                    _enquiresIDs = new ArrayList<>();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Enquire e = childSnapshot.getValue(Enquire.class);
                        if (e.getType() == Enquire.EnquireType.Food) {
                            Date date = e.getCreationDate();
                            Date now = new Date();
                            long diff = now.getTime() - date.getTime();
                            long diffHours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
                            switch (position) {
                                case 1:
                                    if (diffHours <= 24) {
                                        _dataset.add(e.toString());
                                    }
                                    break;
                                case 2:
                                    if (diffHours <= 7 * 24) {
                                        _dataset.add(e.toString());
                                    }
                                    break;
                                case 3:
                                    if (diffHours <= 30 * 24) {
                                        _dataset.add(e.toString());
                                    }
                                    break;
                                case 0:
                                    _dataset.add(e.toString());
                                    break;
                            }
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

                }
            });
        } else {
            Log.d("tag", "onComplete: KURRR");
            _currentListener = foodEnquiresReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    _dataset = new ArrayList<>();
                    ArrayList<Enquire> enquires = new ArrayList<>();
                    _enquiresIDs = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Enquire e = childSnapshot.getValue(Enquire.class);
                        if (e.getType() == Enquire.EnquireType.Food) {
                            enquires.add(e);
                        }
                    }
                    Collections.sort(enquires);
                    for (Enquire e : enquires) {
                        _dataset.add(e.toString());
                        _enquiresIDs.add(e.getEnquireID());
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
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    private class FoodModuleItemClickListener implements  RecyclerViewAdapter.ItemClickListener {
        @Override
        public void onItemClick(View v,int pos) {
            _enquireViewFragment.setEnquire(_enquiresIDs.get(pos), _dataset.get(pos));
            _enquireViewFragment.setPreviousFragment(_moduleFragment);
            _mainScreen.setCurrentFragment(_enquireViewFragment);
        }
    }

    private class FoodModuleAllEnquiresEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            _dataset = new ArrayList<>();
            _enquiresIDs = new ArrayList<>();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                Enquire e = childSnapshot.getValue(Enquire.class);
                if (e.getType() == Enquire.EnquireType.Food) {
                    _dataset.add(e.toString());
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


    private FoodModuleItemClickListener _foodModuleItemClickListener = new FoodModuleItemClickListener();
    private FoodModuleAllEnquiresEventListener _foodModuleAllEnquiresEventListener = new FoodModuleAllEnquiresEventListener();

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
        _moduleType = Enquire.EnquireType.values()[getArguments().getInt("moduleType")];
        _spinnerPaths = new String[]{
                getResources().getString(R.string.All),
                getResources().getString(R.string.this_day),
                getResources().getString(R.string.this_week),
                getResources().getString(R.string.this_month),
                getResources().getString(R.string.top_spinner)};
        _mainScreen = (MainScreen) getActivity();
        _resources = getResources();
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
    }
}