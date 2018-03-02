package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Projectile extends Sprite
{
    private static final Texture PROJECTILE_TEXTURE = new Texture("textures/projectile1.png");
    private int direction = 0;
    private int speed = 7;
    
    public Projectile(int x, int y, int direction)
    {
        super(PROJECTILE_TEXTURE);
        this.setX(x);
        this.setY(y);
        this.setSize(32, 32);
        this.direction = direction;
        if(direction == -1)
        {
            this.flip(true, false);
        }
    }
    
    public void move()
    {
        translateX(direction * speed);
    }
}
