package com.example.todolist.MyDay;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class mydayRecyclerAdapter extends RecyclerView.Adapter<mydayRecyclerAdapter.mydayViewHolder> {
    @NonNull
    @Override
    public mydayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull mydayViewHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }

    public static class mydayViewHolder extends RecyclerView.ViewHolder{

        public mydayViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
