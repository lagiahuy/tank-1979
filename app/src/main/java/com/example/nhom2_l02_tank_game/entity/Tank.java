package com.example.nhom2_l02_tank_game.entity;

import android.util.Log;

import com.example.nhom2_l02_tank_game.util.Constants;

public class Tank extends Unit {
    public static final int DESTORYED = 0;
    public static final int ACTIVE = 1;

    int type;
    int hitPoint;
    int life;
    int attackInterval;
    public int state;
    int numKill;
    /*
     * pixelPerBlock: to calculate how many pixels it should move
     */
    public Tank(int type, int x, int y, int direction, int owner, int pixelPerBlock,int life) {
        this.type = type;
        int[] stats = Constants.TYPES[type]; // stats:[HP, speed scalar, attack interval]
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.owner = owner;
        hitPoint = stats[0];
        this.life = life;
        // pixelPerBlock / 10 is a proper base speed. Change speed by multiplying a scalar.
        speed = (int)((pixelPerBlock / 10) * (1+0.25*stats[1]));
        Log.d("speed of tank type" + this.type, "" + speed);
        isMoving = false;
        attackInterval = stats[2];
        state = ACTIVE;
        numKill = 0;
    }

    public void hit() {
        if(hitPoint > 0) {
            hitPoint--;
            Log.e("TAG", "hit: " + hitPoint);
            if(hitPoint == 0){
                state = DESTORYED;
                numKill ++;
                if(life > 0){
                    life --;
                }
            }
        }else {
            state = DESTORYED;
            Log.e("TAG", "life: "+life);
            if(life > 0){
                life --;
            }
        }
    }

    public void setHitPoint(int hitPoint) {
        this.hitPoint = hitPoint;
        state = ACTIVE;
    }

    public int getHitPoint() {
        return hitPoint;
    }

    public int getAttackInterval() {
        return attackInterval;
    }

    public int getState() {
        return state;
    }
    public int getLife() {
        return life;
    }
    public void setLife(int life) {
        this.life = life;
    }

    public int getNumKill() {
        return numKill;
    }

    public void setNumKill(int numKill) {
        this.numKill = numKill;
    }
}
