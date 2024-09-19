package com.example.todolist;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class DatabaseResetWorker extends Worker {


    public DatabaseResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        Context context=getApplicationContext();

        try {
            cleareMyDayTable();
            return Result.success();
        }catch (Exception e){
            Log.e(TAG, "doWork: Error clearing table",e );
            return Result.failure();
        }

    }

    private void cleareMyDayTable(){
     Database dbHelper= new Database(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM myday_table");
        Log.d(TAG, "createMyDayTable: table deleted sucessfully");
    }
}
