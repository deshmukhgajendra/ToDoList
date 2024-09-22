package com.example.todolist.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;

import java.util.List;

public class ListViewRecyclerAdapter extends RecyclerView.Adapter<ListViewRecyclerAdapter.ViewHolder> {

    private List<ListModel> listNames;
    Context context;

    ListViewRecyclerAdapter(List<ListModel> listNames,Context context){
        this.listNames=listNames;
        this.context=context;
    }
    @NonNull
    @Override
    public ListViewRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.listrow,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewRecyclerAdapter.ViewHolder holder, int position) {
        ListModel model=listNames.get(position);
        holder.radioButton.setText(model.getListTask());
    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardview;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview=itemView.findViewById(R.id.cardview);
            radioButton=itemView.findViewById(R.id.radioButton);
        }
    }
}



