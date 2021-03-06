package com.agility.game.WorldObjects;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.UI.MoneyMonitor;
import com.agility.game.Utils.AnimationWithOffset;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Random;

public class Coin extends Actor {

    public static final int TYPE_EMERALD = 0;
    public static final int TYPE_SAPPHIRE = 1;
    public static final int TYPE_RUBY = 2;
    public static final int TYPE_EMPTYCOIN = 3;
    public static final int TYPE_COIN = 4;

    private static final Random random = new Random();
    private Vector2 position, velocity;
    private World world;
    private Game game;
    private static final Texture[] atlases = new Texture[8];
    public static final int X_OFFSET = 110;
    public static final int Y_OFFEST = 40;
    private AnimationWithOffset animation;
    private float stateTime;

    protected Vector2 v2Position;
    protected Vector2 v2Velocity = new Vector2();

    private static Sprite[][] sprites = new Sprite[6][atlases.length];

    // Sprite: 84x100

    public Coin(Vector2 position, World world, Game game) {
        this.position = position;
        this.world = world;
        this.game = game;

        velocity = new Vector2((random.nextFloat()+0.2f) * (Game.getHero().getDirection() > 0 ? 2f : -2f),random.nextFloat() * (Math.random() > 0.5f ? 1.5f : -1.5f));
        //velocity = new Vector2((random.nextFloat()+0.2f) * (Math.random() > 0.5f ? 1.5f : -1.5f),random.nextFloat() * (Math.random() > 0.5f ? 1.5f : -1.5f));
        animation = new AnimationWithOffset(new Animation<Sprite>(0.5f,sprites[getRandomType()]),0,0,0);
    }

    public static void loadAtlases() {

        for (int i = 0; i < atlases.length; i++) {
            atlases[i] = new Texture(Gdx.files.internal("itemAtlases/"+i+".gif"));
            for (int j = 0; j < sprites.length-1; j++) {
                sprites[j][i] = new Sprite(new TextureRegion(atlases[i],105+84*j,40,100,84));
                sprites[j][i].setSize(10f/2,8.4f/2);
            }
            sprites[5][i] = new Sprite(new TextureRegion(atlases[i],105+84*4,40+100*3,100,84));
            sprites[5][i].setSize(10f/2,8.4f/2);
        }
        MoneyMonitor.setAnimation(sprites[5]);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        animation.animation.getKeyFrame(stateTime,true).draw(batch,0.7f);
        stateTime+=0.07f;

        if(stateTime >= 2.5f) {
            moveToHero();
            checkForPlayerTouch();
        }
        else {
            velocity.y = velocity.y/1.05f;
            moveNormally();
        }
    }

    private void moveNormally() {
        position.set(position.x+velocity.x,position.y+velocity.y);
        animation.animation.getKeyFrame(stateTime,true).setPosition(position.x-5/1.5f,position.y);
    }

    private void moveToHero() {

        setVelocity(Hero.getPosition().x,Hero.getPosition().y);
        update();
        animation.animation.getKeyFrame(stateTime,true).setPosition(position.x,position.y);

    }

    public void setVelocity (float toX, float toY) {

// The .set() is setting the distance from the starting position to end position
        v2Velocity.set(toX - position.x, toY - position.y);
        v2Velocity.nor(); // Normalizes the value to be used

        v2Velocity.x *= 2;  // Set speed of the object
        v2Velocity.y *= 2;

    }

    public void update() {
        position.add (v2Velocity);    // Update position
    }

    private void checkForPlayerTouch() {
        if(Math.abs(position.x - game.getHero().getBody().getPosition().x) <= 8 &&
                Math.abs(position.y - game.getHero().getBody().getPosition().y) <= 10){
            try {
                if(Math.random() > 0.95) {
                    MoneyMonitor.addDiamonds(random.nextInt(2)+1);
                }
                else {
                    MoneyMonitor.addMoney(random.nextInt(5)+1);
                }

                game.getStage().getActors().removeValue(this, true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init(String request) {

    }

    private static int getRandomType() {
        return random.nextInt(5);
    }
}
