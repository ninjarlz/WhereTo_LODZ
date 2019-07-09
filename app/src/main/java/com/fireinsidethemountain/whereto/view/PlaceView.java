package com.fireinsidethemountain.whereto.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.AnsweredPlace;
import com.fireinsidethemountain.whereto.model.Enquire;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public class PlaceView extends Fragment  {
    private RecyclerView _recyclerView;
    private RecyclerViewAdapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;
    private DatabaseReference _databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference _enquiresReference;
    private DatabaseReference _answeredPlacesReference;
    private MapScreen _mapScreen;
    private PlaceView _placeViewModuleFragment = this;
    private MainScreen _mainScreen;
    private List<String> _enquiresIDs = new ArrayList<>();
    private LayoutInflater _inflater;
    private EnquireView _enquireViewFragment;
    private List<String> _dataset = new ArrayList<>();
    private ValueEventListener _currentListener;
    private String _placeID;
    private TextView _placeStats;
    private String _loading;




    private class PlaceViewItemClickListener implements  RecyclerViewAdapter.ItemClickListener {
        @Override
        public void onItemClick(View v,int pos) {
            _enquireViewFragment.setEnquire(_enquiresIDs.get(pos), _dataset.get(pos));
            _enquireViewFragment.setPreviousFragment(_placeViewModuleFragment);
            _mainScreen.setCurrentFragment(_enquireViewFragment);
        }
    }


    private PlaceViewItemClickListener _placeViewItemClickListener = new PlaceViewItemClickListener();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _inflater = inflater;
        return inflater.inflate(R.layout.place_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _answeredPlacesReference = _databaseReference.child("AnsweredPlaces");
        _enquiresReference = _databaseReference.child("Enquires");
        _loading = MainScreen.getContext().getResources().getString(R.string.loading);
        _mainScreen = (MainScreen) getActivity();
        _mapScreen = _mainScreen.getMapScreenFragment();
        _enquireViewFragment = _mainScreen.getEnquireViewFragment();
        _recyclerView = view.findViewById(R.id.place_recycler_view);
        _recyclerView.setHasFixedSize(true);
        _layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(_layoutManager);
        _placeStats = view.findViewById(R.id.placeStats);
    }


    public void setPlace(final String placeID) {
        if (placeID != null) {
            _placeID = placeID;
            _placeStats.setText(_loading);

            if (_currentListener != null) {
                _enquiresIDs.clear();
                _dataset.clear();
                _adapter = new RecyclerViewAdapter(_inflater, _dataset);
                _recyclerView.setAdapter(_adapter);
                _enquiresReference.removeEventListener(_currentListener);
            }

            _answeredPlacesReference.addListenerForSingleValueEvent(new ValueEventListener() {

                AnsweredPlace answeredPlace;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    answeredPlace = dataSnapshot.child(placeID).getValue(AnsweredPlace.class);
                    _placeStats.setText(answeredPlace.toString());

                    final Set<String> enquiresIDs = answeredPlace.getEnquireIDsCount().keySet();

                    _currentListener = _enquiresReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<Enquire> enquires = new ArrayList<>();
                            for (String enquireID : enquiresIDs) {
                                enquires.add(dataSnapshot.child(enquireID).getValue(Enquire.class));
                            }
                            Collections.sort(enquires);
                            Collections.reverse(enquires);
                            for (Enquire e : enquires) {
                                _enquiresIDs.add(e.getEnquireID());
                                AnsweredPlace.PlaceNameWithCount pnwc = answeredPlace.getObjectContatiningNumberOfAnswersForEnquire(e.getEnquireID());
                                _dataset.add(e.toString() + "\n" + pnwc.toString(false));
                            }
                            _adapter = new RecyclerViewAdapter(_inflater, _dataset);
                            _recyclerView.setAdapter(_adapter);
                            _adapter.setClickListener(_placeViewItemClickListener);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            _placeID = null;
        }
    }
}
