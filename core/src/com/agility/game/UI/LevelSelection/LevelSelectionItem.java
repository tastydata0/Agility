package com.agility.game.UI.LevelSelection;

import com.agility.game.Utils.GameBalanceConstants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.agility.game.Game;

import java.util.zip.ZipEntry;

public class LevelSelectionItem extends Actor implements Comparable<LevelSelectionItem> {

    protected Sprite preview;
    private Sprite locked;
    private Level level;
    private static BitmapFont nameFont;
    public String drawableName;
    private boolean isLocked;
    private boolean unlocked;

    public LevelSelectionItem(Level level) {
        this.level = level;
        int levelNumber = Integer.parseInt(level.getName().split("_")[1]);
        level.setNumber(levelNumber);
        preview = new Sprite(new Texture("maps/" + level.getName()+".png"));
        preview.setSize(512, 288);


        drawableName = level.getName().split("_")[0];

        if(nameFont == null) {
            initResources();
        }
        locked = new Sprite(new Texture(Gdx.files.internal("lockedLevel.png")));
        isLocked = level.getNumber()-1>Game.lastPassedLevel;
        if(unlocked){
            isLocked = false;
        }
    }

    public void setPosition(Vector2 position) {
        preview.setPosition(position.x,position.y);
    }

    @Override
    public int compareTo(LevelSelectionItem levelSelectionItem ) {
        return level.getNumber() - levelSelectionItem.level.getNumber();
    }

    public void hit(float x, float y) {
        if (x >= preview.getX() && x <= preview.getX() + preview.getWidth() &&
                y >= preview.getY() && y <= preview.getY() + preview.getHeight() && !isLocked) {
            // Hit handle
            LevelSelectionItemsHandler.setSelectedItem(this);
            level.start();
        }
    }

    private void scroll(float directedValue) {
        preview.setX(preview.getX() + directedValue);
    }

    private static void initResources() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33OLD.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        parameter.characters = GameBalanceConstants.RUSSIAN_CHARACTERS;
        nameFont = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.begin();
        preview.draw(batch);
        nameFont.draw(batch, drawableName, preview.getX() + 10, preview.getY() + preview.getHeight() + 40);
        if(unlocked){
            isLocked = false;
        }
        if(isLocked) {
            locked.setPosition(preview.getX(), preview.getY());
            locked.draw(batch);
        }
        batch.end();

    }

    public static void dispose() {
        nameFont.dispose();
        // TODO dispose
    }

    @Override
    public float getX() {
        return preview.getX();
    }

    @Override
    public float getY() {
        return preview.getY();
    }
    public void unlock() {
        unlocked = true;
        isLocked = false;
    }
}
