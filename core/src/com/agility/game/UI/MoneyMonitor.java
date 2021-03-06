package com.agility.game.UI;

import com.agility.game.Utils.AnimationWithOffset;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MoneyMonitor extends Actor {
    public static MoneyMonitor instance = new MoneyMonitor();
    private static float alpha = 0.99999f,stateTime;
    private static Sprite currentFrame, diamond;
    private static Animation<Sprite> animation;
    private static BitmapFont font;

    private static int money;
    private static int diamonds;

    private MoneyMonitor() {

    }

    public static void setAnimation(Sprite[] frames) {
        for (int i = 0; i < frames.length; i++) {
            frames[i].setSize(100f/2,84f/2);
            frames[i].setPosition(34,140);
        }
        animation = new Animation<Sprite>(0.2f,frames);
        diamond = new Sprite(new Texture(Gdx.files.internal("diamond.png")));
        diamond.setSize(32,32);
        diamond.setPosition(39,80);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("stacked pixel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
    }

    public static void addMoney(int money) {
        MoneyMonitor.money += money;
        update();
    }

    public static void addDiamonds(int value) {
        MoneyMonitor.diamonds += value;
        update();
    }

    public static void setCoins(int money) {
        MoneyMonitor.money = money;
    }

    public static void setDiamonds(int diamonds) {
        MoneyMonitor.diamonds = diamonds;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        animation.getKeyFrame(stateTime,true).draw(batch,alpha);
        diamond.draw(batch,alpha);
        font.setColor(1,1,1,alpha);
        font.draw(batch,money+"",90,168);
        font.draw(batch,diamonds+"",90,107);
        stateTime+=Gdx.graphics.getDeltaTime();
        if(alpha > 0.4f) alpha-=(1-alpha)/20;
    }

    private static void update() {
        alpha = 0.99999f;
    }

    public static int getMoney() {
        return money;
    }

    public static int getDiamonds() {
        return diamonds;
    }
}
