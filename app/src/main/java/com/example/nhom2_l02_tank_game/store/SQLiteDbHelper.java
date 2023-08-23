package com.example.nhom2_l02_tank_game.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tank.db";

    private static final int DB_VERSION = 1;

    private Context context;


    public SQLiteDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("TAG", "onCreate: "+db );
        db.execSQL("CREATE TABLE IF NOT EXISTS score_game_one (id integer primary key autoincrement, " +
                "score intger)");
        db.execSQL("CREATE TABLE IF NOT EXISTS score_game_second (id integer primary key autoincrement, " +
                "score intger)");
        db.execSQL("CREATE TABLE IF NOT EXISTS score_game_third (id integer primary key autoincrement, " +
                "score intger)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
