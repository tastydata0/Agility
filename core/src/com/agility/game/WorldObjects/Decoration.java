package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Decoration extends Actor {
    private Sprite sprite;

    public Decoration(String name, Vector2 position) {
        setPosition(position.x,position.y);
        try {
            sprite = new Sprite(new Texture(Gdx.files.internal("objects/" + name + ".png")));
            sprite.setPosition(position.x, position.y);
            sprite.setSize(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
        }
        catch (Exception e){

        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(Math.abs(Game.camera.position.x - getX()) < Game.camera.viewportWidth/1.75f &&
                Math.abs(Game.camera.position.y - getY()) < Game.camera.viewportHeight/1.75f) {
            sprite.draw(batch);
        }
    }
}
