package com.example.nhom2_l02_tank_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.nhom2_l02_tank_game.GamePlay.Game.GameActivity;

public class SelectGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_game);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());

        RelativeLayout game1 = findViewById(R.id.game1);
        game1.setOnClickListener(view -> {
            Intent intentGame1 = new Intent(SelectGameActivity.this, GameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("gamePlay",1);
            intentGame1.putExtra("intent",bundle);
            startActivity(intentGame1);
        });

        RelativeLayout game2 = findViewById(R.id.game2);
        game2.setOnClickListener(view -> {
            Intent intentGame1 = new Intent(SelectGameActivity.this, GameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("gamePlay",2);
            intentGame1.putExtra("intent",bundle);
            startActivity(intentGame1);
        });

        RelativeLayout game3 = findViewById(R.id.game3);
        game3.setOnClickListener(view -> {
            Intent intentGame1 = new Intent(SelectGameActivity.this, GameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("gamePlay",3);
            intentGame1.putExtra("intent",bundle);
            startActivity(intentGame1);
        });

    }
}