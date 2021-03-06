package com.agility.game.Utils;

import com.agility.game.Game;
import com.agility.game.UI.UI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class SimpleDirectionGestureDetector extends GestureDetector implements InputProcessor {
    private Vector2 touchPos;
    public static boolean shift = false,touchOnUI;
    public static int flingButton, touchButton;
    public interface DirectionListener {
        void onLeft();

        void onRight();

        void onUp();

        void onDown();

        void onRightDown();

        void onLeftDown();

        void onTouch();
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPos = new Vector2(x,y);
        shift = true;
        touchButton = button;
        touchOnUI = Game.tap(x,Gdx.graphics.getHeight()-y);
        return super.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean touchUp(float x, float y, int pointer, int button) {

        try {
            if (Math.abs(touchPos.x - x) <= 5 && Math.abs(touchPos.y - y) <= 5 && !touchOnUI) {
                DirectionGestureListener.directionListener.onTouch();
            }
            shift = false;
            touchButton = -1;
        }
        catch (Exception e) {

        }
        touchOnUI = false;
        Game.removeFinger(x, Gdx.graphics.getHeight()-y);
        return super.touchUp(x, y, pointer, button);
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        Game.getUi().addFlingPiece(x, Gdx.graphics.getHeight() - y);
        return super.touchDragged(x, y, pointer);
    }

    public SimpleDirectionGestureDetector(DirectionListener directionListener, Game game) {
        super(new DirectionGestureListener(directionListener));
    }

    public static class DirectionGestureListener extends GestureAdapter {
        public static DirectionListener directionListener;

        public DirectionGestureListener(DirectionListener directionListener){
            this.directionListener = directionListener;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            //com.agility.game.Game.log("vX: "+velocityX+"   vY: "+velocityY);

            if(Math.abs(velocityX) > 1200 && velocityY > 1200) {
                if(velocityX>0) {
                    directionListener.onRightDown();
                }
                else {
                    directionListener.onLeftDown();
                }
            }
            else {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 5) {
                        directionListener.onRight();
                    } else if (velocityX < -5) {
                        directionListener.onLeft();
                    }
                } else {
                    if (velocityY > 5) {
                        directionListener.onDown();
                    } else if (velocityY < -5) {
                        directionListener.onUp();
                    }
                }
            }
            flingButton = button;

            return super.fling(velocityX, velocityY, button);
        }

    }


}