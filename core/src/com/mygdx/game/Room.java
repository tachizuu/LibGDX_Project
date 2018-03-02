package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Room
{
    private List<Wall> walls = new ArrayList<Wall>();
    private List<Enemy> enemies = new ArrayList<Enemy>();
    private List<Door> doors = new ArrayList<Door>();
    
    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();
    public static final int EDGE_WALL_THICKNESS = 20;
    private static float GRAVITY = 1f;
    
    public Room()
    {
        JsonValue levels = getLevels();
        if(levels == null)
        {
            levels = null;//backup-level ifall det inte går att läsa nivåerna
        }
        
        int levelNum = (int)Helpers.random(0, levels.get("level").size - 1);
        JsonValue level = levels.get("level").get(levelNum);
        walls.addAll(readWallsFromJson(level));
        enemies.addAll(readEnemiesFromJson(level));
        
        //skapa kontur-väggar(dessa ska renderas sist och läggs därför sist i listan
        Wall northWall = new Wall(-100, screenWidth + 100, screenHeight - EDGE_WALL_THICKNESS, screenHeight + 100);
        Wall southWall = new Wall(-100, screenWidth + 100, -100, EDGE_WALL_THICKNESS);
        Wall westWall = new Wall(-100, EDGE_WALL_THICKNESS, -100, screenHeight + 100);
        Wall eastWall = new Wall(screenWidth - EDGE_WALL_THICKNESS, screenWidth + 100, -100, screenHeight + 100);
        walls.add(northWall);
        walls.add(southWall);
        walls.add(westWall);
        walls.add(eastWall);
        
    }
    
    public List<Wall> getWalls()
    {
        return walls;
    }
    
    public List<Enemy> getEnemies()
    {
        return enemies;
    }
    
    public List<Door> getDoors()
    {
        return doors;
    }
    
    public void addDoor(Door door)
    {
        doors.add(door);
        if(door.getType() == 0) //upp-dörr
        {
            //skapa en plattform under dörren
            int middleOfWindow = Gdx.graphics.getWidth() / 2;
            Wall w = new Wall(middleOfWindow - 50, middleOfWindow + 50, (int)door.getY() - 30, (int)door.getY());
            walls.add(w);
        }
    }
    
    public void moveEnemies()
    {
        for(Enemy e : enemies)
        {
            //kollisionscheck
            e.setFalling(true);
            for(Wall w : walls)
            {
                Rectangle enemyRect = e.getBoundingRectangle();
                Rectangle wallRect = w.getBoundingRectangle();
                
                if(enemyRect.overlaps(wallRect))
                {
                    Rectangle intersection = new Rectangle();
                    Intersector.intersectRectangles(enemyRect, wallRect, intersection);
                    
                    if(intersection.x > enemyRect.x)    //kolliderar med vägg t. höger
                    {
                        e.changeDirection();
                    }
                    else if(intersection.x + intersection.width < enemyRect.x + enemyRect.width)    //kolliderar med vägg t. vänster
                    {
                        e.changeDirection();
                    }

                    if(intersection.y > enemyRect.y)    //kolliderar med tak
                    {
                        
                    }
                    else if(intersection.y + intersection.height < enemyRect.y + enemyRect.height)  //kolliderar med golv
                    {
                        e.setFalling(false);
                    }
                }
            }
            //gravitation och förflyttning
            if(e.isFalling())
            {
                e.translateY(e.getDY());
                e.setDY(e.getDY() - GRAVITY);
            }
            else
            {
                e.translateX(e.getMovementSpeed() * e.getDirection());
            }
            
            //flytta fiendens skott & kolla om skott har krockat med vägg
            List<Projectile> removeList = new ArrayList<Projectile>();  //man kan inte ta bort ur en lista samtidigt som man itererar igenom den, så skotten sparas i en annan lista och tas bort efter
            for(Projectile p : e.getProjectiles())
            {
                p.move();
                
                for(Wall w : walls)
                {
                    if(p.getBoundingRectangle().overlaps(w.getBoundingRectangle()))
                    {
                        removeList.add(p);
                    }
                }
            }
            for(Projectile p : removeList)
            {
                e.deleteProjectile(p);
            }
            
            e.shoot();
        }
    }
    
    public float getGravity()
    {
        return GRAVITY;
    }
    
    private List<Wall> readWallsFromJson(JsonValue level)
    {
        List<Wall> walls = new ArrayList<Wall>();
        for(JsonValue w : level.get("wall"))
            {
                int x1 = w.getInt("x1");
                int x2 = w.getInt("x2");
                int y1 = w.getInt("y1");
                int y2 = w.getInt("y2");
                Wall newWall = new Wall(x1, x2, y1, y2);
                walls.add(newWall);
            }
        return walls;
    }
    
    private List<Enemy> readEnemiesFromJson(JsonValue level)
    {
        
        
        List<Enemy> enemies = new ArrayList<Enemy>();
        for(JsonValue e : level.get("enemy"))
        {
            int x = e.getInt("x");
            int y = e.getInt("y");
            Enemy newEnemy = new Enemy(x, y);
            enemies.add(newEnemy);
        }
        return enemies;
    }
    
    private JsonValue getLevels()
    {
        try
        {
            String requestURL = "http://localhost:8080/Level_Creator/api/Levels";
            URL levelRequest = new URL(requestURL);
            
            Scanner scanner = new Scanner(levelRequest.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            
            JsonReader jsonReader = new JsonReader();
            JsonValue levels = jsonReader.parse(response);
            
            scanner.close();
            return levels;
        }
        catch(Exception e)
        {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }
}
