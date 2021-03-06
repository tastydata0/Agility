package com.agility.game;

import com.agility.game.UI.ItemInfo;
import com.agility.game.UI.OnHitDamageView;
import com.agility.game.Utils.AnimationWithOffset;
import com.agility.game.Utils.GameBalanceConstants;
import com.agility.game.Utils.SimpleDirectionGestureDetector;
import com.agility.game.Utils.SoundPlayer;
import com.agility.game.Utils.SpritePack;
import com.agility.game.WorldObjects.Bullet;
import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Hero extends Actor {

    public static Sprite blood;
    private Sprite currentFrame;
    private static Sprite manaBar, manaBarBackground;

    private Body body;
    public Body swordSwipe;
    private final World world;
    private static double mana = 0;
    private static Vector2 position,velocity;
    private int direction = 1, wallTouchDirection;

    public int damaged;
    private String currentAnimation = "idle";
    private float stateTime = 0, maxHealth = GameBalanceConstants.DEFAULT_HERO_MAX_HEALTH,health = maxHealth, afterRollingSlowlinessTimer;
    private int touchings = 0, avaliableJumps = 2, attackOrder, rollingTimer, rollDirection, jumpBlock;
    private boolean onGround = true, isAttacking, hasWeapon, isSwiped, isDied, isRolling;
    private Game game;
    private transient ItemInfo itemInfoInEdge;
    private Inventory inventory;
    private final static Color colorDamage = new Color(1,0.5f,0.5f,1);
    private FixtureDef fixtureDef;
    private PolygonShape shape;
    private float timeAfterDying;


    // Exp bar

    private static BitmapFont manaText;
    private static float manaBarOpacity;
    private static boolean drawExpBar;


    private static final HashMap<String,AnimationWithOffset> animations = new HashMap<String, AnimationWithOffset>();
    private boolean anotherOneAttack;
    private boolean stopped, casting;
    private Item weapon, armor;
    private Sound[] swipes = new Sound[6];
    private Sound[] jumps = new Sound[2];
    private Sound onGroundStep,rollSound;
    ArrayList<Enemy> enemies;
    private static final Random random = new Random();
    private static int manaDisplay,manaTarget;
    private int clearDamage, clearCriticalStrike;
    private Spell spell;
    private boolean grab, grabAbility;
    private boolean blow;

    //private Music runSound;

    public Hero(Vector2 position, World world, final Game game) {
        this.position = position;
        this.world = world;
        this.game = game;
        isDied = false;
        blood = new Sprite(new Texture(Gdx.files.internal("blood.png")));
        blood.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        swipes[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe0.ogg"));
        swipes[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe1.ogg"));
        swipes[2] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe2.ogg"));
        swipes[3] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe0.ogg"));
        swipes[4] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe1.ogg"));
        swipes[5] = Gdx.audio.newSound(Gdx.files.internal("sounds/swipe2.ogg"));

        jumps[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/jump0.ogg"));
        jumps[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/jump1.ogg"));

        onGroundStep = Gdx.audio.newSound(Gdx.files.internal("sounds/onGround0.ogg"));

        rollSound = Gdx.audio.newSound(Gdx.files.internal("sounds/roll0.ogg"));

        //runSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/walk.ogg"));

        init("animations");
        init("default equipment");
        init("mana bar");
        inventory = new Inventory();



        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

            @Override
            public void onUp() {
                if(canCast()) {
                    cast();
                }

            }

            @Override
            public void onRight() {
                if(!isDied && !isCasting()) {
                    direction = 1;
                    stopped = false;
                }
            }

            @Override
            public void onLeft() {
                if(!isDied && !isCasting()) {
                    direction = -1;
                    stopped = false;
                }
            }

            @Override
            public void onDown() {
                if(canRoll())
                    roll(direction);
            }

            @Override
            public void onTouch() {
                if(!isFacingToEnemy()) {
                    jump();
                }
                else {
                    if (!isDied) {
                        if (!isAttacking) {
                            body.setLinearVelocity(0, body.getLinearVelocity().y);
                            if (hasWeapon) {

                                SoundPlayer.play(swipes[random.nextInt(3)]);
                                setAnimation("attack" + ((++attackOrder % 3) + 1));
                            } else {
                                setAnimation("beat");
                            }
                            //direction = 0;
                            isAttacking = true;
                        } else {
                            anotherOneAttack = true;
                        }
                    }
                }
            }

            @Override
            public void onRightDown() {

            }

            @Override
            public void onLeftDown() {

            }
        },game));
    }

    private boolean canCast() {
        return mana >= 1 && !isCasting() && onGround && !isDied;
    }

    private boolean canRoll() {
        return !isDied && !isCasting();
    }

    private void cast() {
        mana = 0;
        Vector2 pos = position.cpy();
        pos.x += 3;
        if(direction == -1) {
            pos.x -= 100;
        }
        spell = new Spell(true, pos,direction,game);
        Game.getStage().addActor(spell);
        stop();
        setAnimation("cast");
    }

    private void jump() {
        if(!isDied && jumpBlock == 0 && !blow) {
            if (wallTouchDirection == 0 || touchings == 1) {
                if (avaliableJumps > 0) {
                    avaliableJumps -= 1;
                    body.setLinearVelocity(body.getLinearVelocity().x, 0);
                    body.applyLinearImpulse(new Vector2(0, 15000), new Vector2(0, 0), true);
                    SoundPlayer.play(jumps[avaliableJumps]);
                    isAttacking = false;
                    stabilizeSpeed();
                }

                stopped = false;
            } else if (wallTouchDirection == -1) {
                if (!onGround && avaliableJumps != 2) {
                    body.setLinearVelocity(0, 0);
                    SoundPlayer.play(jumps[1]);
                    body.applyLinearImpulse(new Vector2(-999999999, 999999999), new Vector2(0, 0), true);
                    wallTouchDirection = 0;
                }

                direction = 1;
                stopped = false;
            }
            else {
                if (wallTouchDirection == 1 && !onGround && avaliableJumps != 2) {
                    body.setLinearVelocity(0, 0);
                    SoundPlayer.play(jumps[1]);
                    body.applyLinearImpulse(new Vector2(999999999, 999999999), new Vector2(0, 0), true);
                    wallTouchDirection = 0;
                }

                direction = -1;
                stopped = false;
            }
        }

    }

    private boolean isFacingToEnemy() {
        for (Enemy e : game.getEnemies()) {
            if(!e.isDied() && Math.hypot(e.getBody().getPosition().x -
                    position.x, e.getBody().getPosition().y - position.y) <= 30 && wallTouchDirection == 0 && touchings > 0) {
                return true;
            }
            else if(!e.isDied() && Math.hypot(e.getBody().getPosition().x -
                    position.x, e.getBody().getPosition().y - position.y) <= 15 && wallTouchDirection == 0) {
                return true;
            }
        }
        for(Bullet e:Game.getBullets()) {
            Game.log(e.getBody().getPosition()+"  "+position);
            if(Math.hypot(e.getBody().getPosition().x -
                    position.x, e.getBody().getPosition().y - position.y) <= 24 && wallTouchDirection == 0 &&
                    Math.abs(e.getBody().getPosition().y - position.y) < 40) {
                return true;
            }
        }


        return false;
    }

    public void frag() {
        if(!isCasting()) {
            int addMana = 0;
            do {
                addMana = (int) ((0.11 - (random.nextDouble() / (100))) * 1000);
            } while (addMana <= 30);
            mana += addMana / 1000f;
            if (mana > 1) {
                mana = 1;
            }
            drawExpBar(addMana);
        }
    }

    public void stop(boolean setIdle) {
        body.setLinearVelocity(0,0);
        if(setIdle) {
            if (hasWeapon) {
                setAnimation("idle-sword");
            } else {
                setAnimation("idle");
            }
        }
        stopped = true;
    }

    public void stop() {
        stop(true);
    }

    private void endAttack() {
        isSwiped = false;
        isAttacking = false;
        if (hasWeapon) {
            setAnimation("idle-sword");
        } else {
            setAnimation("idle");
        }

    }

    public void equip(Item item) {
        equip(item, false);
    }

    public void equip(Item item, boolean silent) {
        if(item.getType() == ItemInfo.TYPE_WEAPON) {

            weapon = item;
            hasWeapon = true;
            if(Game.getUi() != null && !silent) {
                Game.log("Equipped: " + weapon.getName() + ", " + weapon.getParameter1() + " damage");
            }
        }
    }

    public void roll(int rollDirection) {
        setAnimation("roll");
        isAttacking = false;
        anotherOneAttack = false;
        PolygonShape ps = (PolygonShape)(body.getFixtureList().get(0).getShape());
        ps.setAsBox(4,0.01f);
        enemies = new ArrayList<Enemy>();
        for (int i = 0; i < game.getStage().getActors().size; i++) {
            Actor a = game.getStage().getActors().get(i);
            if(a.getName() != null && a.getName().equals("enemy")) {
                enemies.add((Enemy)game.getStage().getActors().get(i));
            }
        }
        for (Enemy e:enemies) {
            e.getBody().setActive(false);
        }
        SoundPlayer.play(rollSound);
        isRolling = true;
        stopped = false;
        rollingTimer = 24;
        this.rollDirection = rollDirection;
    }

    private void reduceJumpBlock(){
        if(jumpBlock>0) jumpBlock--;
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(touchings == 1 && wallTouchDirection != 0 && !onGround && !grab && grabAbility && body.getLinearVelocity().y >= -40) {
            grab();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.T)) {
            maxHealth++;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Y)) {
            health++;
        }
        if (swordSwipe != null) {
            world.destroyBody(swordSwipe);
            swordSwipe = null;
        }
        checkForAnotherOneAttack();
        checkForDeath();
        checkForRoll();
        reduceJumpBlock();
        if(isDied) {
            timeAfterDying += Gdx.graphics.getDeltaTime();
            if(timeAfterDying > 6) {
                timeAfterDying = 0;
                game.lose();
            }
        }
        if((currentAnimation.equals("attack1") || currentAnimation.equals("attack2") || currentAnimation.equals("attack3")) && touchings == 0) {
            setAnimation("jump");
        }
        if(wallTouchDirection != 0 && touchings == 0) {
            wallTouchDirection = 0;
        }
        //Game.log("Roll = "+isRolling +"  SlTimer = "+afterRollingSlowlinessTimer + "  IsAttacking = "+isAttacking);
        //printState();

        position = body.getPosition();
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = animations.get(currentAnimation).animation.getKeyFrame(stateTime, !isAttacking && !isDied);
        if(isAttacking && stateTime >= 0.1f && !isSwiped) {
            swipe();
        }
        if(isAttacking && animations.get(currentAnimation).animation.isAnimationFinished(stateTime)){
            endAttack();
        }
        currentFrame.setPosition(body.getPosition().x-9-direction*animations.get(currentAnimation).xOffset,body.getPosition().y-0.5f+animations.get(currentAnimation).yOffset);
        currentFrame.setSize(50f/2f,37f/2f);
        currentFrame.setFlip(direction == -1,false);
        currentFrame.draw(batch,parentAlpha);
        if(damaged > 0) {
            damaged--;
        }

        if(currentAnimation.equals("hurt") && stateTime >= 0.3f) {
            setAnimation("run");
        }

        if(!stopped && !isRolling && !isDied && !isCasting() && !blow) {
            stabilizeSpeed();
        }
        else if(isDied) {
            body.setLinearVelocity(0,body.getLinearVelocity().y);
        }
        checkForGrab();
        if(drawExpBar) {

            manaBar.setPosition(currentFrame.getX()+6.5f + 1.5f*direction ,currentFrame.getY() + 18);
            manaBarBackground.setPosition(manaBar.getX(),manaBar.getY());
            manaBarBackground.setSize(manaBarBackground.getWidth(),0.5f);

            manaBarBackground.draw(batch,manaBarOpacity);
            manaBar.setSize(manaBarBackground.getWidth() * (float)(mana),manaBarBackground.getHeight());
            manaBar.draw(batch,manaBarOpacity);
            manaBarOpacity-=(1-manaBarOpacity)/25;
            if(manaBarOpacity <= 0.01f) {
                manaBarOpacity = 0.99f;
                drawExpBar = false;
            }
            if(manaTarget > manaDisplay) {
                manaDisplay += (manaTarget-manaDisplay)/10;
            }

            float cx = game.getStage().getCamera().position.x - game.getStage().getCamera().viewportWidth/2;
            float cy = game.getStage().getCamera().position.y - game.getStage().getCamera().viewportHeight/2;
            float manaDrawTextX = (manaBar.getX() - cx - 2f) * 7.5f;
            float manaDrawTextY = (manaBar.getY() + 4 - cy) * 7.5f;
            if(!Game.getUi().drawText) {
                if(isManaFull()) {
                    Game.getUi().drawText("Ultimate ready", manaDrawTextX, manaDrawTextY);
                }
                //Game.getUi().drawText("Mana " + (1f/manaDisplay)*100+"%", manaDrawTextX, manaDrawTextY);

            }
            else {
                Game.getUi().drawTextX = manaDrawTextX;
                Game.getUi().drawTextY = manaDrawTextY;
            }
        }
    }

    private void checkForGrab() {
        //Game.log("WTD = "+wallTouchDirection+"  Touchings = "+touchings +"  Grab = "+grab +"  On ground = "+onGround +"  Grab ability = "+grabAbility+"  State time = "+stateTime);
        if(touchings != 1 || !grabAbility) {
            grab = false;
        }
        if(grab) {
            if(stateTime < 1 || true) {
                if(!currentAnimation.equals("grab")) {
                    setAnimation("grab", false);
                }
                if(wallTouchDirection == -1) {
                    body.setLinearVelocity(-200, 40);
                }
                else if(wallTouchDirection == 1) {
                    body.setLinearVelocity(200, 40);
                }
                else {
                    grab = false;
                }
            }
            else {
                grab = false;
            }
        }
    }

    private void grab() {
        grab = true;
        setAnimation("grab");
    }
    public boolean isCasting() {
        return spell != null;
    }

    private void restoreBody() {
        PolygonShape ps = (PolygonShape)(body.getFixtureList().get(0).getShape());
        Vector2[] points = {
                new Vector2(0,(float)Math.sqrt(12)*4),
                new Vector2(0,0.2f),
                new Vector2(0.2f,0),
                new Vector2((float)Math.sqrt(3)*4-0.2f,0),
                new Vector2((float)Math.sqrt(3)*4,0.2f),
                new Vector2((float)Math.sqrt(3)*4,(float)Math.sqrt(12)*4)};
        ps.set(points);
    }

    private boolean isManaFull() {
        return mana >= 1;
    }

    private void checkForRoll() {
        if(rollingTimer > 0 && onGround) {
            rollingTimer--;
            body.setLinearVelocity(120*rollDirection,0);
        }
        else if(isRolling){
            afterRollingSlowlinessTimer = 10;
            stabilizeSpeed();
            isRolling = false;
            for (Enemy e:enemies) {
                e.getBody().setActive(true);
            }
            rollDirection = 0;
            restoreBody();
        }

        if(!isRolling && afterRollingSlowlinessTimer > 0) {
            afterRollingSlowlinessTimer--;
            if(afterRollingSlowlinessTimer <= 0) {
                stabilizeSpeed();
            }
        }

        if(isRolling && wallTouchDirection != 0) {
            afterRollingSlowlinessTimer = 10;
            stabilizeSpeed();
            isRolling = false;
            for (Enemy e:enemies) {
                e.getBody().setActive(true);
            }
            rollDirection = 0;
            setAnimation("idle");
            restoreBody();
        }
    }

    private void checkForDeath() {
        if(health <= 0) {
            setAnimation("die");
            isDied = true;
            if(timeAfterDying == 0) {
                timeAfterDying = 0.1f;
            }
            health = 0;
        }
    }

    public boolean isDied() {
        return isDied;
    }

    private void swipe() {
        isSwiped = true;
        BodyDef def = new BodyDef();
        def.gravityScale = 0;
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.x = position.x+3+7*direction;
        def.position.y = position.y+7;

        swordSwipe = world.createBody(def);
        swordSwipe.setFixedRotation(true);
        fixtureDef = new FixtureDef();
        shape = new PolygonShape();
        shape.setAsBox(6,1);
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 0f;

        swordSwipe.createFixture(fixtureDef);
        swordSwipe.setUserData("weaponSwipe");

        Game.getCamera().shake(direction);
    }

    private void printState() {
        System.out.println("X speed: "+body.getLinearVelocity().x+" | Y:"+body.getLinearVelocity().y+" Hero: On Ground: "+onGround+" | Wall touch: "+wallTouchDirection+" | Jumps: "+avaliableJumps);
    }

    private void stabilizeSpeed() {
        if(isAttacking || afterRollingSlowlinessTimer > 0) {
            afterRollingSlowlinessTimer--;
            switch (direction) {
                case(-1):
                    body.setLinearVelocity(-20,body.getLinearVelocity().y);
                    break;
                case (1):
                    body.setLinearVelocity(20,body.getLinearVelocity().y);
                    break;

            }
        }
        else {
            switch (direction) {
                case(-1):
                    body.setLinearVelocity(-95,body.getLinearVelocity().y);
                    break;
                case (1):
                    body.setLinearVelocity(95,body.getLinearVelocity().y);
                    break;

            }
        }

    }

    private void checkForAnotherOneAttack() {
        if(!isAttacking && anotherOneAttack) {
            anotherOneAttack = false;
            isAttacking = true;
            //  direction = 0;
            if(hasWeapon) {
                SoundPlayer.play(swipes[random.nextInt(3)]);
                setAnimation("attack" + ((++attackOrder % 3) + 1));
            }
            else {
                setAnimation("beat");
            }
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
    }




    public static Vector2 getPosition() {
        return position;
    }

    public void init(String request) {
        if(request.equalsIgnoreCase("body")) {
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
                    /* OLD (Need for presentation)
                    new Vector2(0,(float)Math.sqrt(12)*4),
                    new Vector2(0,1),
                    new Vector2(1,0),
                    new Vector2((float)Math.sqrt(3)*4-1,0),
                    new Vector2((float)Math.sqrt(3)*4,1),
                    new Vector2((float)Math.sqrt(3)*4,(float)Math.sqrt(12)*4)};
                     */
                    new Vector2(0,(float)Math.sqrt(12)*4),
                    new Vector2(0,0.2f),
                    new Vector2(0.2f,0),
                    new Vector2((float)Math.sqrt(3)*4-0.2f,0),
                    new Vector2((float)Math.sqrt(3)*4,0.2f),
                    new Vector2((float)Math.sqrt(3)*4,(float)Math.sqrt(12)*4)};
            shape.set(points);
            fixtureDef.shape = shape;
            fixtureDef.density = 1;
            fixtureDef.friction = 0f;

            body.createFixture(fixtureDef);
            body.setUserData("player");
            stop();
        }
        else if(request.equalsIgnoreCase("animations")) {
            animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("hero/run",6).content),2,0, -8));

            animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.4f,new SpritePack("hero/idle",4).content),2,0, -8));

            animations.put("jump",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/crnr-jmp",2).content),2,0, -8));

            animations.put("roll",new AnimationWithOffset(new Animation<Sprite>(0.09f,new SpritePack("hero/smrslt",4).content),0,-3, -8));

            animations.put("back-roll",new AnimationWithOffset(new Animation<Sprite>(0.09f,new SpritePack("hero/back-smrslt",4).content),0,-3, -8));

            animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/die",7).content),2,0, -8));

            animations.put("attack1",new AnimationWithOffset(new Animation<Sprite>(0.05f,new SpritePack("hero/attack1",5).content),2,0, -8));

            animations.put("attack2",new AnimationWithOffset(new Animation<Sprite>(0.05f,new SpritePack("hero/attack2",6).content),2,0, -8));

            animations.put("attack3",new AnimationWithOffset(new Animation<Sprite>(0.05f,new SpritePack("hero/attack3",6).content),2,0, -8));

            animations.put("getsword",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/swrd-drw",4).content),2,0, -8));

            animations.put("removesword",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/swrd-shte",4).content),2,0, -8));

            animations.put("idle-sword",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/idle-2",4).content),2,0, -8));

            animations.put("cast",new AnimationWithOffset(new Animation<Sprite>(0.5f,new SpritePack("hero/cast",4).content),2,0, -8));

            animations.put("beat",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("hero/cast",4).content),2,0, -8));

            animations.put("fall",new AnimationWithOffset(new Animation<Sprite>(0.1f,new SpritePack("hero/fall",2).content),2,0, -8));

            animations.put("wall-slide",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/wall-slide",2).content),0,0, -8));

            animations.put("hurt",new AnimationWithOffset(new Animation<Sprite>(0.1f,new SpritePack("hero/hurt",3).content),0,0, -8));

            animations.put("grab",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("hero/crnr-clmb",5).content),0,-4, -8));




        }

        else if(request.equals("default equipment")) {
            if(weapon == null) {
                ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON, "Fists", 60, 2, 1);
                weapon = new Item(game, null, info);
                equip(weapon);
                info.setItem(weapon);
                hasWeapon = false;
            }
            else {
                hasWeapon = true;
            }


        }

        else if(request.equals("mana bar")) {
            Pixmap manaPixmap = new Pixmap(12,1,Pixmap.Format.RGBA8888);
            manaPixmap.setColor(0.27f, 0.68f, 1,1);
            manaPixmap.fill();
            manaBar = new Sprite(new Texture(manaPixmap));

            Pixmap manaPixmapBackground = new Pixmap(12,1,Pixmap.Format.RGBA8888);
            manaPixmapBackground.setColor(0,0,0,1);
            manaPixmapBackground.fill();
            manaBarBackground = new Sprite(new Texture(manaPixmapBackground));

            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 4;
            parameter.color = Color.WHITE;
            manaText = generator.generateFont(parameter);
        }

    }

    public void heal(int amount) {
        health+=amount;
        if(health > maxHealth) {
            health = maxHealth;
        }
    }

    private static void drawExpBar(int addedMana) {
        drawExpBar = true;
        manaBarOpacity = 0.99f;
        Game.getUi().drawText = false;
        manaTarget = addedMana;
    }

    public void damage(float deal) {
        this.health -= deal;
        damaged = 20;
        body.applyLinearImpulse(new Vector2(direction*-2999, 0), new Vector2(0, 0), true);
        setAnimation("hurt");
    }

    public void addItem(Item item) {
        if(inventory.add(item)) {
            if(itemInfoInEdge != null) {
                Game.getUi().getActors().removeValue(itemInfoInEdge,false);
            }
            itemInfoInEdge = item.getInfo();
            Game.getUi().addActor(itemInfoInEdge);
        }
        else {
            Game.log("Not enough free space in inventory...");
        }
    }

    public void touchBlock(Contact contact) {
        touchings++;
        grabAbility = false;
        blow = false;

        Body block = null;
        if(contact.getFixtureA().getBody().getUserData().equals("player") && contact.getFixtureB().getBody().getUserData().equals("block")) {
            block = contact.getFixtureB().getBody();
        }
        else if(contact.getFixtureB().getBody().getUserData().equals("player") && contact.getFixtureA().getBody().getUserData().equals("block")){
            block = contact.getFixtureA().getBody();
        }
        if(block != null) {

            if(block.getPosition().y <= body.getPosition().y+10) {
                grabAbility = true;
            }
            //Game.log("Block y ="+block.getPosition().y+"  Hero y ="+body.getPosition().y);
        }
        if(block != null && !isAttacking) {
            if (block.getPosition().y + 4 <= position.y && block.getPosition().x - position.x < 3 * 16) {
                onGround = true;
                wallTouchDirection = 0;
                avaliableJumps = 2;
                if (!currentAnimation.equals("run") && !isRolling && !stopped) {
                    setAnimation("run");
                    SoundPlayer.play(onGroundStep);
                }
            } else if (block.getPosition().y + 4 >= position.y - 25) {
                if (block.getPosition().x > position.x) {
                    if (body.getLinearVelocity().y < -100) {
                        if (!currentAnimation.equals("wall-slide")) {
                            setAnimation("wall-slide");
                        }
                    }
                    wallTouchDirection = 1;
                } else if (block.getPosition().x < position.x) {
                    if (body.getLinearVelocity().y < -100) {
                        if (!currentAnimation.equals("wall-slide")) {
                            setAnimation("wall-slide");
                        }
                    }
                    wallTouchDirection = -1;
                }
            } else {
                wallTouchDirection = 0;
            }
        }
    }

    public void releaseTouch(Contact contact) {
        touchings--;
        if(touchings == 0) {
            setAnimation("jump");
            onGround = false;
        }
    }

    private void setAnimation(String name, boolean refreshStateTime) {
        if(!isDied) {
            currentAnimation = name;
            if(refreshStateTime) {
                stateTime = 0;
            }
        }
    }

    private void setAnimation(String name) {
        setAnimation(name,true);
    }

    public Body getBody() {
        return body;
    }

    public static void setPosition(Vector2 position) {
        Hero.position = position;
    }

    public void grabSword(Item item) {
        ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON,"Start sword",70,0.04f,1);
        weapon = new Item(game,null,info);
        equip(weapon);
        info.setItem(weapon);
        setAnimation("getsword");
        hasWeapon = true;
    }

    public int getMaxHealth() {

        return (int)maxHealth;
    }

    public Item getWeapon() {
        return weapon;
    }

    public Item getArmor() {
        return armor;
    }

    public float getHealth() {
        return health;
    }

    public static double getMana() {
        return mana;
    }

    public void hitEnemy(Enemy enemy) {
        jumpBlock = 15;

        float damage = weapon.getParameter1() + clearDamage;
        damage += damage * ((new Random().nextInt(11)-5)/100f);
        boolean critical = Math.random()<((weapon.getParameter2()+clearCriticalStrike)/100f);
        if(critical) {
            damage *= 1.5f;  // Critical strike
        }
        enemy.damage((int)damage);
        Gdx.input.vibrate(20);
        float cx = game.getStage().getCamera().position.x - game.getStage().getCamera().viewportWidth/2;
        float cy = game.getStage().getCamera().position.y - game.getStage().getCamera().viewportHeight/2;
        Game.getUi().addActor(new OnHitDamageView((int)damage,new Vector2((enemy.getBody().getPosition().x+3 - cx) * 7.5f,(enemy.getBody().getPosition().y+6 - cy) * 7.5f),critical));
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void equipLastItem() {
        equip(inventory.get().get(inventory.get().size()-1));
    }

    public void increaseMaxHealth(int value) {
        int heal = (int)(200 * (maxHealth/health));
        System.out.println("NOT Healing: "+heal+" Max health = "+maxHealth +"  Value = "+value);
        maxHealth+=value;

        heal(heal);
    }

    public void increaseDamage(int value) {
        clearDamage+=value;
    }

    public void increaseCriticalStrike(int value) {
        clearCriticalStrike+=value;
    }

    public void endSpell() {
        spell = null;
        stopped = false;
        setAnimation("run");
        stabilizeSpeed();
    }

    public boolean isRolling() {
        return isRolling;
    }

    public void blow() {
        for (int i = 0; i < 10; i++) {
            body.applyLinearImpulse(new Vector2(-999999999*direction,999999999), new Vector2(0,0), true);
        }
        body.setLinearVelocity(-200*direction,500);
        blow = true;

    }

    public void setMana(float i) {
        mana = i;
        drawExpBar(1);
    }

    public boolean hasWeapon() {
        return hasWeapon;
    }

    public float getDirection() {
        return direction;
    }
}
