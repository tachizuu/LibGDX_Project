package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import static com.badlogic.gdx.graphics.Texture.TextureWrap.Repeat;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Wall extends Sprite
{
    public static final Texture BRICK_TEXTURE = new Texture("textures/brick1.png");
            
    public Wall(int x1, int x2, int y1, int y2)
    {
        super(BRICK_TEXTURE, (x2 - x1), (y2 - y1));
        BRICK_TEXTURE.setWrap(Repeat, Repeat);
        this.setX(x1);
        this.setY(y1);
    }
}
