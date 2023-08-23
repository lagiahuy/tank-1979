package com.example.nhom2_l02_tank_game;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;

public class OptionActivity extends AppCompatActivity {

    private SharedPreferences sp;
    @Override
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_option);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());

        sp = PreferenceManager.getDefaultSharedPreferences(OptionActivity.this);
        boolean audio = sp.getBoolean("audio", true);
        boolean soundGame = sp.getBoolean("soundGame", true);

        Switch sch = findViewById(R.id.switch1);
        sch.setChecked(audio);

        sch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("audio", isChecked);
            editor.commit();
        });

        Switch sch2 = findViewById(R.id.switch2);
        sch2.setChecked(soundGame);
        sch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("soundGame", isChecked);
            editor.commit();
        });

    }
}