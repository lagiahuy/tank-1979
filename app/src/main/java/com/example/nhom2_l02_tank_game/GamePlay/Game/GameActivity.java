package com.example.nhom2_l02_tank_game.GamePlay.Game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nhom2_l02_tank_game.GamePlay.GameView;
import com.example.nhom2_l02_tank_game.MainActivity;
import com.example.nhom2_l02_tank_game.OptionActivity;
import com.example.nhom2_l02_tank_game.R;
import com.example.nhom2_l02_tank_game.SelectGameActivity;
import com.example.nhom2_l02_tank_game.util.Constants;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private FrameLayout mFrame;
    private View ui;
    private BroadcastReceiver receiver;

    // buttons for control
    private ImageButton leftButton;
    private ImageButton rightButton;
    private ImageButton upButton;
    private ImageButton downButton;
    private Button fightButton,resumeButton,restartButton,next_game,returnMenuButton,settingButton;
    private TextView text_game_over,text_win;
    private ImageButton pauseButton;
    private RelativeLayout controlBlock;
    private MediaPlayer mediaPlayer ;
    private SharedPreferences sp;
    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gameView = new GameView(this);
        Intent callerIntent = getIntent();
        Bundle data = callerIntent.getBundleExtra("intent");
        int type = data.getInt("gamePlay");
        gameView.gamePlay(type);
        mFrame = new FrameLayout(this);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        ui = inflater.inflate(R.layout.ui_game,null);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        sp = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
        mediaPlayer = new MediaPlayer();
        boolean audio = sp.getBoolean("audio", true);
        if(audio){
            try {
                AssetFileDescriptor descriptor;
                switch (type){
                    case 1:
                        descriptor = getAssets().openFd("game1.ogg");
                        break;
                    case 2:
                        descriptor = getAssets().openFd("game2.ogg");
                        break;
                    case 3:
                        descriptor = getAssets().openFd("game3.mp3");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }

                mediaPlayer.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(),descriptor.getLength());
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        controlBlock = ui.findViewById(R.id.left_panel);
        upButton = (ImageButton) ui.findViewById(R.id.up);
        upButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                gameView.movePlayer(GameView.UP, GameView.STOP);
                return true;
            } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                gameView.movePlayer(GameView.UP, GameView.MOVING);
                return true;
            }
            return false;
        });

        rightButton = (ImageButton) ui.findViewById(R.id.right);
        rightButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                gameView.movePlayer(GameView.RIGHT, GameView.STOP);
                return true;
            } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                gameView.movePlayer(GameView.RIGHT, GameView.MOVING);
                return true;
            }
            return false;
        });

        downButton = (ImageButton) ui.findViewById(R.id.down);
        downButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                gameView.movePlayer(GameView.DOWN, GameView.STOP);
                return true;
            } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                gameView.movePlayer(GameView.DOWN, GameView.MOVING);
                return true;
            }
            return false;
        });

        leftButton = (ImageButton) ui.findViewById(R.id.left);
        leftButton.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                gameView.movePlayer(GameView.LEFT, GameView.STOP);
                return true;
            } else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                gameView.movePlayer(GameView.LEFT, GameView.MOVING);
                return true;
            }
            return false;
        });


        fightButton = (Button) ui.findViewById(R.id.fight_button);
        fightButton.setOnClickListener(view -> gameView.fire());

        pauseButton = (ImageButton) ui.findViewById(R.id.pause);
        pauseButton.setOnClickListener(view -> pauseGame());


        text_game_over = ui.findViewById(R.id.text_game_over);
        text_game_over.setVisibility(View.GONE);
        text_win = ui.findViewById(R.id.text_win);
        text_win.setVisibility(View.GONE);

        resumeButton = (Button) ui.findViewById(R.id.return_game);
        resumeButton.setVisibility(View.GONE);
        resumeButton.setOnClickListener(view -> resumeGame());

        settingButton = ui.findViewById(R.id.setting_game);
        settingButton.setVisibility(View.GONE);
        settingButton.setOnClickListener(view -> startActivity(new Intent(GameActivity.this, OptionActivity.class)));

        restartButton = (Button) ui.findViewById(R.id.restart_game);
        restartButton.setVisibility(View.GONE);
        restartButton.setOnClickListener(view -> restartGame());

        next_game = ui.findViewById(R.id.next_game);
        next_game.setOnClickListener(view -> startActivity(new Intent(GameActivity.this, SelectGameActivity.class)));
        next_game.setVisibility(View.GONE);

        returnMenuButton = (Button) ui.findViewById(R.id.return_menu);
        returnMenuButton.setVisibility(View.GONE);
        returnMenuButton.setOnClickListener(view -> startActivity(new Intent(GameActivity.this, MainActivity.class)));

        // handle controls
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.GAME_OVER);
        filter.addAction(Constants.WIN);
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.e("TAG", "onReceive: " +intent.getAction());
                switch (intent.getAction()){
                    case Constants.GAME_OVER:
                        overGame();
                        break;
                    case Constants.WIN:
                        winGame();
                        break;
                }
            }
        };
        registerReceiver(receiver, filter);

        mFrame.addView(gameView);
        mFrame.addView(ui);
        setContentView(mFrame);
    }

    public void pauseGame() {
        gameView.pause();
        ui.setBackgroundColor(ContextCompat.getColor(
                GameActivity.this,R.color.lightGray));
        pauseButton.setVisibility(View.GONE);
        controlBlock.setVisibility(View.GONE);
        fightButton.setVisibility(View.GONE);

        resumeButton.setVisibility(View.VISIBLE);
        returnMenuButton.setVisibility(View.VISIBLE);
    }

    public void resumeGame() {
        gameView.resume();
        ui.setBackgroundColor(Color.TRANSPARENT);
        pauseButton.setVisibility(View.VISIBLE);
        controlBlock.setVisibility(View.VISIBLE);
        fightButton.setVisibility(View.VISIBLE);

        resumeButton.setVisibility(View.GONE);
        returnMenuButton.setVisibility(View.GONE);
        settingButton.setVisibility(View.GONE);
    }

    public void overGame() {
        ui.setBackgroundColor(ContextCompat.getColor(
                GameActivity.this,R.color.lightGray));
        pauseButton.setVisibility(View.GONE);
        controlBlock.setVisibility(View.GONE);
        fightButton.setVisibility(View.GONE);

        text_game_over.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
        returnMenuButton.setVisibility(View.VISIBLE);
    }

    public void restartGame(){
        gameView.restartGame();
        ui.setBackgroundColor(Color.TRANSPARENT);
        pauseButton.setVisibility(View.VISIBLE);
        controlBlock.setVisibility(View.VISIBLE);
        fightButton.setVisibility(View.VISIBLE);

        text_game_over.setVisibility(View.GONE);
        restartButton.setVisibility(View.GONE);
        returnMenuButton.setVisibility(View.GONE);
    }

    public void winGame(){
        ui.setBackgroundColor(ContextCompat.getColor(
                GameActivity.this,R.color.lightGray));
        pauseButton.setVisibility(View.GONE);
        controlBlock.setVisibility(View.GONE);
        fightButton.setVisibility(View.GONE);

        text_win.setVisibility(View.VISIBLE);
        next_game.setVisibility(View.VISIBLE);
        returnMenuButton.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }
    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        mediaPlayer.stop();
        super.onDestroy();
    }
}