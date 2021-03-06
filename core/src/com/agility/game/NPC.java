package com.agility.game;

import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class NPC extends Actor {

    // Services
    public static NPCService weaponSell = new NPCService() {
        @Override
        public void service() {

        }

        @Override
        public String assetName() {
            return "";
        }

        @Override
        public int cost() {
            return 1;
        }
    };

    public static NPCService weaponUpgrade = new NPCService() {
        @Override
        public void service() {
            Game.getHero().getWeapon().upgrade();
        }

        @Override
        public String assetName() {
            return "";
        }

        @Override
        public int cost() {
            return 1;
        }
    };

    public static NPCService bookSpellCast = new NPCService() {
        @Override
        public void service() {

        }

        @Override
        public String assetName() {
            return "";
        }

        @Override
        public int cost() {
            return 1;
        }
    };

    public static NPCService manaFill = new NPCService() {
        @Override
        public void service() {
            Game.getHero().setMana(1);
        }

        @Override
        public String assetName() {
            return "";
        }

        @Override
        public int cost() {
            return 1;
        }
    };
    private AnimationWithOffset animation;
    private NPCService service;
    private Sprite card;
    private Vector2 position;
    private float stateTime;

    public NPC(AnimationWithOffset animation, NPCService service, Vector2 position) {
        this.animation = animation;
        this.service = service;
        this.position = position;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime();

        Sprite currentFrame = animation.animation.getKeyFrame(stateTime, true);
        currentFrame.setPosition(position.x + animation.defaultXOffset, position.y + animation.yOffset);
        currentFrame.draw(batch);
    }
}
