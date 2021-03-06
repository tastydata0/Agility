package com.agility.game.Utils;

import com.agility.game.MainMenu;
import com.agility.game.UI.LevelSelection.LevelSelectionMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class MainMenuInputProcessor implements InputProcessor {
    private MainMenu mainMenu;
    private LevelSelectionMenu levelSelectionMenu;

    public MainMenuInputProcessor(MainMenu mainMenu, LevelSelectionMenu levelSelectionMenu) {
        this.mainMenu = mainMenu;
        this.levelSelectionMenu = levelSelectionMenu;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mainMenu.touchDown(screenX,Gdx.graphics.getHeight()-screenY,pointer,button);
        levelSelectionMenu.touchDown(screenX,Gdx.graphics.getHeight()-screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        levelSelectionMenu.touchUp(screenX,Gdx.graphics.getHeight()-screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
