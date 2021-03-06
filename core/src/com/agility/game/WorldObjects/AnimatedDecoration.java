package com.agility.game.WorldObjects;

import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.SpritePack;
import com.agility.game.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class AnimatedDecoration extends Decoration {
    private AnimationWithOffset animation;
    private float stateTime;
    private Sprite currentFrame;

    public AnimatedDecoration(String name, Vector2 position, float frameDuration, int framesCount) {
        super(name, position);
        setPosition(position.x, position.y);
        animation = new AnimationWithOffset(new Animation<Sprite>(frameDuration,new SpritePack("objects/"+name,framesCount).content),0,0, 0);
        for (int i = 0; i < animation.animation.getKeyFrames().length; i++) {
            animation.animation.getKeyFrames()[i].setSize(animation.animation.getKeyFrames()[i].getWidth()*0.5f, animation.animation.getKeyFrames()[i].getHeight()*0.5f);
            animation.animation.getKeyFrames()[i].setPosition(position.x, position.y);
        }
    }

    public AnimatedDecoration(String name, Vector2 position, float frameDuration, int framesCount, float yOffset) {
        super(name, position);
        setPosition(position.x, position.y);
        animation = new AnimationWithOffset(new Animation<Sprite>(frameDuration,new SpritePack("objects/"+name,framesCount).content),0,0, 0);
        for (int i = 0; i < animation.animation.getKeyFrames().length; i++) {
            animation.animation.getKeyFrames()[i].setSize(animation.animation.getKeyFrames()[i].getWidth()*0.5f, animation.animation.getKeyFrames()[i].getHeight()*0.5f);
            animation.animation.getKeyFrames()[i].setPosition(position.x, position.y+yOffset);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(Math.abs(Game.camera.position.x - getX()) < Game.camera.viewportWidth/1.75f &&
                Math.abs(Game.camera.position.y - getY()) < Game.camera.viewportHeight/1.75f) {
            animation.animation.getKeyFrame(stateTime, true).draw(batch);
        }
        stateTime += Gdx.graphics.getDeltaTime();
    }
}
