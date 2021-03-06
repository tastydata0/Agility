package com.agility.game.UI;

import com.agility.game.Game;
import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ItemEquipRequest extends Actor {

    private static transient Sprite frame;
    private Item item;
    private float scale;


    public ItemEquipRequest() {
        frame = new Sprite(new Texture(Gdx.files.internal("tapToEquip.png")));
        frame.setX(Gdx.graphics.getWidth() - 192);
        frame.setY(Gdx.graphics.getHeight()/2);
        scale = Gdx.graphics.getHeight()/720;
        frame.setScale(scale);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if(x >= frame.getX() && x <= frame.getX()+frame.getWidth() && y >= frame.getY() && y <= frame.getY()+frame.getHeight()) {

            Item nearestItem = getNearestItem();
            if(nearestItem.rangeToHero() <= 50) {
                Game.getHero().equip(nearestItem);
                Game.getUi().tapOnUI = true;
                Game.onGroundItems.remove(nearestItem);
                Game.getStage().getActors().removeValue(nearestItem, false);
            }
        }
        return super.hit(x, y, touchable);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        item = getNearestItem();
        if(item != null && item.rangeToHero() <= 50) {

            frame.setX(Gdx.graphics.getWidth() - frame.getWidth() - 20);
            frame.setY(Gdx.graphics.getHeight()/1.2f - frame.getHeight() - 20);
            frame.draw(batch);
            Sprite itemIcon = new Sprite(item.getIcon());
            itemIcon.setSize(64*scale,64*scale);
            itemIcon.setPosition(frame.getX() + 65*scale, frame.getY() + 60*scale);
            itemIcon.draw(batch);
        }
    }

    private static Item getNearestItem() {
        float closestRange = 9999999;
        Item nearestItem = null;
        for (Item i : Game.onGroundItems) {
            if(i.rangeToHero() < closestRange) {
                closestRange = i.rangeToHero();
                nearestItem = i;
            }
        }
        return nearestItem;
    }
}
