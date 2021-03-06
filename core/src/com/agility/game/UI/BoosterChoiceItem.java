package com.agility.game.UI;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.WorldObjects.Booster;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class BoosterChoiceItem extends Actor {
    public static final int COINS = 0;
    public static final int DIAMONDS = 1;
    public static final int HEALTH = 2;
    public static final int MAX_HEALTH = 3;
    public static final int DAMAGE = 4;
    public static final int CRITICAL_STRIKE = 5;

    private int kind, value;
    private UI ui;
    private Sprite background, foreground;

    public BoosterChoiceItem(int kind, int value, int xOrder, UI ui) {
        this.kind = kind;
        this.value = value;
        this.ui = ui;


        int bgWidth =  Gdx.graphics.getWidth()/3;
        int bgHeight = Gdx.graphics.getHeight()/3;
        Pixmap bgPixmap = new Pixmap(bgWidth,bgHeight,Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.BLACK);
        bgPixmap.fill();
        int rangeBetweenItems = 10;
        background = new Sprite(new Texture(bgPixmap));
        if(xOrder == 0) {
            background.setX(Gdx.graphics.getWidth()/6 - rangeBetweenItems / 2);
        }
        else {
            background.setX(Gdx.graphics.getWidth()/2 + rangeBetweenItems / 2);
        }
        background.setY(Gdx.graphics.getHeight()/8);

        switch (kind) {
            case COINS:
                foreground = new Sprite(new Texture(Gdx.files.internal("coins+.png")));
                break;
            case DIAMONDS:
                foreground = new Sprite(new Texture(Gdx.files.internal("diamonds+.png")));
                break;
            case HEALTH:
                foreground = new Sprite(new Texture(Gdx.files.internal("heal.png")));
                break;
            case MAX_HEALTH:
                foreground = new Sprite(new Texture(Gdx.files.internal("maxHealth+.png")));
                break;
            case DAMAGE:
                foreground = new Sprite(new Texture(Gdx.files.internal("damage+.png")));
                break;
            case CRITICAL_STRIKE:
                foreground = new Sprite(new Texture(Gdx.files.internal("critical+.png")));
                break;
        }

        foreground.setX(background.getX()+background.getWidth()/2-foreground.getWidth()/2);
        foreground.setY(background.getY()+background.getHeight()/2-foreground.getHeight()/2);
        ui.addActor(this);


    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if(x >= background.getX() && x <= background.getX()+background.getWidth() && y >= background.getY() && y <= background.getY()+background.getHeight()) {
            Game.getUi().tapOnUI = true;
            switch (kind) {
                case COINS:
                    MoneyMonitor.addMoney(value);
                    break;
                case DIAMONDS:
                    MoneyMonitor.addDiamonds(value);
                    break;
                case HEALTH:
                    Game.getHero().heal(value);
                    break;
                case MAX_HEALTH:
                    Game.getHero().increaseMaxHealth(value);
                    break;
                case DAMAGE:
                    Game.getHero().increaseDamage(value);
                    break;
                case CRITICAL_STRIKE:
                    Game.getHero().increaseCriticalStrike(value);
                    break;
            }
            ui.getGame().getCurrentBoosterChoice().remove();
        }

        return super.hit(x, y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        background.draw(batch,0.5f);
        foreground.draw(batch);
    }
}
