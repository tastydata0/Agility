package com.agility.game.UI;

import com.agility.game.Utils.MusicHandler;
import com.agility.game.Utils.Settings;
import com.agility.game.Utils.UIButtonEvent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.agility.game.Game;

public class PausePanel extends Actor {
    private UIButton resume, exit, rate, soundToggle, graphicsQuality;
    private Vector2 position;
    private Sprite background;
    private final float buttonsY = 10, scale;
    private Game game;
    private UI ui;
    //  sound resume-resume exit

    public PausePanel(Game game, UI ui) {

        this.game = game;
        this.ui = ui;
        scale = Gdx.graphics.getHeight()/720f;
        background = new Sprite(new Texture(Gdx.files.internal("pauseBG.png")));
        background.setSize(background.getWidth()*scale, background.getHeight()*scale);
        position = new Vector2(Gdx.graphics.getWidth()/2 - background.getWidth()/2, Gdx.graphics.getHeight()/2 - background.getHeight()/2);
        background.setPosition(position.x, position.y);

        resume = new UIButton(new UIButtonEvent() {
            @Override
            public void handle() {
                getGame().pause(false);
                Game.getUi().getActors().removeValue(get(), false);
                Game.getUi().getActors().removeValue(resume, false);
                Game.getUi().getActors().removeValue(exit, false);
                Game.getUi().getActors().removeValue(rate, false);
                Game.getUi().getActors().removeValue(soundToggle, false);
                Game.getUi().tapOnUI = true;
            }
        }, new Vector2(getLayoutX(1.5f), position.y + buttonsY), new Vector2(202*scale, 96*scale), "Icon_Play2");
        exit = new UIButton(new UIButtonEvent() {
            @Override
            public void handle() {
                Gdx.app.exit();
            }
        }, new Vector2(getLayoutX(4), position.y + buttonsY), 96*scale, "Icon_QuitRight");

        soundToggle = new UIButton(new UIButtonEvent() {
            @Override
            public void handle() {
                if(Settings.SOUND_ENABLED) {
                    Settings.SOUND_ENABLED = false;
                    soundToggle.setIcon(new Sprite(new Texture(Gdx.files.internal("UI/Vector_SoundMuted.png"))));
                }
                else {
                    Settings.SOUND_ENABLED = true;
                    soundToggle.setIcon(new Sprite(new Texture(Gdx.files.internal("UI/Vector_SoundOn.png"))));
                }
                MusicHandler.refresh();
            }
        }, new Vector2(getLayoutX(0), position.y + buttonsY), 96 * scale, "Vector_SoundOn.png");

        if(!Settings.SOUND_ENABLED) {
            soundToggle.setIcon(new Sprite(new Texture(Gdx.files.internal("UI/Vector_SoundMuted.png"))));
        }
        ui.addActor(resume);
        ui.addActor(exit);
        ui.addActor(soundToggle);
    }

    private float getLayoutX(float order) {
        return position.x + (10 + order * 106) * scale;
    }
    private Game getGame() {
        return game;
    }

    private PausePanel get() {
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        background.draw(batch);
        resume.draw(batch, 1);
        exit.draw(batch, 1);
        soundToggle.draw(batch, 1);
    }
}
