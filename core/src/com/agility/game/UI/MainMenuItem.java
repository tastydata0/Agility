package com.agility.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuItem extends Actor {
    private MenuItemEvent event;
    private Texture texture;
    private float y;
    private float scale;

    public static void init() {

    }

    public MainMenuItem(MenuItemEvent event, Texture texture, float y) {
        this.event = event;
        this.texture = texture;
        setBounds(100,y,texture.getWidth(),texture.getHeight());
        this.y = y;
        scale = 720f/Gdx.graphics.getHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture,100,y,texture.getWidth()*scale,texture.getHeight()*scale);
    }

    public void touchDown(int x, int y) {
        if(x>=100 && x <= 100+texture.getWidth()*scale && y>=this.y && y <= this.y+texture.getHeight()*scale) {
            hit();
        }
    }


    public void hit() {
        event.handle();
    }
}
