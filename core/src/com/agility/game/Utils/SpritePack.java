package com.agility.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class SpritePack {
    public Sprite[] content;
    public int iteration;
    public SpritePack(String spriteName, int count) {
        content = new Sprite[count];
        for (int i = 0; i < count; i++) {
            Texture buffer = new Texture(Gdx.files.internal(spriteName+"-0"+i+".png"));
            content[i] =  new Sprite(buffer);
            //content[i].setSize(50/2,37/2);
        }
    }

    public SpritePack(String spriteName, int count, float scale) {
        content = new Sprite[count];
        for (int i = 0; i < count; i++) {
            Texture buffer = new Texture(Gdx.files.internal(spriteName+"-0"+i+".png"));
            content[i] = new Sprite(buffer);
            content[i].setSize(content[i].getWidth()*scale, content[i].getHeight()*scale);
        }
    }

    public SpritePack(String spriteName, int count, Vector2 size) {
        content = new Sprite[count];
        for (int i = 0; i < count; i++) {
            Texture buffer = new Texture(Gdx.files.internal(spriteName+"-0"+i+".png"));
            content[i] = new Sprite(buffer);
            content[i].setSize(size.x, size.y);
        }
    }
}
