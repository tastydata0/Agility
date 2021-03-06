package com.agility.game.UI;

import com.agility.game.Hero;
import com.agility.game.WorldObjects.Item;
import com.agility.game.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

import java.io.Serializable;

public class ItemInfo extends Actor implements Disposable, Serializable {
    public static final int TYPE_WEAPON = 0;
    public static final int TYPE_ARMOR  = 1;

    private boolean vanish = true;
    private transient Sprite itemIcon;
    private transient static BitmapFont nameFont, info;
    private String name;
    private int type,vanishTimer = 480;
    private int parameter1;
    private float parameter2;
    //private transient Game Game;
    private int level;
    private Item item;

    public ItemInfo() {
    }

    public ItemInfo(int type, String name, int parameter1, float parameter2, int level) {
        this.type = type;
        this.name = name;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.level = level;
        setName("itemInfo");
    }



    public void setItem(Item item) {
        this.item = item;
        this.name = item.getName();
        this.parameter1 = item.getParameter1();
        this.parameter2 = item.getParameter2();
        this.level = item.getLevel();
    }



    @Override
    public void dispose() {

    }

    @Override
    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getParameter1() {
        return parameter1;
    }

    public float getParameter2() {
        return parameter2;
    }

    public int getLevel() {
        return level;
    }

    public void setParameter1(int parameter1) {
        this.parameter1 = parameter1;
    }

    public void setParameter2(float parameter2) {
        this.parameter2 = parameter2;
    }
}
