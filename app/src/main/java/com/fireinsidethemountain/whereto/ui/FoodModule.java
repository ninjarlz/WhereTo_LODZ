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
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.RecyclerViewAdapter;

import java.util.ArrayList;


public class FoodModule extends Fragment implements View.OnClickListener {

    private RecyclerView _recyclerView;
    private RecyclerView.Adapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.food_module, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _recyclerView = (RecyclerView) view.findViewById(R.id.food_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        _recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        _layoutManager = new LinearLayoutManager(getActivity());
        _recyclerView.setLayoutManager(_layoutManager);

        ArrayList<String> dataset = new ArrayList<>();
        dataset.add("Burger");
        dataset.add("Gowno");
        dataset.add("Spaghetti");

        // specify an adapter
        _adapter = new RecyclerViewAdapter(getActivity(), dataset);
        _recyclerView.setAdapter(_adapter);
    }

    @Override
    public void onClick(View view) {

    }
}
