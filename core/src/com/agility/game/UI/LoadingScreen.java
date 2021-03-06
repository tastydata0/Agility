package com.agility.game.UI;

import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.SpritePack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class LoadingScreen extends Actor {
    private float stateTime;
    private static AnimationWithOffset animation;
    private boolean draw = true;

    public LoadingScreen() {
        if(animation == null) {
            init();
        }
    }

    private static void init() {
        animation = new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("loadingScreen/loading",4).content),0,0, 0);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0,0,0,1);
        stateTime += Gdx.graphics.getDeltaTime();
        Sprite currentFrame = animation.animation.getKeyFrame(stateTime,true);
        currentFrame.draw(batch);
    }

    public void stopDrawing() {
        draw = false;
    }
}
