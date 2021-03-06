package com.agility.game;

import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.EnemyDef;
import com.agility.game.Utils.SpritePack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Boss extends Enemy {
    private AnimationWithOffset shield;
    private boolean shieldEnabled;
    private int shieldTimer;
    public Boss(EnemyDef def, World world, Vector2 position, Game game) {
        super(def, world, position, game);
        maxHealth = Game.getHero().getWeapon().getParameter1()*80;
        health = maxHealth;
        shield = new AnimationWithOffset(new Animation<Sprite>(0.12f,new SpritePack("effects/bossShield",4).content),0,-16, -16);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        shieldEnabled = false;
        if(shieldTimer >= 0) {
            shieldEnabled = true;
            if(shieldTimer >= 600) {
                shieldTimer = -400;
            }
        }
        shieldTimer++;
        if(shieldEnabled && isDied() == false) {
            handleShield(batch);
        }
    }

    @Override
    public void damage(float deal) {
        if(shieldEnabled && shieldTimer >= 40) {
            Game.getHero().blow();
            Game.getHero().damage(30);
        }
        else {
            super.damage(deal);
        }
    }

    @Override
    protected void drawHealthBar(Batch batch) {

        if(health>0) {
            Game.getUi().drawBossHealth((health/maxHealth),hpbg,hpfg);
        }
    }

    protected void handleShield(Batch batch) {
        Sprite currentShieldSprite = shield.animation.getKeyFrame(stateTime,true);
        currentShieldSprite.setPosition(getBody().getPosition().x-27, getBody().getPosition().y-20);
        currentShieldSprite.draw(batch);
    }

    protected void init(String request) {
        if(request.equals("body")) {
            BodyDef def = new BodyDef();
            def.gravityScale = 70;
            def.type = BodyDef.BodyType.DynamicBody;
            def.position.x = position.x;
            def.position.y = position.y;

            body = world.createBody(def);
            body.setFixedRotation(true);
            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            Vector2[] points = {
                    new Vector2(0,25),
                    new Vector2(0,1),
                    new Vector2(2,0),
                    new Vector2(8,0),
                    new Vector2(10,1),
                    new Vector2(10,25)
            };
            shape.set(points);
            fixtureDef.shape = shape;
            fixtureDef.density = 0.9f;
            fixtureDef.friction = 1f;

            body.createFixture(fixtureDef);
            body.setUserData("enemy");

        }
        else if(request.equalsIgnoreCase("animations")) {


        }
    }
}
