  package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Enemy extends Character
{
    private static final Texture ENEMY_TEXTURE = new Texture("textures/enemy1.png");
    
    public Enemy(float x, float y)
    {
        super(ENEMY_TEXTURE, x, y, 2, 120, 2);   //textur, x, y, movement speed, hitpoints
        this.flip(true, false);
    }
    
    public void changeDirection()
    {
        setDirection(getDirection() * -1);
    }
}
