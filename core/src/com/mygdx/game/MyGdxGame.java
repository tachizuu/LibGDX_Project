package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyGdxGame extends Game
{
    public SpriteBatch batch;
    public BitmapFont font;
    
    private Player player;
    private Dungeon dungeon;
    private Room currentRoom;
    
    private int score;
    
    private int slowmotionfreeze = 0;
    
    @Override
    public void create()
    {
        batch = new SpriteBatch();
        
        player = new Player(25, 25);
        dungeon = new Dungeon(10, 10);
        currentRoom = dungeon.getCurrentRoom();
        font = new BitmapFont();
        font.setColor(Color.BLUE);
        
        score = 0;
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0.5f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //logik
        //flytta spelare
        
        //input
        if(player.isAlive())
        {
            if(Gdx.input.isKeyPressed(Input.Keys.A))
            {
                player.setDirection(-1);
                player.translateX(player.getDirection() * player.getMovementSpeed());
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D))
            {
                player.setDirection(1);
                player.translateX(player.getDirection() * player.getMovementSpeed());
            }
            if(Gdx.input.isKeyPressed(Input.Keys.E))
            {
                player.shoot();
            }
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
            {
                player.jump();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.F))    //använd dörr
            {
                //kolla om spelaren kolliderar med en dörr
                Door doorUsed = null;
                for(Door d : currentRoom.getDoors())
                {
                    if(player.getBoundingRectangle().overlaps(d.getBoundingRectangle()))
                    {
                        doorUsed = d;
                    }
                }
                useDoor(doorUsed);
            }
        }
        
        //DEBUG MODE
        boolean debug = true;
        if(debug)
        {
            if(Gdx.input.isKeyPressed(Input.Keys.F1))
            {
                player.setX(25);
                player.setY(25);
                player.setDY(0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.F2))
            {
                System.out.println("coords: " + player.getX() + "," + player.getY());
                System.out.println("dy: " + player.getDY());
                System.out.println("falling: " + player.isFalling());
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.I))
            {
                dungeon.changeCurrentRoom(0, -1);
                updateCurrentRoom();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.J))
            {
                dungeon.changeCurrentRoom(-1, 0);
                updateCurrentRoom();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.K))
            {
                dungeon.changeCurrentRoom(0, 1);
                updateCurrentRoom();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.L))
            {
                dungeon.changeCurrentRoom(1, 0);
                updateCurrentRoom();
            }
        }
        //gravitation
        if(player.isFalling())
        {
            player.translateY(player.getDY());
            player.setDY(player.getDY() - currentRoom.getGravity());
        }
        else
        {
            player.setDY(0);
        }
        
        //kollisionscheck
        player.setFalling(true);
        Rectangle playerRect = player.getBoundingRectangle();
        for(Wall w : currentRoom.getWalls())
        {
            Rectangle wallRect = w.getBoundingRectangle();

            if(playerRect.overlaps(wallRect))
            {
                Rectangle intersection = new Rectangle();
                Intersector.intersectRectangles(playerRect, wallRect, intersection);

                if(intersection.x > playerRect.x)    //kolliderar med vägg t. höger
                {
                    if(w.getY() + w.getHeight() > player.getY() + player.getHeight() / 4)
                    {
                        //krockar med kanten av lådan
                        player.setX(w.getX() - player.getWidth());
                    }
                    else
                    {
                        player.setY(w.getX() + w.getHeight());
                    }
                }
                else if(intersection.x + intersection.width < playerRect.x + playerRect.width)    //kolliderar med vägg t. vänster
                {
                    if(w.getY() + w.getHeight() > player.getY() + player.getHeight() / 4)
                    {
                        //krockar med kanten av lådan
                        player.setX(w.getX() + w.getWidth());
                    }
                    else
                    {
                        player.setY(w.getX() + w.getHeight());
                    }
                }
                
                if(intersection.y > playerRect.y)    //kolliderar med tak
                {
                    if(player.getDY() > 0)
                    {
                        player.setDY(0);
                    }
                    player.setY(w.getY() - player.getHeight());
                }
                else if(intersection.y + intersection.height < playerRect.y + playerRect.height)  //kolliderar med golv
                {
                    player.setFalling(false);
                    player.setY(w.getY() + w.getHeight() - 1);
                }
            }
        }
        
        //spelarens skott
        List<Projectile> removePlayerProjectiles = new ArrayList<Projectile>();  //man kan inte ta bort ur en lista samtidigt som man itererar igenom den, så skotten sparas i en annan lista och tas bort efter
        for(Projectile p : player.getProjectiles())
        {
            p.move();
            
            //kollision med väggar
            for(Wall w : currentRoom.getWalls())
            {
                if(p.getBoundingRectangle().overlaps(w.getBoundingRectangle()))
                {
                    removePlayerProjectiles.add(p);
                }
            }
            
            //kollision med fiender
            for(Enemy e : currentRoom.getEnemies())
            {
                if(p.getBoundingRectangle().overlaps(e.getBoundingRectangle()))
                {
                    removePlayerProjectiles.add(p);
                    e.takeDamage(1);
                    if(!e.isAlive())
                    {
                        score += 50;
                        System.out.println(dungeon.getAllEnemies().size());
                        if(dungeon.getAllEnemies().isEmpty())
                        {
                            //spelaren har dödat alla fiender, ge stor bonus och avsluta spelet
                            score += 5000;
                            font.setColor(Color.CYAN);
                        }
                    }
                }
            }
        }
        for(Projectile p : removePlayerProjectiles)
        {
            player.deleteProjectile(p);
        }
        
        player.shotCooldown();
        player.damageCooldown();
        
        //flytta fiender
        currentRoom.moveEnemies();
        
        for(Enemy e : currentRoom.getEnemies())
        {
            e.shotCooldown();
            
            //kolla om fienden har krockat med spelaren
            if(player.getBoundingRectangle().overlaps(e.getBoundingRectangle()))
            {
                player.takeDamage(1);
                //e.changeDirection();
            }
            
            //kolla om spelaren har träffats av en fiendes skott
            List<Projectile> removeEnemyProjectiles = new ArrayList<Projectile>();
            for(Projectile p : e.getProjectiles())
            {
                if(player.getBoundingRectangle().overlaps(p.getBoundingRectangle()))
                {
                    removeEnemyProjectiles.add(p);
                    player.takeDamage(1);
                }
            }
            
            //ta bort skott som träffat spelaren
            for(Projectile p : removeEnemyProjectiles)
            {
                e.deleteProjectile(p);
            }
        }
        
        //rendering
        batch.begin();
        
        //dörrar
        for(Door d : currentRoom.getDoors())
        {
            batch.draw(d.getTexture(), d.getX(), d.getY(), d.getWidth(), d.getHeight());
        }
        
        //spelare
        player.draw(batch);
        for(Projectile p : player.getProjectiles())
        {
            p.draw(batch);
        }
        
        //fiender
        List<Enemy> removeEnemies = new ArrayList<Enemy>(); //fiender som ska tas bort
        for(Enemy e : currentRoom.getEnemies())
        {
            if(!e.isAlive())
            {
                removeEnemies.add(e);
            }
            e.draw(batch);
            
            for(Projectile p : e.getProjectiles())
            {
                p.draw(batch);
                //batch.draw(Wall.BRICK_TEXTURE, p.getX(), p.getY(), p.getWidth(), p.getHeight());
            }
        }
        
        //ta bort fiender som är döda
        for(Enemy e : removeEnemies)
        {
            currentRoom.getEnemies().remove(e);
        }
        
        //väggar
        for(Wall w : currentRoom.getWalls())
        {
            batch.draw(w, w.getX(), w.getY(), w.getWidth(), w.getHeight());
        }
        
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 30);
        font.draw(batch, "HP: ", 20, Gdx.graphics.getHeight() - 50);
        for(int i = 0; i < player.getHitpoints(); i++)
        {
            font.draw(batch, "O", 50 + 10 * i, Gdx.graphics.getHeight() - 50);
        }
        
        batch.end();
        
        //slowmotion-effekt om spelaren är död
        if(!player.isAlive())
        {
            try
            {
                TimeUnit.MILLISECONDS.sleep(slowmotionfreeze++);
            }
            catch(Exception e){}
        }
    }

    @Override
    public void dispose ()
    {
        batch.dispose();
    }
    
    private void updateCurrentRoom()
    {
        currentRoom = dungeon.getCurrentRoom();
    }
    
    private void useDoor(Door d)
    {
        if(d != null)
        {
            float middleOfWindow = Gdx.graphics.getHeight() / 2;
            switch(d.getType())
            {
                //upp
                case 0:
                    dungeon.changeCurrentRoom(0, -1);
                    player.setPosition(middleOfWindow - player.getWidth() / 2, Room.EDGE_WALL_THICKNESS);
                    break;

                //ner
                case 1:
                    dungeon.changeCurrentRoom(0, 1);
                    player.setPosition(middleOfWindow - player.getWidth() / 2, Gdx.graphics.getHeight() - Room.EDGE_WALL_THICKNESS - player.getHeight());
                    break;
                //vänster
                case 2:
                    dungeon.changeCurrentRoom(-1, 0);
                    player.setPosition(Gdx.graphics.getWidth() - Room.EDGE_WALL_THICKNESS - player.getWidth(), Room.EDGE_WALL_THICKNESS);
                    break;

                //höger
                case 3:
                    dungeon.changeCurrentRoom(1, 0);
                    player.setPosition(Room.EDGE_WALL_THICKNESS, Room.EDGE_WALL_THICKNESS);
                    break;
            }
            updateCurrentRoom();
        }
    }
}
