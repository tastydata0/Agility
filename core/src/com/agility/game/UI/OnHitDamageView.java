package com.agility.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Random;

public class OnHitDamageView extends Actor {
    private static BitmapFont font, fontCritical;
    private float opacity = 0.999f;
    private int value;
    private Vector2 position,velocity;
    private static final Random random = new Random();
    private Color color = new Color(0xff5555ff);
    private boolean critical;

    public OnHitDamageView(int value, Vector2 position, boolean critical) {
        this.value = value;
        this.position = position;
        this.critical = critical;
        float vx = 0, vy = 0;
        do {
            int signX = Math.random() < 0.5? -1 : 1;
            int signY = Math.random() < 0.5? -1 : 1;
            vx = random.nextFloat()* 4 * signX;
            vy = random.nextFloat()* 4 * signY;
        } while (Math.abs(vx) > 0.1f && Math.abs(vy) > 0.1f);
        velocity = new Vector2(vx,vy);
    }

    public static void init() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("stacked pixel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);

        parameter.size = 48;
        parameter.color = new Color(0xff5555ff);
        fontCritical = generator.generateFont(parameter);

        generator.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!critical) {
            font.setColor(1, 1, 1, opacity);
            font.draw(batch,value+"",position.x,position.y);
        }
        else {
            fontCritical.setColor(color.r,color.g,color.b,opacity);
            fontCritical.draw(batch,value+"",position.x,position.y);
        }

        position.x += velocity.x;
        position.y += velocity.y;
        if(!critical) {
            opacity -= (1 - opacity) / 4;
        }
        else {
            opacity -= (1 - opacity) / 10;
        }
        if(opacity <= 0) {
            getStage().getActors().removeValue(this,false);
        }
    }
}
