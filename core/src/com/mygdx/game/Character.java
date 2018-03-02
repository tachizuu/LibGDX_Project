package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;
import java.util.List;

public class Character extends Sprite
{
    private boolean falling = true;
    private boolean alive = true;
    
    private int movementSpeed;
    private int direction;
    private float dy;
    private List<Projectile> projectiles = new ArrayList<Projectile>();
    private int hitpoints;
    private int shotCooldown;
    private int firingSpeed;
    
    public Character(Texture tex, float x, float y, int movementSpeed, int firingSpeed, int hitpoints)
    {
        super(tex);
        this.setSize(32, 64);
        this.setX(x);
        this.setY(y);
        
        direction = 1;
        this.movementSpeed = movementSpeed;
        this.firingSpeed = firingSpeed;
        this.hitpoints = hitpoints;
    }
    
     public boolean isFalling()
     {
         return falling;
     }
     
    public void setFalling(boolean falling)
    {
        this.falling = falling;
    }
    
    public void shotCooldown()
    {
        shotCooldown--;
    }
    
    public void setDY(float dy)
    {
        this.dy = dy;
    }
    
    public float getDY()
    {
        return dy;
    }
    
    public int getMovementSpeed()
    {
        return movementSpeed;
    }
    
    public void shoot()
    {
        if(shotCooldown <= 0)
        {
            int x = (int)(getX() + (direction * 32));
            int y = (int)(getY() + (getHeight() / 2));
            Projectile p = new Projectile(x, y, direction);
            projectiles.add(p);
            shotCooldown = firingSpeed;
        }
    }
    
    public void deleteProjectile(Projectile p)
    {
        projectiles.remove(p);
    }
    
    public List<Projectile> getProjectiles()
    {
        return projectiles;
    }
    
    public int getDirection()
    {
        return direction;
    }
    
    public void setDirection(int direction)
    {
        this.direction = direction;
        if(direction == 1 && !isFlipX())
        {
            flip(true, false);
        }
        else if(direction == -1 && isFlipX())
        {
            flip(true, false);
        }
    }
    
    public int getHitpoints()
    {
        return hitpoints;
    }
    
    public void takeDamage(int damage)
    {
        hitpoints -= damage;
        if(hitpoints <= 0)
        {
            die();
        }
    }
    
    public boolean isAlive()
    {
        
        return alive;
    }
    
    private void die()
    {
        alive = false;
    }
}
