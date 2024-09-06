package com.example.todolist;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static int version = 1;
    public static String db_name="db";
    public static String table_name="task_table";
    public static String column_id="id";
    public static String column_task="task";


    String query = "CREATE TABLE IF NOT EXISTS " + table_name + " ("
            + column_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + column_task + " TEXT)";


    public Database(@Nullable Context context) {
        super(context,db_name,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ table_name);
        onCreate(sqLiteDatabase);
    }

    public Cursor getAllTask(){
        SQLiteDatabase db = this.getReadableDatabase();
         return db.rawQuery("SELECT * FROM "+ table_name,null);
    }
}
