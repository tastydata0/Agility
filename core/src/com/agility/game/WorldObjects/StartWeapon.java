package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.UI.ItemInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class StartWeapon extends Item {
    private Body body;
    private Vector2 position;
    private Animation<Texture> animation;
    private Texture empty;
    private float stateTime;
    private boolean haveSword = true;

    public StartWeapon(Game game, String iconName, ItemInfo info) {
        super(game, iconName, info);
    }


    public StartWeapon(Vector2 position, World world, Game game, ItemInfo info) {
        super(position,world);
        this.state = info;
        this.position = position;
        setZIndex(0);

        // Body init
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.x = position.x+4;
        def.position.y = position.y;
        body = world.createBody(def);
        body.setSleepingAllowed(true);
        body.setGravityScale(0);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5,0.1f);
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        fixtureDef.density = 1;

        body.createFixture(fixtureDef);
        body.setUserData("startWeapon");
        Texture[] sprites = new Texture[4];
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = new Texture(Gdx.files.internal("startSword/"+i+".png"));
        }
        empty = new Texture(Gdx.files.internal("startSword/4.png"));

        animation = new Animation<Texture>(0.2f,sprites);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(haveSword) {
            batch.draw(animation.getKeyFrame(stateTime, true), position.x - 6, position.y-0.5f, 18, 18);
            stateTime += Gdx.graphics.getDeltaTime();
        }
        else {
            batch.draw(empty, position.x - 6, position.y-0.5f, 18, 18);
        }
    }
    public void removeSword() {
        haveSword = false;
    }
}
