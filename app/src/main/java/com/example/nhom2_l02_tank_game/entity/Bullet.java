package com.example.nhom2_l02_tank_game.entity;


public class Bullet extends Unit {

    public Bullet(int owner, int x, int y, int direction, int pixelsPerBlock) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.owner = owner;
        speed = (int)(pixelsPerBlock / 5 * 2.5);
        isMoving = true;
    }

}
