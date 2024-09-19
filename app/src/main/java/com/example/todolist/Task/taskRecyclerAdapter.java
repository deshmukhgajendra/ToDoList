package com.example.todolist.Task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Database;
import com.example.todolist.R;

import java.util.List;

public class taskRecyclerAdapter extends RecyclerView.Adapter<taskRecyclerAdapter.taskAdapterViewHolder> {

    List<taskModel> taskList;
    Context context;
    Database dbhelper;
    public taskRecyclerAdapter(List<taskModel> taskList, Context context){
        this.taskList=taskList;
        this.context=context;
        dbhelper= new Database(context);
    }

    @NonNull
    @Override
    public taskAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.taskrow,parent,false);
        return new taskAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull taskAdapterViewHolder holder, int position) {

        taskModel model=taskList.get(position);
        holder.radioButton.setText(model.getTask());

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=holder.radioButton.getText().toString();
                deleteTask(name);

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

    public class taskAdapterViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        RadioButton radioButton;

        public taskAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView=itemView.findViewById(R.id.cardview);
            radioButton=itemView.findViewById(R.id.radioButton);
        }
    }

    public void deleteTask(String name){
        SQLiteDatabase sqLiteDatabase=dbhelper.getWritableDatabase();
        sqLiteDatabase.delete("task_table","task= ?", new String[]{name});
        sqLiteDatabase.close();
    }
}
