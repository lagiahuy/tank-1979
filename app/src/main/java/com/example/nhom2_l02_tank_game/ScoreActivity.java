package com.example.nhom2_l02_tank_game;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.nhom2_l02_tank_game.store.DBManager;

import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_score);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());

        DBManager db = new DBManager(this);

        TextView rank1 = findViewById(R.id.rank1);
        TextView rank2 = findViewById(R.id.rank2);
        TextView rank3 = findViewById(R.id.rank3);
        TextView rank4 = findViewById(R.id.rank4);

        List<Integer> scoresGame1 = db.getScoreGame1();
        if(scoresGame1.size() > 0) {
            rank1.setText(String.valueOf(scoresGame1.get(0))+" s");
        }
        if(scoresGame1.size() > 1) {
            rank2.setText(String.valueOf(scoresGame1.get(1))+" s");
        }
        if(scoresGame1.size() > 2) {
            rank3.setText(String.valueOf(scoresGame1.get(2))+" s");
        }
        if(scoresGame1.size() > 3) {
            rank4.setText(String.valueOf(scoresGame1.get(3))+" s");
        }


        TextView second1 = findViewById(R.id.second1);
        TextView second2 = findViewById(R.id.second2);
        TextView second3 = findViewById(R.id.second3);
        TextView second4 = findViewById(R.id.second4);

        List<Integer> scoresGame2 = db.getScoreGame2();
        if(scoresGame2.size() > 0) {
            second1.setText(String.valueOf(scoresGame2.get(0)));
        }
        if(scoresGame2.size() > 1) {
            second2.setText(String.valueOf(scoresGame2.get(1)));
        }
        if(scoresGame2.size() > 2) {
            second3.setText(String.valueOf(scoresGame2.get(2)));
        }
        if(scoresGame2.size() > 3) {
            second4.setText(String.valueOf(scoresGame2.get(3)));
        }


        TextView third1 = findViewById(R.id.third1);
        TextView third2 = findViewById(R.id.third2);
        TextView third3 = findViewById(R.id.third3);
        TextView third4 = findViewById(R.id.third4);

        List<Integer> scoresGame3 = db.getScoreGame3();
        if(scoresGame3.size() > 0) {
            third1.setText(String.valueOf(scoresGame3.get(0))+" s");
        }
        if(scoresGame3.size() > 1) {
            third2.setText(String.valueOf(scoresGame3.get(1))+" s");
        }
        if(scoresGame3.size() > 2) {
            third3.setText(String.valueOf(scoresGame3.get(2))+" s");
        }
        if(scoresGame3.size() > 3) {
            third4.setText(String.valueOf(scoresGame3.get(3)) + " s");
        }
    }
}