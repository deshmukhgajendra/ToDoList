package com.example.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class mydayRecyclerAdapter extends RecyclerView.Adapter<mydayRecyclerAdapter.mydayViewHolder> {


    List<model> taskList;
    Context context;
    Database dbHelper;

    mydayRecyclerAdapter(List<model> taskList, Context context){

        this.taskList=taskList;
        this.context=context;
        dbHelper = new Database(context);
    }

    @NonNull
    @Override
    public mydayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskrow, parent, false);
        return new mydayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mydayViewHolder holder, int position) {

        model model=taskList.get(position);
        holder.radioButton.setText(model.getTask());


        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String taskName=holder.radioButton.getText().toString();
                deleteTask(taskName);

                taskList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, taskList.size());
            }
        });
    }



    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class mydayViewHolder extends RecyclerView.ViewHolder{

        CardView cardview;
        RadioButton radioButton;

        public mydayViewHolder(@NonNull View itemView) {
            super(itemView);

             cardview=itemView.findViewById(R.id.cardview);
             radioButton=itemView.findViewById(R.id.radioButton);

        }
    }
    private void deleteTask(String taskName){
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        db.delete("task_table","task = ?", new String[]{taskName});
        db.close();
    }
}
