package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

class Door extends Sprite
{
    private static Texture DOOR_TEXTURE = new Texture("textures/door1.jpg");
    
    public static int UP = 0;
    public static int DOWN = 1;
    public static int LEFT = 2;
    public static int RIGHT = 3;
    
    private int type;
    
    public Door(int type)
    {
        super();
        this.type = type;
        setSize(40, 80);
        setTexture(DOOR_TEXTURE);
        
        float middleOfWindow = Gdx.graphics.getWidth() / 2;
        switch(type)
        {
            //Upp
            case 0:
                setX(middleOfWindow - getWidth() / 2);
                setY(Gdx.graphics.getHeight() - Room.EDGE_WALL_THICKNESS - getHeight());
                break;
                
            //Ner
            case 1:
                setX(middleOfWindow - getWidth() / 2);
                setY(Room.EDGE_WALL_THICKNESS);
                break;
                
            //Vänster
            case 2:
                setX(Room.EDGE_WALL_THICKNESS);
                setY(Room.EDGE_WALL_THICKNESS);
                break;
                
            //Höger
            case 3:
                setX(Gdx.graphics.getWidth() - Room.EDGE_WALL_THICKNESS - getWidth());
                setY(Room.EDGE_WALL_THICKNESS);
                break;
        }
    }
    
    public int getType()
    {
        return type;
    }
}
