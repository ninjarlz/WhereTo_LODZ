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
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.Enquire;
import com.fireinsidethemountain.whereto.model.RecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FoodModule extends Fragment implements View.OnClickListener {

    private RecyclerView _recyclerView;
    private RecyclerViewAdapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;
    private DatabaseReference _foodEnquiresReference = FirebaseDatabase.getInstance().getReference("Enquires");
    private MapScreen _mapScreen;
    private Fragment _answerCreator;
    private MainScreen _mainScreen;
    private List<String> _enquiresIDs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.food_module, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _mainScreen = (MainScreen) getActivity();
        _mapScreen = (MapScreen) _mainScreen.getMapScreenFragment();
        _answerCreator = _mapScreen.getAnswerCreator();
        _recyclerView = (RecyclerView) view.findViewById(R.id.food_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        _recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        _layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(_layoutManager);

        _foodEnquiresReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> dataset = new ArrayList<>();
                _enquiresIDs = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Enquire e = childSnapshot.getValue(Enquire.class);
                    dataset.add(e.toString());
                    _enquiresIDs.add(childSnapshot.getKey());
                }
                _adapter = new RecyclerViewAdapter(getActivity(), dataset);
                _recyclerView.setAdapter(_adapter);
                _adapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View v,int pos) {
                        AnswerCreator answerCreator = (AnswerCreator)_answerCreator;
                        answerCreator.setCurrentEnquireID(_enquiresIDs.get(pos));
                        answerCreator.setCurrentPlace(null);
                        answerCreator.getAutocompleteFragment().setText("");
                        _mapScreen.getLastKnownLocation();
                        _mapScreen.setCurrentFragment(_answerCreator);
                        _mainScreen.setCurrentFragment(_mapScreen);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }

        });
    }

    @Override
    public void onClick(View view) {

    }


    void updateRecycleContent() {

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

        }
    }
}