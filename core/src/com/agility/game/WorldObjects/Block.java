package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Block extends Actor {
    private final Sprite tile;
    private final Body body;
    public final int layer;
    private final Vector2 position;
    private final int tileId;


    public Block(TextureRegion tile, Body body, int layer, Vector2 position, int tileId) {
        this.tile = new Sprite(tile);
        this.tile.setPosition(position.x,position.y);
        this.body = body;
        this.layer = layer;
        this.tileId = tileId;
        this.position = position;
        setZIndex(layer);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (tileId != 0 && tileId != 15) {
            tile.draw(batch, parentAlpha);
        }
    }

    public int getLayer() {
        return layer;
    }
}
