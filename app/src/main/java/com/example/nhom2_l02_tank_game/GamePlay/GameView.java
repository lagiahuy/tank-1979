package com.example.nhom2_l02_tank_game.GamePlay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.nhom2_l02_tank_game.GamePlay.Thread.LoopThread;
import com.example.nhom2_l02_tank_game.R;
import com.example.nhom2_l02_tank_game.entity.EnemyAI;
import com.example.nhom2_l02_tank_game.entity.Bullet;
import com.example.nhom2_l02_tank_game.entity.Tank;
import com.example.nhom2_l02_tank_game.store.DBManager;
import com.example.nhom2_l02_tank_game.util.Constants;
import com.example.nhom2_l02_tank_game.util.StopWatch;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;


public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    private DBManager db;
    public final static boolean STOP = false;
    public final static boolean MOVING = true;
    public final static int UP = 0;
    public final static int RIGHT = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;
    //static number of the owner
    public final static int PLAYER1 = 100;
    public final static int ENEMY = 102;

    private String fpsText = "0.0";
    private Context mContext;
    private SurfaceHolder holder;
    private LoopThread loopThread;
    private Canvas c;
    private Paint paint;

    //game states
    private boolean playing = false;
    private boolean finished = false;

    // data of the play area
    private int playAreaBlockInPixel; // 50 for 1440P
    private int playAreaBlockWidth = 32;
    private int playAreaBlockHight = 24; // a 32*24 map

    //screen data
    private int screenRatio = 18; //16=16:9, 18=18:9  etc.
    private int screenWidth;
    private int screenHeight;
    private int pixelPerBlock; //block size in pixels, 160 for 1440P

    private int playAreaLeft;
    private int playAreaRight;
    private int playAreaTop;
    private int playAreaBottom;

    // other measurements
    private int bulletSize;
    private int tankSize;
    private int difficulty;

    // components and units
    private Rect leftUIRect;
    private Rect rightUIRect;
    private Tank player;
    private ArrayList<Tank> tankList = new ArrayList<Tank>();
    private ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
    private Iterator<Bullet> bulletIterator;
    private int attackCooldown;
    private int enemyRespawnX;
    private int enemyRespawnY;

    private int playerRespawnX;
    private int playerRespawnY;

    //rule
    private int gamePlay,playerType,enemyType,enemyHitPoint,playerHitPoint,playerLife,enemyLife;

    private SoundPool soundPool;
    private int shoot, boom;
    private SharedPreferences sp;
    boolean soundGame;

            StopWatch stopWatch = new StopWatch();

    EnemyAI enemyAI;

    private int enemyRespawnTime;

    //resources
    Bitmap[] tank1; // player's tank pic
    Bitmap[] tank2; // enemy's tank pic
    Bitmap bullet1;
    int[] tankDrawable1 = {
            R.drawable.tank1_up,  R.drawable.tank1_right,  R.drawable.tank1_down, R.drawable.tank1_left};
    int[] tankDrawable2 = {
            R.drawable.tank2_up,  R.drawable.tank2_right,  R.drawable.tank2_down, R.drawable.tank2_left};

    public GameView(Context context) {
        super(context);
        mContext = context;
        holder = this.getHolder();
        holder.addCallback(this);
        paint = new Paint();

        db = new DBManager(context);

        // loading resources
        tank1 = new Bitmap[4];
        tank2 = new Bitmap[4];
        for(int i=0; i<4; i++) {
            Bitmap pic1 = BitmapFactory.decodeResource(getResources(), tankDrawable1[i]);
            Bitmap pic2 = BitmapFactory.decodeResource(getResources(), tankDrawable2[i]);
            tank1[i] = pic1;
            tank2[i] = pic2;
        }
        Bitmap srcBullet1 =  BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
        bullet1 = Bitmap.createScaledBitmap(srcBullet1,(int) (srcBullet1.getWidth()*8), (int) (srcBullet1.getHeight()*8),false);
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(attrs).build();

        shoot = soundPool.load(context, R.raw.shoot, 1);
        boom = soundPool.load(context, R.raw.explode, 1);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        soundGame = sp.getBoolean("soundGame", true);
    }

    //getters and setters
    public int getPlayAreaLeft() {
        return playAreaLeft;
    }

    public int getPlayAreaRight() {
        return playAreaRight;
    }

    public int getPlayAreaTop() {
        return playAreaTop;
    }

    public int getPlayAreaBottom() {
        return playAreaBottom;
    }

    public int getPlayAreaBlockInPixel() {
        return playAreaBlockInPixel;
    }


    @Override
    public void draw(Canvas canvas) {
        if(canvas != null) {
            super.draw(canvas);
            drawBackground(canvas, paint);
            drawTanks(canvas, paint);
            drawBullet(canvas, paint);
            drawFps(canvas, paint);
            drawHP(canvas,paint);
            drawInfo(canvas,paint);
        }
    }

    public void setFps(double fps) {
        fpsText = new Formatter().format("%.1f", fps).toString();
    }

    //a sort of draw methods
    private void drawFps(Canvas c, Paint p) {
        p.setColor(Color.BLUE);
        p.setTextSize(48);
        c.drawText("FPS: " + fpsText, (screenWidth/2)-100, 90, p);
    }

    private void drawHP(Canvas c, Paint p) {
        Bitmap hpSrc = BitmapFactory.decodeResource(getResources(), R.drawable.hp_bar);
        int currentHp =hpSrc.getWidth() * player.getHitPoint()/playerHitPoint;
        if(currentHp == 0 ){
            currentHp = 1;
        }
        Bitmap hp = Bitmap.createScaledBitmap(hpSrc,currentHp,hpSrc.getHeight(),false);

        Bitmap enemyHpSrc = BitmapFactory.decodeResource(getResources(), R.drawable.hp_bar);
        Tank enemy = tankList.get(1);
        int currentEnemyHp =enemyHpSrc.getWidth() * enemy.getHitPoint()/enemyHitPoint;
        if(currentEnemyHp == 0 ){
            currentEnemyHp = 1;
        }
        Bitmap EnemyHp = Bitmap.createScaledBitmap(enemyHpSrc,currentEnemyHp,enemyHpSrc.getHeight(),false);

        c.drawBitmap(hp,0,0,paint);
        c.drawBitmap(EnemyHp,screenWidth-EnemyHp.getWidth()-100,0,paint);

    }

    private void drawInfo(Canvas c, Paint p) {
        p.setColor(Color.BLUE);
        p.setTextSize(48);
        c.drawText("Số mạng: " + player.getLife(), 40, 150, p);
        Tank enemy = tankList.get(1);
        c.drawText("Số kẻ địch: " + enemy.getLife(), 40, 200, p);

        c.drawText("Thời gian: "+ (int) (stopWatch.getElapsedTime()/1000)+ " s",40,250,p);
    }

    private void drawBackground(Canvas c, Paint p) {
        //first the main background
        p.setColor(Color.GRAY);
        c.drawRect(leftUIRect, p);
        c.drawRect(rightUIRect, p);
        // leave the play area at this moment, cuz it's black
        // we don't need to draw black
        switch (gamePlay){
            case 1:
                Bitmap bgSrc = BitmapFactory.decodeResource(getResources(), R.drawable.backgrounddetailed8);
                Bitmap bg = Bitmap.createScaledBitmap(bgSrc,screenWidth,screenHeight,false);
                c.drawBitmap(bg,0,0,p);
                break;
            case 2:
                Bitmap bgSrc2 = BitmapFactory.decodeResource(getResources(), R.drawable.backgrounddetailed2);
                Bitmap bg2 = Bitmap.createScaledBitmap(bgSrc2,screenWidth,screenHeight,false);
                c.drawBitmap(bg2,0,0,p);
                break;
            case 3:
                Bitmap bgSrc3 = BitmapFactory.decodeResource(getResources(), R.drawable.backgrounddetailed1);
                Bitmap bg3 = Bitmap.createScaledBitmap(bgSrc3,screenWidth,screenHeight,false);
                c.drawBitmap(bg3,0,0,p);
                break;

        }
    }

    private void drawTanks(Canvas c, Paint p) {
        for(Tank t : tankList) {
            if(t.getHitPoint() > 0) {
                int direction = t.getDirection();
                int left = t.getX() - playAreaBlockInPixel;
                int right = t.getX() + playAreaBlockInPixel;
                int top = t.getY() - playAreaBlockInPixel;
                int bottom = t.getY() + playAreaBlockInPixel;
                Rect tankRect = new Rect(left, top, right, bottom);
                if(t.getOwner() == PLAYER1)
                    c.drawBitmap(tank1[direction], null, tankRect, null);
                else if(t.getOwner() == ENEMY)
                    c.drawBitmap(tank2[direction], null, tankRect, null);
            }
        }
    }

    private void drawBullet(Canvas c, Paint p) {
        // we use iterator approach
        bulletIterator = bulletList.iterator();
        while(bulletIterator.hasNext()) {
            Bullet t = bulletIterator.next();
            int left = t.getX() - bulletSize;
            int right = t.getX() + bulletSize;
            int top = t.getY() - bulletSize;
            int bottom = t.getY() + bulletSize;
            Rect bulletRect = new Rect(left, top, right, bottom);
            c.drawBitmap(bullet1, null, bulletRect, null);
        }
    }
    //draw methods end

    public boolean hitDetectBase(Bullet bullet, Tank t) {
        int bulletX = bullet.getX();
        int bulletY = bullet.getY();
        int tX = t.getX();
        int tY = t.getY();

        if(bulletX - bulletSize >= tX - tankSize && bulletX + bulletSize <= tX + tankSize) {
            if(bulletY - bulletSize >= tY - tankSize && bulletY + bulletSize <= tY + tankSize) {
                if(bullet.getOwner() != t.getOwner()) {
                    return true;
                } else {
                    return false;
                }
            }
            else
                return false;
        } else {
            return false;
        }
    }


    public int boundaryDetect(Tank t) {
        if(t.getX() < playAreaLeft + playAreaBlockInPixel) {
            t.setOffset(1, 0);
            return 1; // reach left
        }

        else if(t.getX() > playAreaRight - playAreaBlockInPixel) {
            t.setOffset(-1, 0);
            return 2; // reach right
        }
        else if(t.getY() < playAreaTop + playAreaBlockInPixel) {
            t.setOffset(0, 1);
            return 3; // reach top
        }
        else if(t.getY() > playAreaBottom - playAreaBlockInPixel) {
            t.setOffset(0, -1);
            return 4; // reach bottom
        }
        else
            return 0;
    }

    public boolean boundaryDetect(Bullet bullet) {
        if(bullet.getX() < playAreaLeft + bulletSize
            || bullet.getX() > playAreaRight - bulletSize
            || bullet.getY() < playAreaTop
            || bullet.getY() > playAreaBottom) {
            return true;
        } else {
            return false;
        }
    }

    // game logic updates here
    public synchronized void updateStates() {
        attackCooldown--;
        if(attackCooldown < 0)
            attackCooldown =0;

        Tank player = tankList.get(0);
        if(player.getHitPoint() == 0) {
            if(player.getLife() == 0){
                gameOver();
            }else{
                enemyRespawnTime--;
                if(enemyRespawnTime == 0) {
                    player.setHitPoint(playerHitPoint);
                    player.setX(playerRespawnX);
                    player.setY(playerRespawnY);
                    enemyRespawnTime = 60;
                }
            }
        }else{
            if(player.isMoving && boundaryDetect(player) == 0)
                player.move();
        }

        Tank enemy = tankList.get(1);
        if((int) (stopWatch.getElapsedTime()/1000) == 30){
            if(gamePlay==2){
                db.insertScoreGame2(enemy.getNumKill());
                win();
            }
        }
        if(enemy.getHitPoint() == 0) {
            if(enemy.getLife() == 0){
                switch (gamePlay){
                    case 1:
                        db.insertScoreGame1((int) (stopWatch.getElapsedTime()/1000));
                        break;
                    case 3:
                        db.insertScoreGame3((int) (stopWatch.getElapsedTime()/1000));
                        break;
                }
                win();
            }else{
                enemyRespawnTime--;
                if(enemyRespawnTime == 0) {
                    if(gamePlay == 3 ){
                        enemy.setHitPoint(enemyHitPoint/2);
                    }else{
                        enemy.setHitPoint(enemyHitPoint);
                    }
                    enemy.setX(enemyRespawnX);
                    enemy.setY(enemyRespawnY);
                    enemyRespawnTime = 60;
                }
            }
        } else {
            enemyAI.align();
            enemyAI.attack();
        }

        bulletIterator = bulletList.iterator();
        while(bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            if(hitDetectBase(bullet, enemy)) {
                enemy.hit();
                if(enemy.state == enemy.DESTORYED) {
                    if(soundGame) {
                        soundPool.play(boom, 1f, 1f, 0, 0, 1);
                    }
                    enemy.setX(0);
                    enemy.setY(0);
                }
                bulletIterator.remove();
            }else if(hitDetectBase(bullet,player)) {
                player.hit();
                if(player.state == player.DESTORYED) {
                    if(soundGame) {
                        soundPool.play(boom, 1f, 1f, 0, 0, 1);
                    }
                    player.setX(0);
                    player.setY(0);
                }
                bulletIterator.remove();
            }else if(boundaryDetect(bullet)) {
                bulletIterator.remove();
            } else {
                bullet.move();
            }
        }

    }

    // methods for controls
    public void movePlayer(int dir, boolean isMoving) {
        Tank player = tankList.get(0);
        player.setDirection(dir);
        player.setMovingState(isMoving);
    }

    public synchronized void fire() {
        if(attackCooldown > 0) {
            Log.d("fire", "reloading!");
        } else {
            Tank player = tankList.get(0);
            int direction = player.getDirection();
            int offsetX = 0;
            int offsetY = 0;
            switch (direction) {
                case 0:
                    offsetY = -playAreaBlockInPixel;
                    break;
                case 1:
                    offsetX = playAreaBlockInPixel;
                    break;
                case 2:
                    offsetY = playAreaBlockInPixel;
                    break;
                case 3:
                    offsetX = -playAreaBlockInPixel;
                    break;
                default:
                    break;
            }
            Bullet bullet = new Bullet(
                    0, player.getX() + offsetX, player.getY() + offsetY, player.getDirection(), playAreaBlockInPixel);
            if(soundGame){
                soundPool.play(shoot,1f,1f,0,0,1);
            }
            bulletList.add(bullet);
            attackCooldown =player.getAttackInterval();

        }


    }

    public synchronized void fire(Tank t) {
        Log.d("fire", "enemy fire!");
        int direction = t.getDirection();
        int offsetX = 0;
        int offsetY = 0;
        switch (direction) {
            case 0:
                offsetY = -playAreaBlockInPixel;
                break;
            case 1:
                offsetX = playAreaBlockInPixel;
                break;
            case 2:
                offsetY = playAreaBlockInPixel;
                break;
            case 3:
                offsetX = -playAreaBlockInPixel;
                break;
            default:
                break;
        }
        Bullet p = new Bullet(
                0, t.getX() + offsetX, t.getY() + offsetY, t.getDirection(), playAreaBlockInPixel);
        if(soundGame){
            soundPool.play(shoot,1f,1f,0,0,1);
        }
        bulletList.add(p);
    }

    public void gamePlay(int game) {
        gamePlay = game;
        Log.e("TAG", "gamePlay: "+game );
        switch (game){
            case 1:
                //type[][HP, speed scalar, attack interval]
                playerType = 0;
                enemyType = 2;
                playerHitPoint=3;
                enemyHitPoint=1;
                playerLife=1;
                enemyLife=10;
                break;
            case 2:
                playerType = 3;
                enemyType = 1;
                playerHitPoint = 5;
                enemyHitPoint = 2;
                playerLife = 1;
                enemyLife = 9999999;
                break;

            case 3:
                playerType = 3;
                enemyType = 4;
                playerHitPoint = 5;
                enemyHitPoint = 30;
                playerLife = 3;
                enemyLife = 2;
                break;
        }
    }

    // initialize game
    public void init() {
        stopWatch.start();
        Log.e("TAG", "playerType: "+ playerType+" --- "+enemyType);
        playerRespawnX = screenWidth/5;
        playerRespawnY = screenHeight/2;
        player = new Tank(playerType, playerRespawnX, playerRespawnY, 0, PLAYER1, playAreaBlockInPixel,playerLife);
        attackCooldown = player.getAttackInterval();
        tankList.add(player);

        enemyRespawnX = screenWidth / 2;
        enemyRespawnY = screenHeight / 5;

        Tank enemy = new Tank(enemyType, enemyRespawnX, enemyRespawnY, 2, ENEMY, playAreaBlockInPixel,enemyLife);
        enemyAI = new EnemyAI(enemy, player, this, difficulty);
        tankList.add(enemy);

        enemyRespawnTime = 60;
        difficulty = 1;
    }

    public void pause(){
        playing = false;
        stopWatch.pause();
        loopThread.setRunning(false);
    }

    public void gameOver(){
        playing = false;
        stopWatch.pause();
        loopThread.setRunning(false);
        Intent i = new Intent(Constants.GAME_OVER);
        mContext.sendBroadcast(i);
    }

    public void win(){
        playing = false;
        stopWatch.pause();
        loopThread.setRunning(false);
        Intent i = new Intent(Constants.WIN);
        mContext.sendBroadcast(i);
    }

    public void resume() {
        playing = true;
        stopWatch.start();
        loopThread = new LoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();
    }

    public void restartGame(){
        playing = true;
        stopWatch.reset();
        stopWatch.start();
        Tank player = tankList.get(0);
        player.setHitPoint(playerHitPoint);
        player.setLife(playerLife);
        player.setX(playerRespawnX);
        player.setY(playerRespawnY);
        player.setNumKill(0);
        Tank enemy = tankList.get(1);
        enemy.setHitPoint(enemyHitPoint);
        enemy.setLife(enemyLife);
        enemy.setX(enemyRespawnX);
        enemy.setY(enemyRespawnY);
        enemy.setNumKill(0);
        loopThread = new LoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();
    }

    public void restart() {
        playing = true;
        finished = false;
        init();
        loopThread = new LoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();
    }

    // get screen width & height here
    public void surfaceCreated(SurfaceHolder holder) {
        Rect surfaceFrame = holder.getSurfaceFrame();
        screenWidth = surfaceFrame.width();
        screenHeight = surfaceFrame.height();
        Log.d("W and H", screenWidth + ", " + screenHeight);
        pixelPerBlock = screenWidth / screenRatio;
        Log.d("pixelPerBlock:", ""+pixelPerBlock);
        int sideSpace = (screenRatio - 12) / 2; //sideSpace in blocks
        playAreaLeft = sideSpace ; // we have a 4:3 game area
        playAreaRight = screenWidth ;
        playAreaTop = 0;
        playAreaBottom = screenHeight;

        leftUIRect = new Rect(0, 0, 0, screenHeight);
        rightUIRect = new Rect(screenWidth , 0, screenWidth, screenHeight);
        playAreaBlockInPixel = (playAreaRight - playAreaLeft) / playAreaBlockWidth;
        Log.d("playAreaBlockInPixel", "" + playAreaBlockInPixel);
        bulletSize = playAreaBlockInPixel / 4;
        tankSize = playAreaBlockInPixel;
        restart();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int screenWidth,
                               int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        playing = false;
        loopThread.setRunning(false);
        while (retry) {
            try {
                loopThread.sleep(100);
                loopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
