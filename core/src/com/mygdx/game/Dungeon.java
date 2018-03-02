package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class Dungeon
{
    private final int width;
    private final int height;
    private Room rooms[][];
    private int currentRoomX;
    private int currentRoomY;
    
    
    public Dungeon(int w, int h)
    {
        this.width = w;
        this.height = h;
        rooms = new Room[height][width];
        
        //skapa rum
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                rooms[y][x] = new Room();
            }
        }
        
        //lägg till dörrar
        for(int y = 0; y < rooms.length; y++)
        {
            for(int x = 0; x < rooms[y].length; x++)
            {
                do
                {
                    if(x < rooms[y].length - 1)
                    {
                        //rummet har möjlighet att ha en höger-dörr
                        if(Helpers.random(0, 100) <= 55)    //55% chans att generera en dörr
                        {
                            rooms[y][x].addDoor(new Door(Door.RIGHT));
                            rooms[y][x + 1].addDoor(new Door(Door.LEFT));
                        }
                    }

                    if(y < rooms.length - 1)
                    {
                        //rummet har möjlighet att ha en nedåt-dörr
                        if(Helpers.random(0, 100) <= 55)    //55% chans att generera en dörr
                        {
                            rooms[y][x].addDoor(new Door(Door.DOWN));
                            rooms[y + 1][x].addDoor(new Door(Door.UP));
                        }
                    }
                }while(rooms[y][x].getDoors().size() <= 0);
            }
        }
    }
    
    public int getCurrentRoomX()
    {
        return currentRoomX;
    }
    
    public int getCurrentRoomY()
    {
        return currentRoomY;
    }
    
    public Room getCurrentRoom()
    {
        return rooms[currentRoomY][currentRoomX];
    }
    
    public void setCurrentRoom(int x, int y)
    {
        if(!(x < 0 || x >= width || y < 0 || y >= height))
        {
            currentRoomX = x;
            currentRoomY = y;
        }
    }
    
    public void changeCurrentRoom(int x, int y)
    {
        //denna metod används för att "förflytta" sig genom rummen, så vill man
        //ett rum till höger anropar man changeCurrentRoom(1, 0) etc etc
        currentRoomX += x;
        currentRoomY += y;
        if(currentRoomX < 0)
        {
            currentRoomX = 0;
        }
        if(currentRoomX >= width)
        {
            currentRoomX = width - 1;
        }
        if(currentRoomY < 0)
        {
            currentRoomY = 0;
        }
        if(currentRoomY >= height)
        {
            currentRoomY = height - 1;
        }
    }
    
    public List<Enemy> getAllEnemies()
    {
        List<Enemy> allEnemies = new ArrayList<Enemy>();
        
        for(Room[] roomRow : rooms)
        {
            for(Room room : roomRow)
            {
                allEnemies.addAll(room.getEnemies());
            }
        }
        
        return allEnemies;
    }
}