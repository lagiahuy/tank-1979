package com.example.nhom2_l02_tank_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.nhom2_l02_tank_game.store.DBManager;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ToggleButton btn_sound;
    MediaPlayer mediaPlayer ;
    private DBManager db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        db = new DBManager(this);
        Button start = findViewById(R.id.start);
        Button option = findViewById(R.id.ops);
        Button rank = findViewById(R.id.score);

        start.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SelectGameActivity.class);
            startActivity(intent);
        });

        option.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OptionActivity.class);
            startActivity(intent);
        });

        rank.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
            startActivity(intent);
        });

        btn_sound = findViewById(R.id.btn_sound);

        mediaPlayer = new MediaPlayer();

        try {
            AssetFileDescriptor descriptor = getAssets().openFd("intro.ogg");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(),descriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btn_sound.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}