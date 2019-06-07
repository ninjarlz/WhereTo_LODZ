package com.fireinsidethemountain.whereto.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fireinsidethemountain.whereto.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> _data;
    private LayoutInflater _inflater;
    private ItemClickListener _clickListener;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, List<String> data) {
        _inflater = LayoutInflater.from(context);
        _data = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = _inflater.inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = _data.get(position);
        holder._textView.setText(animal);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return _data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView _textView;

        ViewHolder(View itemView) {
            super(itemView);
            _textView = itemView.findViewById(R.id.food_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (_clickListener != null) _clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return _data.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        _clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}