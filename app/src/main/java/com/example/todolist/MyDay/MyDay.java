package com.example.todolist.MyDay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.todolist.Database;
import com.example.todolist.DatabaseResetWorker;
import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyDay extends AppCompatActivity {

    FloatingActionButton fab;
    public String myDayTask;
    Database dbHelper;
    private List<model> taskList;
    mydayRecyclerAdapter adapter;
    RecyclerView recyclerView;
    MaterialToolbar mydayToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myday);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fab=findViewById(R.id.fab);
        dbHelper= new Database(this);
        taskList= new ArrayList<>();
        adapter= new mydayRecyclerAdapter(taskList,MyDay.this);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mydayToolBar=findViewById(R.id.mydayToolBar);
        fetchData();

        mydayToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the bottom sheet layout
                LayoutInflater inflater = getLayoutInflater();
                View bottomSheetView = inflater.inflate(R.layout.bottonsheetlayout, null);

                // Access the views from the bottomSheetView, not the main view
                EditText taskEditText = bottomSheetView.findViewById(R.id.taskEditText);
                MaterialRadioButton taskButton = bottomSheetView.findViewById(R.id.tasksButton);
                Button addButton = bottomSheetView.findViewById(R.id.addButton);

                // Create and show the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MyDay.this);
                bottomSheetDialog.setContentView(bottomSheetView);

                bottomSheetDialog.show();

                // Set the onClickListener for the add button
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDayTask = taskEditText.getText().toString();
                        insertTask(myDayTask);
                        bottomSheetDialog.dismiss();
                        fetchData();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        scheduleDailyDatabaseReset();
        scheduleNotification(getApplicationContext());
    }

    public void insertTask(String task){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("task",task);
        db.insert("myday_table",null,values);
    }

    public void fetchData(){
        taskList.clear();

        Cursor cursor = dbHelper.getAllTask();

        if (cursor.moveToFirst()){
            do {
                String taskname=cursor.getString(cursor.getColumnIndex(Database.column_task));
                taskList.add(new model(taskname));
            }while (cursor.moveToNext());

        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
    public void onBackPressed() {

        super.onBackPressed();

        Intent i = new Intent(MyDay.this,MainActivity.class);
        startActivity(i);
    }
    public void scheduleDailyDatabaseReset(){
        Calendar currentTime= Calendar.getInstance();
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY,0);
        targetTime.set(Calendar.MINUTE,0);
        targetTime.set(Calendar.SECOND,0);

        if (targetTime.before(currentTime)){
            targetTime.add(Calendar.DAY_OF_MONTH,1);
        }

        long initialDelay= targetTime.getTimeInMillis() - currentTime.getTimeInMillis();

        PeriodicWorkRequest dailyWorkRequest= new PeriodicWorkRequest.Builder(DatabaseResetWorker.class,1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay,TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("DailyDatabaseReset", ExistingPeriodicWorkPolicy.REPLACE,dailyWorkRequest);
    }

    public void scheduleNotification(Context context){

        AlarmManager alarmManager=  (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        int []hours = {20,21,22,23};
        int[] minuts = {00,00,00,30};

        for (int i = 0;i<hours.length;i++){
            Calendar calendar= Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,hours[i]);
            calendar.set(Calendar.MINUTE,minuts[i]);
            calendar.set(Calendar.SECOND,0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()){
                calendar.add(Calendar.DAY_OF_YEAR,1);
            }

            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        }


    }
