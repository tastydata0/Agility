package com.agility.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.agility.game.Game;

public class FlingPiece extends Actor implements Disposable {
    private int x, y;
    private static Sprite sprite;
    private float alpha, scale;

    public FlingPiece(int x, int y) {
        this.x = x;
        this.y = y;
        alpha = 1;
        scale = 1;
    }

    public static void init() {
        Pixmap pixmap = new Pixmap(24,24,Pixmap.Format.RGBA4444);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(x,y);
        sprite.setScale(scale);
        sprite.draw(batch, alpha);
        alpha-=0.06f;
        scale-=0.06f;
        if(alpha <= 0) {
            Game.getUi().getActors().removeValue(this,false);
        }
    }

    @Override
    public void dispose() {

    }
}
