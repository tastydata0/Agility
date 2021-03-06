package com.agility.game;

import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;

public class Inventory extends Actor {
    private final ArrayList<Item> content = new ArrayList<Item>();

    public boolean add(Item item) {
        content.add(item);
        return true;
    }
    public ArrayList<Item> get() {
        return content;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

    }
}
