package com.example.nhom2_l02_tank_game.store;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private SQLiteDbHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        Log.e("TAG", "DBManager: " );
        helper = new SQLiteDbHelper(context);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
    }

    public List<Integer> getScoreGame1() {
        Cursor cursor = db.rawQuery("select score from score_game_one order by score asc", null);
        List<Integer> rs = new ArrayList<>();
        while(cursor.moveToNext()) {
            rs.add(cursor.getInt(0));
        }
        cursor.close();
        return rs;
    }

    public List<Integer> getScoreGame2() {
        Cursor cursor = db.rawQuery("select score from score_game_second order by score desc", null);
        List<Integer> rs = new ArrayList<>();
        while(cursor.moveToNext()) {
            rs.add(cursor.getInt(0));
        }
        cursor.close();
        return rs;
    }

    public List<Integer> getScoreGame3() {
        Cursor cursor = db.rawQuery("select score from score_game_third order by score asc", null);
        List<Integer> rs = new ArrayList<>();
        while(cursor.moveToNext()) {
            rs.add(cursor.getInt(0));
        }
        cursor.close();
        return rs;
    }

    public void insertScoreGame1(int score) {
        db.execSQL("INSERT INTO score_game_one(score) VALUES" +
                " (?)", new Object[]{score});
    }
    public void insertScoreGame2(int score) {
        db.execSQL("INSERT INTO score_game_second(score) VALUES" +
                " (?)", new Object[]{score});
    }
    public void insertScoreGame3(int score) {
        db.execSQL("INSERT INTO score_game_third(score) VALUES" +
                " (?)", new Object[]{score});
    }
}
