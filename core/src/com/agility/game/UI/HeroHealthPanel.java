package com.agility.game.UI;

import com.agility.game.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HeroHealthPanel extends Actor {
    private Sprite heart, heartFill;
    private Game game;

    public HeroHealthPanel(Game game) {
        super();
        this.game = game;
        heart = new Sprite(new Texture(Gdx.files.internal("heart.png")));
        heartFill = new Sprite(new Texture(Gdx.files.internal("heartFill.png")));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int allHearts = (int)(game.getHero().getMaxHealth()/200);
        float modHeart = game.getHero().getMaxHealth()/200 - (int)(game.getHero().getMaxHealth()/200);

        for (int i = 0; i < allHearts; i++) {
            heart.setPosition(40+30*i,Gdx.graphics.getHeight()-60);
            heart.draw(batch);
        }
        heart.setPosition(40+30*allHearts,Gdx.graphics.getHeight()-60);
        heart.draw(batch,modHeart);
        int fullHearts = (int)(game.getHero().getHealth()/200);
        float modHeartFill = game.getHero().getHealth()/200 - (int)(game.getHero().getHealth()/200);

        for (int i = 0; i < fullHearts; i++) {
            heartFill.setPosition(40+30*i,Gdx.graphics.getHeight()-60);
            heartFill.draw(batch);
        }
        heartFill.setPosition(40+30*fullHearts,Gdx.graphics.getHeight()-60);
        heartFill.draw(batch,modHeartFill);

    }
}
