package com.agility.game.WorldObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.agility.game.Game;

public class Bullet extends Actor {
    private Body body;
    private Vector2 velocity;
    private int direction;
    private Sprite sprite;
    private float stateTime;
    private boolean destroy;
    private boolean alreadyDestroyed;

    public Bullet(Body body, String spriteName, Vector2 velocity, int direction) {
        this.body = body;
        this.velocity = velocity;
        this.direction = direction;
        this.sprite = new Sprite(new Texture(Gdx.files.internal(spriteName)));
        sprite.setFlip(direction == -1, false);
        sprite.setScale(0.8f);

        body.setActive(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(body.getPosition().x-sprite.getWidth()/2, body.getPosition().y-sprite.getHeight()/2);
        sprite.draw(batch);

        body.setLinearVelocity(velocity.x, velocity.y);
        stateTime+=Gdx.graphics.getDeltaTime();

        if(stateTime >= 0.1f && !body.isActive()) {
            body.setActive(true);
        }
        if(destroy && !alreadyDestroyed) {
            alreadyDestroyed = true;
            Game.getMainWorld().destroyBody(body);
            Game.getBullets().remove(this);
            Game.getStage().getActors().removeValue(this,false);
        }
    }

    public Body getBody() {
        return body;
    }

    public void destroy() {
        if(stateTime >= 0.2f) {
            destroy = true;
        }

    }
}
