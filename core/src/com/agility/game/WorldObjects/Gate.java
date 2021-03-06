package com.agility.game.WorldObjects;

import com.agility.game.Hero;
import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.KillsCounter;
import com.agility.game.Utils.SpritePack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.agility.game.Game;

public class Gate extends Actor {
    private Body body;
    private AnimationWithOffset animation;
    private Vector2 position;
    private int needToKill;
    private float stateTime;
    private World world;
    private boolean opened = false;
    private BitmapFont font;
    private Sprite skull;

    public Gate(Vector2 position, World world, float opensWithKillsPart, int enemiesCount) {
        this.position = position;
        this.world = world;
        setZIndex(0);

        needToKill = (int)(enemiesCount*opensWithKillsPart);
        if(needToKill == 0) {
            needToKill = 1; // Boss level
        }

        BodyDef def = new BodyDef();
        def.gravityScale = 70;
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.x = position.x;
        def.position.y = position.y;
        def.bullet = true;

        body = world.createBody(def);
        body.setFixedRotation(true);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Vector2[] points = {
                new Vector2(0,0),
                new Vector2(8,0),
                new Vector2(8,18),
                new Vector2(0,18)};
        shape.set(points);
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;

        body.createFixture(fixtureDef);
        body.setUserData("block");

        animation = new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("gate/gate",11).content),0,0, 0);
        for (int i = 0; i < animation.animation.getKeyFrames().length; i++) {
            animation.animation.getKeyFrames()[i].setSize(14,20);
            animation.animation.getKeyFrames()[i].setPosition(position.x,position.y-2);
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33OLD.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        font.getData().setScale(0.11f);
        generator.dispose();

        skull = new Sprite(new Texture(Gdx.files.internal("skull.png")));
        skull.setPosition(position.x - 9, position.y + 10);
        skull.setSize(4,4);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Sprite currentFrame = animation.animation.getKeyFrame(stateTime,false);

        if(canOpen() || stateTime > 0) {
            stateTime+=Gdx.graphics.getDeltaTime();
            if(stateTime >= 1.5f) {  // Frame duration * frames count - 1
                body.setActive(false);
                opened = true;
            }
        }
        else {
            skull.draw(batch);
            font.draw(batch, KillsCounter.getKillsInCurrentGame()+"/"+needToKill,currentFrame.getX() - 13, currentFrame.getY() + 9);
        }


        currentFrame.draw(batch);
    }

    private boolean canOpen() {
        return KillsCounter.getKillsInCurrentGame() >= needToKill && Math.hypot(Hero.getPosition().x - position.x, Hero.getPosition().y - position.y) <= 80 && Math.abs(Hero.getPosition().y - position.y) < 13 && !opened;
    }
}
