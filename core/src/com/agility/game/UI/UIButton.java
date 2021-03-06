package com.agility.game.UI;

import com.agility.game.Utils.UIButtonEvent;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class UIButton extends Actor {
    private UIButtonEvent event;
    private Sprite icon;

    public UIButton(UIButtonEvent event, Vector2 position, Vector2 size, String iconName) {
        this.event = event;
        icon = new Sprite(new Texture("UI/" + iconName.replaceAll(".png","") + ".png"));
        icon.setPosition(position.x, position.y);
        icon.setSize(size.x, size.y);
    }

    public UIButton(UIButtonEvent event, Vector2 position, float size, String iconName) {
        this(event, position, new Vector2(size,size), iconName);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        icon.draw(batch);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if(x >= icon.getX() && x <= icon.getX()+icon.getWidth() && y >= icon.getY() && y <= icon.getY()+icon.getHeight()) {
            event.handle();
        }
        return super.hit(x, y, touchable);
    }

    public void setIcon(Sprite sprite) {
        sprite.setPosition(icon.getX(), icon.getY());
        sprite.setSize(icon.getWidth(), icon.getHeight());

        icon = sprite;
    }
}
