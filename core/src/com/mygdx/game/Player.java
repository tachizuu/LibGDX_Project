package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import java.util.List;

public class Player extends Character
{
    private static final Texture PLAYER_TEXTURE = new Texture("textures/player1.png");
    
    private int damageCooldown;
    
    private List<Projectile> projectiles = new ArrayList<Projectile>();
    
    public Player(float x, float y)
    {
        super(PLAYER_TEXTURE, x, y, 4, 60, 5);  //textur, x, y, movement speed, hitpoints
    }
    
    public void jump()
    {
        if(!isFalling())
        {
            setDY(20);
            setFalling(true);
        }
    }
    
    @Override
    public void takeDamage(int damage)
    {
        if(damageCooldown <= 0)
        {
            super.takeDamage(damage);
            damageCooldown = 60;
            //play sound
        }
    }
    
    public void damageCooldown()
    {
        damageCooldown--;
    }
}
