package com.agility.game;

import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.SoundPlayer;
import com.agility.game.Utils.SpritePack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Spell extends Actor {
    private final Vector2 position;
    boolean stunningHero;
    float stateTime, duration = 2f;
    int direction;
    static AnimationWithOffset animation;
    Game game;
    static Sound sound;

    public Spell(boolean stunningHero, Vector2 position, int direction, Game game) {
        this.stunningHero = stunningHero;
        this.game = game;
        this.direction = direction;
        this.position = position;
        if(animation == null) {
            animation = new AnimationWithOffset(new Animation<Sprite>(duration / 20f, new SpritePack("lighting/lighting", 20).content), 0, 0, 0);
        }
        for (int i = 0; i < animation.animation.getKeyFrames().length; i++) {
            animation.animation.getKeyFrames()[i].setPosition(position.x, position.y - 6);
            animation.animation.getKeyFrames()[i].setSize(100, 24f);
            animation.animation.getKeyFrames()[i].setFlip(direction == -1, false);
        }
        setZIndex(10);
        if(sound == null) {
            sound = Gdx.audio.newSound(Gdx.files.internal("sounds/lighting.ogg"));
        }
        SoundPlayer.play(sound);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime();
        if(stateTime >= duration) {
            Game.getStage().getActors().removeValue(this,false);
            Game.getHero().endSpell();
        }
        animation.animation.getKeyFrame(stateTime,false).draw(batch);
        checkForTouching();
    }

    private void checkForTouching() {
        for (Enemy e : game.getEnemies()) {
            if(e.getBody().getPosition().x > position.x && e.getBody().getPosition().x <= position.x + 100) {
                if(Math.abs(e.getBody().getPosition().y - position.y) <= 12) {
                    touch(e);
                }
            }
        }
    }

    private void touch(Enemy e) {
        e.damage(Game.getHero().getWeapon().getParameter1()/20f, false);
    }

    public boolean isCasting() {
        return stateTime < duration;
    }
}
