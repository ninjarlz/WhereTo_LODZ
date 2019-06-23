package com.fireinsidethemountain.whereto.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;


import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.AnsweredPlace;
import com.fireinsidethemountain.whereto.model.Enquire;
import com.fireinsidethemountain.whereto.model.ProgramClient;
import com.fireinsidethemountain.whereto.model.RecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EnquireView extends Fragment implements View.OnClickListener {

    private String _enquireID;
    private Enquire _enquire;
    private Fragment _previousFragment;
    private RecyclerView _recyclerView;
    private RecyclerViewAdapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;
    private LayoutInflater _inflater;
    private Button _postAnswer;
    private AlphaAnimation _buttonClick = new AlphaAnimation(1f, 0.8f);
    private DatabaseReference _answeredPlacesReference = FirebaseDatabase.getInstance().getReference("AnsweredPlaces");
    private DatabaseReference _enquireReference;
    private EnquireView _enquireView = this;
    private AnswerCreator _answerCreator;
    private MainScreen _mainScreen;
    private MapScreen _mapScreen;
    private TextView _enquireStats;
    private List<String> _dataset;
    private ValueEventListener _currentListener;


    public Fragment getPreviousFragment() {
        return _previousFragment;
    }

    public void setPreviousFragment(Fragment previousFragment) {
        _previousFragment = previousFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _inflater = inflater;
        return inflater.inflate(R.layout.enquire_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _enquireStats = view.findViewById(R.id.enquireStats);
        _recyclerView = view.findViewById(R.id.enquire_recycler_view);
        _recyclerView.setHasFixedSize(true);
        _layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(_layoutManager);
        _postAnswer = view.findViewById(R.id.postAnswerEnquireView);
        _postAnswer.setOnClickListener(this);
        _buttonClick.setDuration(300);
        _mainScreen = (MainScreen) getActivity();
        _answerCreator = _mainScreen.getAnswerCreatorFragment();
        _mapScreen = _mainScreen.getMapScreenFragment();
    }

    public void setEnquire(String enquireID, String enquireStats) {
        _enquireID = enquireID;
        _enquireStats.setText(enquireStats);
        if (_currentListener != null) {
            _answeredPlacesReference.removeEventListener(_currentListener);
        }
        _currentListener = _answeredPlacesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (_enquireID != null) {
                    _dataset = new ArrayList<>();
                    ArrayList<AnsweredPlace.PlaceNameWithCount> pnwcs = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        AnsweredPlace a = childSnapshot.getValue(AnsweredPlace.class);
                        AnsweredPlace.PlaceNameWithCount pnwc = a.getObjectContatiningNumberOfAnswersForEnquire(_enquireID, getActivity());
                        if (pnwc != null) {
                            pnwcs.add(pnwc);
                        }
                    }
                    Collections.sort(pnwcs);
                    Collections.reverse(pnwcs);
                    for (AnsweredPlace.PlaceNameWithCount pnwc1 : pnwcs) {
                        _dataset.add(pnwc1.toString(true));
                    }
                    _adapter = new RecyclerViewAdapter(_inflater, _dataset);
                    _recyclerView.setAdapter(_adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == _postAnswer) {
            view.startAnimation(_buttonClick);
            _answerCreator.resetAnswerCreator();
            _answerCreator.setCurrentEnquireID(_enquireID);
            _answerCreator.getAutocompleteFragment().setText("");
            _mapScreen.getLastKnownLocation();
            _mapScreen.setCurrentFragment(_answerCreator);
            _answerCreator.setPreviousFragment(this);
            _mainScreen.setCurrentFragment(_mapScreen);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            _enquireID = null;
            _enquire = null;
        }
    }
}
