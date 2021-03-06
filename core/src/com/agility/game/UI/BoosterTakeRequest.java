package com.agility.game.UI;

import com.agility.game.Game;
import com.agility.game.WorldObjects.Booster;
import com.agility.game.WorldObjects.Booster;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BoosterTakeRequest extends Actor {
    private static transient Sprite frame;
    private Booster booster;
    private float scale;


    public BoosterTakeRequest() {
        frame = new Sprite(new Texture(Gdx.files.internal("tapToEquip.png")));
        frame.setX(Gdx.graphics.getWidth() - frame.getWidth() - 20);
        frame.setY(Gdx.graphics.getHeight()/1.2f - (frame.getHeight() - 20)*2);
        scale = Gdx.graphics.getHeight()/720;
        frame.setScale(scale);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        if(x >= frame.getX() && x <= frame.getX()+frame.getWidth() && y >= frame.getY() && y <= frame.getY()+frame.getHeight()) {
            Booster nearestBooster = getNearestBooster();
            if(nearestBooster.rangeToHero() <= 25) {
                nearestBooster.activate();
                Game.getUi().tapOnUI = true;
                Game.getStage().getActors().removeValue(nearestBooster, false);
            }
        }
        return super.hit(x, y, touchable);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        booster = getNearestBooster();
        if(booster != null && booster.rangeToHero() <= 25) {

            frame.setX(Gdx.graphics.getWidth() - frame.getWidth() - 20);
            frame.setY(Gdx.graphics.getHeight()/1.2f - (frame.getHeight() + 20)*2);
            frame.draw(batch);
            Sprite itemIcon = booster.getSprite();
            itemIcon.setSize(64*scale,64*scale);
            itemIcon.setPosition(frame.getX() + 65*scale, frame.getY() + 60*scale);
            itemIcon.draw(batch);
        }
    }

    private Booster getNearestBooster() {
        float closestRange = 9999999;
        Booster nearestBooster = null;
        for (Booster i : Game.boosters) {
            if(i.rangeToHero() < closestRange) {
                closestRange = i.rangeToHero();
                nearestBooster = i;
            }
        }
        return nearestBooster;
    }

}
