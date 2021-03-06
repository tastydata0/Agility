package com.agility.game;

import com.agility.game.UI.BoosterChoice;
import com.agility.game.UI.DamageBoosterChoice;
import com.agility.game.UI.FlingPiece;
import com.agility.game.UI.HealthBoosterChoice;
import com.agility.game.UI.ItemInfo;
import com.agility.game.UI.LevelSelection.Level;
import com.agility.game.UI.LevelSelection.LevelSelectionMenu;
import com.agility.game.UI.LoadingScreen;
import com.agility.game.UI.MoneyBoosterChoice;
import com.agility.game.UI.MoneyMonitor;
import com.agility.game.UI.OnHitDamageView;
import com.agility.game.UI.UI;
import com.agility.game.WorldObjects.AnimatedDecoration;
import com.agility.game.WorldObjects.Block;
import com.agility.game.Utils.*;
import com.agility.game.WorldObjects.Booster;
import com.agility.game.WorldObjects.Bullet;
import com.agility.game.WorldObjects.Coin;
import com.agility.game.WorldObjects.Decoration;
import com.agility.game.WorldObjects.ExitPortal;
import com.agility.game.WorldObjects.Gate;
import com.agility.game.WorldObjects.Item;
import com.agility.game.WorldObjects.StartWeapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Game extends com.badlogic.gdx.Game {



    // Enemy patterns
    EnemyDef[] patterns = new EnemyDef[8];
    public static EnemyDef ENEMY_BANDIT;
    public static EnemyDef ENEMY_SKELETON;
    public static EnemyDef ENEMY_UNDEAD;
    public static EnemyDef ENEMY_WIZARD;
    public static EnemyDef ENEMY_SORCERER;
    public static EnemyDef ENEMY_NINJA;
    public static EnemyDef ENEMY_VIKING;
    public static EnemyDef ENEMY_SLIME;
    public static EnemyDef ENEMY_BOSS;

    // Application states
    public static final int STATE_IN_MAIN_MENU = 0;
    public static final int STATE_IN_LEVEL_SELECTION = 1;
    public static final int STATE_IN_GAME = 2;


    private World background;
    private static World middle;
    private World foreground;
    private Box2DDebugRenderer debugRenderer;
    private static Stage stage;
    private static UI ui;
    private static MainMenu mainMenu;
    private static LevelSelectionMenu levelSelectionMenu;
    public static LockedCamera camera;
    private SpriteBatch batch;
    public static ArrayList<Item> onGroundItems = new ArrayList<Item>();
    public static ArrayList<Booster> boosters = new ArrayList<Booster>();
    private Map map;
    private BoosterChoice currentChoice;
    public static float zoom = 7.5f, timeSinceLevelSelectionMenuOpen;
    private static Hero hero;
    private int currentState;
    public static int lastPassedLevel;
    private HintsHandler hintsHandler;
    private ArrayList<Hint> mightBeAddedHints;

    // Flash
    private float flashOpacity = 0;
    private boolean flash;
    private static Sprite flashScreen;


    Block[][] block = new Block[128][72];
    public static StartWeapon startWeapon;
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private boolean choose;
    private boolean freeze;
    private static boolean paused;
    private static int currentLevelNumber;

    public static int drawableItemGrabRequests;
    private MusicHandler musicHandler;
    public ExitPortal exitPortal;


    public Game() {

    }

    @Override
    public void create() {
        Gdx.gl.glClearColor(10.2f/100f,11.8f/100f,19.2f/100f,1);
//        File assetsDir = new File("E:\\Android\\Agility\\android\\assets\\convertName");
//        File[] assets = assetsDir.listFiles();
//        for (int i = 0; i < assets.length; i++) {
//            assets[i].renameTo(new File("E:\\Android\\Agility\\android\\assets\\convertName\\warrior-0"+assets[i].getName().replaceAll(".gif","")+".png"));
//        }

        System.out.print("Init main menu........");
        init("level selection menu");
        init("main menu");
        init("passed levels");
        Gdx.graphics.setVSync(true);
    }

    public void start(Level level) {
        System.out.println("Starting level: "+level.getName());

        mainMenu.music.stop();
        mainMenu.music.dispose();




        if(currentState == STATE_IN_LEVEL_SELECTION) {

            System.out.print("Init map..............");
            initLevel(level);
            System.out.print("Init enemies..........");
            init("enemies");
            level.init();
            System.out.print("Init stage elements...");
            init("stage elements");
            System.out.print("Init saves............");
            init("saves");
            System.out.println("------------------------------------");
            level.initForeground();
            currentState = STATE_IN_GAME;

        }
        if(level.getNumber() == 1){
            hintsHandler = new HintsHandler(mightBeAddedHints);
        }
        else {
            hintsHandler = null;
        }
        unfreeze();
        hero.heal(hero.getMaxHealth());
        musicHandler.begin(level.getNumber());
        KillsCounter.refreshGameKills();
        ui.start();
    }

    public static void log(String message) {
        ui.log(message);
    }

    @Override
    public void render() {
        if(currentState == STATE_IN_GAME) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            Gdx.gl.glClearColor(10.2f/100f,11.8f/100f,19.2f/100f,1);

            camera.update();
            stage.draw();
            stage.act(Gdx.graphics.getDeltaTime());
            ui.act();
            ui.draw();
            if(flash) {
                if(flashOpacity >= 0.95) {
                    finish();
                    flash = false;
                    flashOpacity = 0;
                }
                batch.begin();
                flashScreen.setPosition(camera.position.x-camera.viewportWidth/2,camera.position.y-camera.viewportHeight/2);
                flashScreen.draw(batch,flashOpacity);
                flashOpacity+=0.015f;
                musicHandler.setVolume(1-flashOpacity);
                batch.end();
            }

            if(!freeze) {
                middle.step((1 / 100f), 4, 4);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                debugRenderer.render(middle, camera.combined);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                debugRenderer.render(foreground, camera.combined);
                flash = true;
            }



        }
        else if(currentState == STATE_IN_MAIN_MENU){
            Gdx.gl.glClearColor(10f/100f,12f/100f,19.7f/100f,1);
            mainMenu.draw();
        }
        else if(currentState == STATE_IN_LEVEL_SELECTION){
            levelSelectionMenu.draw();
            timeSinceLevelSelectionMenuOpen+=Gdx.graphics.getDeltaTime();
        }

        if(freeze) {
            for(Enemy e:enemies) {
                e.setAnimation("idle");
            }
        }

        drawableItemGrabRequests = 0;
        if (hintsHandler != null) {
            hintsHandler.update();
        }
    }

    // Finish current level
    private void finish() {
        lastPassedLevel = Math.max(currentLevelNumber, lastPassedLevel);
        System.out.println("FIN "+currentLevelNumber);
        System.out.println("LPL " +lastPassedLevel);
        save();
        musicHandler.stop();
        currentState = STATE_IN_MAIN_MENU;
        enemies.clear();

        try{levelSelectionMenu.getHandler().getItems()[lastPassedLevel].unlock();}
        catch (Exception e){}
        BlockFactory.refreshVariables();
        onGroundItems.clear();
        Gdx.input.setInputProcessor(new MainMenuInputProcessor(mainMenu, levelSelectionMenu));
    }

    // Finish current level
    public void lose() {
        save();

        musicHandler.stop();
        currentState = STATE_IN_MAIN_MENU;
        enemies.clear();
        BlockFactory.refreshVariables();
        onGroundItems.clear();
        Gdx.input.setInputProcessor(new MainMenuInputProcessor(mainMenu, levelSelectionMenu));
    }

    @Override
    public void resize(int width, int height) {

    }

    public void pause (boolean pause) {
        paused = pause;
        if(pause) {
            freeze();
        }
        else {
            unfreeze();
        }
    }

    @Override
    public void dispose() {

    }

    public void save(){
        Save save = savedProgress();
        if(hero != null && hero.isDied()) {
            save.clear(lastPassedLevel);
            System.out.println("Prefs clear!");
        }
        else {
            save.save();
        }
    }

    private Save savedProgress() {
        Save save = new Save();
        save.coins = MoneyMonitor.getMoney();
        save.diamonds = MoneyMonitor.getDiamonds();
        if(hero != null) {
            save.heroMaxHealth = hero.getMaxHealth();
            save.passedLevels = 0;
            save.equippedWeapon = hero.getWeapon();
        }
        System.out.println("LPL "+lastPassedLevel);
        save.passedLevels = lastPassedLevel;
        return save;
    }


    public void addRandomItem(Vector2 position, boolean fromBoss) {
        Item item;
        if(fromBoss) {
            item = new ItemFactory(this).createSlasher();
        }
        else {
            item = new ItemFactory(this).createRandomWeapon();
        }

        item.addToWorld(stage, position);
    }

    public void addRandomItem(Vector2 position) {
        addRandomItem(position,false);
    }

    private void initLevel(Level level) {
        map = level.getMap();
        System.out.println("Done");
    }

    private void init(String request) {
        long startTime = System.currentTimeMillis();
        if     (request.equalsIgnoreCase("box2D")) {
            background = new World(new Vector2(0,0),true);
            middle = new World(new Vector2(0,-10),true);
            foreground = new World(new Vector2(0,0),true);
            debugRenderer = new Box2DDebugRenderer();
            middle.setContactListener(new ContactHandler(this));
        }
        else if(request.equalsIgnoreCase("hero")) {
            hero = new Hero(new Vector2(25,565),middle,this);
            hero.setZIndex(0);
        }
        else if(request.equalsIgnoreCase("camera")) {
            float abstractHeight = Gdx.graphics.getHeight()/zoom;
            double w_div_h = Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
            camera  = new LockedCamera(abstractHeight*(float)w_div_h,abstractHeight,hero);
        }
        else if(request.equalsIgnoreCase("stage")) {
            batch = new SpriteBatch();
            stage = new Stage(new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera),batch);
            stage.getBatch().setProjectionMatrix(camera.combined);
        }
        else if(request.equalsIgnoreCase("ui")) {
            ui = new UI(this);
            ui.addActor(MoneyMonitor.instance);
            Pixmap fl = new Pixmap(1,1,Pixmap.Format.RGB888);
            fl.setColor(1,1,1,1);
            fl.fill();
            flashScreen = new Sprite(new Texture(fl));

            flashScreen.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
            OnHitDamageView.init();
            FlingPiece.init();

            musicHandler = new MusicHandler();
        }

        else if(request.equalsIgnoreCase("stage elements")) {
            Hero.setPosition(BlockFactory.heroStartPos);


            createStartSword();
            Coin.loadAtlases();
            exitPortal = new ExitPortal(this,BlockFactory.exitPos);
            exitPortal.addToWorld(stage);

            for (int i = 0; i < BlockFactory.boostsPos.size(); i++) {
                stage.addActor(new Booster(this, BlockFactory.boostsPos.get(i)));
            }

            enemies.clear();
            for (int i = 0; i < BlockFactory.enemiesPos.size(); i++) {
                EnemyDef pattern = patterns[new Random().nextInt(patterns.length)];
                enemies.add(new Enemy(pattern,middle,BlockFactory.enemiesPos.get(i),this));
                stage.addActor(enemies.get(i));
            }
            if(BlockFactory.bossPos != null) {
                Boss boss = new Boss(ENEMY_BOSS,middle,BlockFactory.bossPos,this);
                enemies.add(boss);
                stage.addActor(boss);
            }

            for (int i = 0; i < BlockFactory.gatesPos.size(); i++) {
                stage.addActor(new Gate(BlockFactory.gatesPos.get(i), getMainWorld(), GameBalanceConstants.REQUIRED_KILLS, enemies.size()));
            }

            mightBeAddedHints = new ArrayList<Hint>();
            // Decorations
            for (int i = 0; i < BlockFactory.anvilsPos.size(); i++) {
                stage.addActor(new Decoration("anvil", BlockFactory.anvilsPos.get(i)));
            }
            for (int i = 0; i < BlockFactory.barrelsPos.size(); i++) {
                stage.addActor(new Decoration("barrel", BlockFactory.barrelsPos.get(i)));
            }
            for (int i = 0; i < BlockFactory.chestsPos.size(); i++) {
                stage.addActor(new Decoration("chest", BlockFactory.chestsPos.get(i)));
            }
            for (int i = 0; i < BlockFactory.cobblestonesPos.size(); i++) {
                stage.addActor(new AnimatedDecoration("cobblestone", BlockFactory.cobblestonesPos.get(i), 0.2f, 6, -11.8f));
            }
            for (int i = 0; i < BlockFactory.firesPos.size(); i++) {
                stage.addActor(new AnimatedDecoration("fire", BlockFactory.firesPos.get(i),0.15f,6));
            }
            for (int i = 0; i < BlockFactory.fountainsPos.size(); i++) {
                stage.addActor(new AnimatedDecoration("fountain", BlockFactory.fountainsPos.get(i), 0.15f, 6));
            }
            for (int i = 0; i < BlockFactory.pristsPos.size(); i++) {
                stage.addActor(new Decoration("priest", BlockFactory.pristsPos.get(i)));
            }
            for (int i = 0; i < BlockFactory.signsPos.size(); i++) {
                Vector2 hPos = BlockFactory.signsPos.get(i);
                mightBeAddedHints.add(new Hint(hPos, i));
                stage.addActor(new Decoration("sign", hPos));
            }
            for (int i = 0; i < BlockFactory.vasesPos.size(); i++) {
                stage.addActor(new Decoration("vase", BlockFactory.vasesPos.get(i)));
            }

            // NPC
            /*for (int i = 0; i < BlockFactory.castersPos.size(); i++) {
                stage.addActor(new NPC(new AnimationWithOffset(new Animation<Sprite>(0.1f,new SpritePack("NPC/caster",20, 0.15f).content),-0,-4.4f, 0),NPC.bookSpellCast, BlockFactory.castersPos.get(i)));
            }
            for (int i = 0; i < BlockFactory.ninjasPos.size(); i++) {
                stage.addActor(new NPC(new AnimationWithOffset(new Animation<Sprite>(0.07f,new SpritePack("NPC/ninja",19,0.08f).content),-0,-1.1f, 0),NPC.weaponSell, BlockFactory.ninjasPos.get(i)));
            }
            for (int i = 0; i < BlockFactory.warriorsPos.size(); i++) {
                stage.addActor(new NPC(new AnimationWithOffset(new Animation<Sprite>(0.1f,new SpritePack("NPC/warrior",7,0.06f).content),-0,-2.8f, 0),NPC.weaponUpgrade, BlockFactory.warriorsPos.get(i)));
            }
            for (int i = 0; i < BlockFactory.witchesPos.size(); i++) {
                stage.addActor(new NPC(new AnimationWithOffset(new Animation<Sprite>(0.07f,new SpritePack("NPC/witch",26,0.075f).content),-0,-1, 0),NPC.manaFill, BlockFactory.witchesPos.get(i)));
            }*/

            hero.init("body");
            stage.addActor(hero);
        }

        else if(request.equalsIgnoreCase("enemies")) {

            ENEMY_BANDIT = new EnemyDef();
            ENEMY_BANDIT.cooldown = 60;
            ENEMY_BANDIT.cooldownInStart = 30;
            ENEMY_BANDIT.attackRange = 15;
            ENEMY_BANDIT.visibilityY = 5;
            ENEMY_BANDIT.visibilityX = 50;
            ENEMY_BANDIT.maxHealth = 300+Math.round(Math.random()-0.3)*100;
            ENEMY_BANDIT.runVelocity = 65;
            ENEMY_BANDIT.damageDealt = 75;
            ENEMY_BANDIT.stateTimeSlash = 0.4f;
            ENEMY_BANDIT.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_BANDIT.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/bandit/run",8).content),-0,-9, -20));
            ENEMY_BANDIT.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/bandit/idle",6).content),-0,-9, -20));
            ENEMY_BANDIT.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/bandit/die",6).content),-0,-9, -20));
            ENEMY_BANDIT.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/bandit/attack",7).content),-0,-9, -20));
            patterns[0] = ENEMY_BANDIT;


            ENEMY_SKELETON = new EnemyDef();
            ENEMY_SKELETON.cooldown = 60;
            ENEMY_SKELETON.cooldownInStart = 5;
            ENEMY_SKELETON.visibilityY = 5;
            ENEMY_SKELETON.visibilityX = 60;
            ENEMY_SKELETON.attackRange = 25;
            ENEMY_SKELETON.maxHealth = 400+Math.round(Math.random()-0.3)*100;
            ENEMY_SKELETON.runVelocity = 40;
            ENEMY_SKELETON.damageDealt = 125;
            ENEMY_SKELETON.stateTimeSlash = 1.075f;
            ENEMY_SKELETON.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_SKELETON.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/skeleton/run",13).content),0,-8, -8));
            ENEMY_SKELETON.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/skeleton/idle",11).content),0,-8, -8));
            ENEMY_SKELETON.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/skeleton/die",15).content),0,-8,-8));
            ENEMY_SKELETON.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/skeleton/attack",18).content),-4,-9, -18));
            patterns[1] = ENEMY_SKELETON;


            ENEMY_UNDEAD = new EnemyDef();
            ENEMY_UNDEAD.cooldown = 60;
            ENEMY_UNDEAD.cooldownInStart = 5;
            ENEMY_UNDEAD.attackRange = 20;
            ENEMY_UNDEAD.visibilityY = 5;
            ENEMY_UNDEAD.visibilityX = 60;
            ENEMY_UNDEAD.maxHealth = 400+Math.round(Math.random()-0.3)*100;
            ENEMY_UNDEAD.runVelocity = 40;
            ENEMY_UNDEAD.damageDealt = 125;
            ENEMY_UNDEAD.stateTimeSlash = 1.075f;
            ENEMY_UNDEAD.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_UNDEAD.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/undead/run",13).content),0,-16, -24));
            ENEMY_UNDEAD.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/undead/idle",11).content),2,-8, -21));
            ENEMY_UNDEAD.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/undead/die",13).content),1,-8,-30));
            ENEMY_UNDEAD.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/undead/attack",18).content),0,-16, -23));
            patterns[2] = ENEMY_UNDEAD;


            ENEMY_WIZARD = new EnemyDef();
            ENEMY_WIZARD.cooldown = 100;
            ENEMY_WIZARD.cooldownInStart = 5;
            ENEMY_WIZARD.attackRange = 60;
            ENEMY_WIZARD.visibilityY = 5;
            ENEMY_WIZARD.visibilityX = 80;
            ENEMY_WIZARD.maxHealth = 400+Math.round(Math.random()-0.3)*100;
            ENEMY_WIZARD.runVelocity = 40;
            ENEMY_WIZARD.damageDealt = 100;
            ENEMY_WIZARD.ranged = true;
            ENEMY_WIZARD.stateTimeSlash = 1.075f;
            ENEMY_WIZARD.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_WIZARD.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/wizard/run",2).content),1,-10, -17));
            ENEMY_WIZARD.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/wizard/idle",4).content),1,-10, -17));
            ENEMY_WIZARD.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.25f,new SpritePack("enemies/wizard/die",10).content),1,-10,-17));
            ENEMY_WIZARD.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/wizard/attack",10).content),1,-10, -17));
            patterns[3] = ENEMY_WIZARD;


            ENEMY_SORCERER = new EnemyDef();
            ENEMY_SORCERER.cooldown = 80;
            ENEMY_SORCERER.cooldownInStart = 5;
            ENEMY_SORCERER.attackRange = 50;
            ENEMY_SORCERER.visibilityY = 5;
            ENEMY_SORCERER.visibilityX = 80;
            ENEMY_SORCERER.maxHealth = 500+Math.round(Math.random()-0.3)*100;
            ENEMY_SORCERER.runVelocity = 0;
            ENEMY_SORCERER.damageDealt = 140;
            ENEMY_SORCERER.stateTimeSlash = 1.075f;
            ENEMY_SORCERER.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_SORCERER.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/sorcerer/run",1,1.2f).content),-1,-11, -24));
            ENEMY_SORCERER.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/sorcerer/idle",2,1.2f).content),-1,-11, -24));
            ENEMY_SORCERER.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/sorcerer/die",17,1.2f).content),-1,-11,-24));
            ENEMY_SORCERER.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.12f,new SpritePack("enemies/sorcerer/attack",10,1.2f).content),-1,-11, -24));
            patterns[4] = ENEMY_SORCERER;


            ENEMY_NINJA = new EnemyDef();
            ENEMY_NINJA.cooldown = 50;
            ENEMY_NINJA.cooldownInStart = 15;
            ENEMY_NINJA.attackRange = 22;
            ENEMY_NINJA.visibilityY = 5;
            ENEMY_NINJA.visibilityX = 50;
            ENEMY_NINJA.maxHealth = 300+Math.round(Math.random()-0.3)*100;
            ENEMY_NINJA.runVelocity = 60;
            ENEMY_NINJA.damageDealt = 95;
            ENEMY_NINJA.stateTimeSlash = 0.55f;
            ENEMY_NINJA.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_NINJA.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/ninja/run",8).content),-1,-23.5f, -28));
            ENEMY_NINJA.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/ninja/idle",4).content),-1,-23.5f, -28));
            ENEMY_NINJA.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/ninja/die",21).content),-1,-23.5f,-28));
            ENEMY_NINJA.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.12f,new SpritePack("enemies/ninja/attack",8).content),-1,-23.5f, -28));
            patterns[5] = ENEMY_NINJA;


            ENEMY_VIKING = new EnemyDef();
            ENEMY_VIKING.cooldown = 80;
            ENEMY_VIKING.cooldownInStart = 5;
            ENEMY_VIKING.attackRange = 20;
            ENEMY_VIKING.visibilityY = 5;
            ENEMY_VIKING.visibilityX = 60;
            ENEMY_VIKING.maxHealth = 550+Math.round(Math.random()-0.3)*100;
            ENEMY_VIKING.runVelocity = 30;
            ENEMY_VIKING.damageDealt = 130;
            ENEMY_VIKING.stateTimeSlash = 1f;
            ENEMY_VIKING.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_VIKING.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/viking/run",8).content),-1,-23.5f, -28));
            ENEMY_VIKING.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/viking/idle",4).content),-1,-23.5f, -28));
            ENEMY_VIKING.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/viking/die",13).content),-1,-23.5f,-28));
            ENEMY_VIKING.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/viking/attack",11).content),-1,-23.5f, -28));
            patterns[6] = ENEMY_VIKING;


            ENEMY_SLIME = new EnemyDef();
            ENEMY_SLIME.cooldown = 80;
            ENEMY_SLIME.cooldownInStart = 5;
            ENEMY_SLIME.attackRange = 20;
            ENEMY_SLIME.visibilityY = 5;
            ENEMY_SLIME.visibilityX = 60;
            ENEMY_SLIME.maxHealth = 350+Math.round(Math.random()-0.3)*100;
            ENEMY_SLIME.runVelocity = 40;
            ENEMY_SLIME.damageDealt = 90;
            ENEMY_SLIME.stateTimeSlash = 0.55f;
            ENEMY_SLIME.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_SLIME.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/slime/run",4).content),-1,-6.2f, -12));
            ENEMY_SLIME.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/slime/idle",4).content),-1,-6.2f, -12));
            ENEMY_SLIME.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/slime/die",4).content),-1,-6.2f,-12));
            ENEMY_SLIME.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.2f,new SpritePack("enemies/slime/attack",5).content),-1,-6.2f, -12));
            patterns[7] = ENEMY_SLIME;

            ENEMY_BOSS = new EnemyDef();
            ENEMY_BOSS.cooldown = 70;
            ENEMY_BOSS.cooldownInStart = 5;
            ENEMY_BOSS.attackRange = 25;
            ENEMY_BOSS.boss = true;
            ENEMY_BOSS.visibilityY = 20;
            ENEMY_BOSS.visibilityX = 1000;
            ENEMY_BOSS.maxHealth = 8000;
            ENEMY_BOSS.runVelocity = 40;
            ENEMY_BOSS.damageDealt = 100;
            ENEMY_BOSS.stateTimeSlash = 1f;
            ENEMY_BOSS.animations = new HashMap<String, AnimationWithOffset>();
            ENEMY_BOSS.animations.put("run",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/skeleton_boss/walk",13,new Vector2(60*0.7f,90*0.7f)).content),-60*0.7f*0.23f+6,-32, -29));
            ENEMY_BOSS.animations.put("idle",new AnimationWithOffset(new Animation<Sprite>(0.3f,new SpritePack("enemies/skeleton_boss/idle",11,new Vector2(60*0.7f,80*0.7f)).content),-60*0.7f*0.23f+6,-32, -30));
            ENEMY_BOSS.animations.put("die",new AnimationWithOffset(new Animation<Sprite>(0.15f,new SpritePack("enemies/skeleton/die",15).content),0,-8,-8));
            ENEMY_BOSS.animations.put("attack",new AnimationWithOffset(new Animation<Sprite>(0.14f,new SpritePack("enemies/skeleton_boss/attack",18,new Vector2(80.6f*0.91f,69.37f*0.91f)).content),-8,-64.5f, -88));
        }
        else if(request.equals("main menu")) {
            mainMenu = new MainMenu(this);
        }
        else if(request.equals("level selection menu")) {
            levelSelectionMenu = new LevelSelectionMenu(this);
        }
        else if(request.equals("passed levels")) {
            Save save = new Save();
            save.load();
            lastPassedLevel = save.passedLevels;
            for (int i = 0; i <= lastPassedLevel; i++) {
                System.out.println("UNCLOCK Level "+(i+1));
                try{
                levelSelectionMenu.getHandler().getItems()[i].unlock();}
                catch (Exception e){}
            }
        }
        else if(request.equals("saves")) {
            Save save = new Save();
            save.load();
            MoneyMonitor.setCoins(save.coins);
            MoneyMonitor.setDiamonds(save.diamonds);
            hero.increaseMaxHealth(save.heroMaxHealth-GameBalanceConstants.DEFAULT_HERO_MAX_HEALTH);
            if(save.equippedWeapon != null) {
                hero.equip(save.equippedWeapon, true);
            }
            else {
                //System.out.println("Weapon is NULL");
            }
        }
        System.out.println("Done (" + (System.currentTimeMillis() - startTime)/1000f + " s)");
    }

    private void createStartSword() {
        ItemInfo info = new ItemInfo(ItemInfo.TYPE_WEAPON,"Beginner's sword",70,3,1);
        if(BlockFactory.startWeaponPos != null) {
            startWeapon = new StartWeapon(BlockFactory.startWeaponPos, middle, this, info);
            info.setItem(startWeapon);
            stage.addActor(startWeapon);
        }
    }

    public static LockedCamera getCamera() {
        return camera;
    }

    public static Hero getHero() {
        return hero;
    }

    public StartWeapon getStartWeapon() {
        return startWeapon;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public static UI getUi() {
        return ui;
    }

    public static World getMainWorld() {
        return middle;
    }

    public static Stage getStage() {
        return stage;
    }

    public static boolean tap(float x, float y) {
        boolean res = ui.tap((int)x,(int)y);
        if(!res) {
            ui.point(x,y);
        }
        return res;
    }

    public static boolean removeFinger(float x, float y) {
        ui.swipeEnd(x,y);
        return false;
    }

    public void heroInPortal() {
        flash = true;
        hero.stop();
    }

    public World getBackgroundWorld() {
        return background;
    }

    public World getForegroundWorld() {
        return foreground;
    }

    public void openLevelSelectionMenu() {
        currentState = STATE_IN_LEVEL_SELECTION;
        timeSinceLevelSelectionMenuOpen = 0;
    }

    public void drawLoadingScreen() {
        levelSelectionMenu.draw();
    }

    public static LevelSelectionMenu getLevelSelectionMenu() {
        return levelSelectionMenu;
    }
    LoadingScreen screen;
    Thread thread;
    public void prepare(final Level level) {
        currentLevelNumber = level.getNumber();

        screen = new LoadingScreen();
        levelSelectionMenu.addActor(screen);
        screen.setZIndex(10000);
        drawLoadingScreen();
        System.out.print("Init box2d............");
        init("box2d");
        drawLoadingScreen();
        System.out.print("Init hero.............");
        init("hero");
        drawLoadingScreen();
        System.out.print("Init camera...........");
        init("camera");
        drawLoadingScreen();
        System.out.print("Init stage............");
        init("stage");
        drawLoadingScreen();
        System.out.print("Init ui...............");
        init("ui");
        drawLoadingScreen();
        start(level);
    }

    public int getCurrentState() {
        return currentState;
    }

    public void choose(int kind) {
        if(!choose) {
            switch (kind) {
                case Booster.KIND_HEALTH:
                    currentChoice = new HealthBoosterChoice(ui);
                    break;
                case Booster.KIND_DAMAGE:
                    currentChoice = new DamageBoosterChoice(ui);
                    break;
                case Booster.KIND_MONEY:
                    currentChoice = new MoneyBoosterChoice(ui);
                    break;
            }
            choose = true;
            freeze();
        }
    }

    public BoosterChoice getCurrentBoosterChoice() {
        return currentChoice;
    }

    public void chooseEnd() {
        choose = false;
        unfreeze();
    }

    public static boolean isPaused() {
        return paused;
    }

    public void freeze() {
        freeze = true;
        hero.stop(true);
        for(Enemy e:enemies) {
            e.setAnimation("idle");
        }
    }

    public void unfreeze() {
        freeze = false;
    }

    public boolean isFreezed() {
        return freeze;
    }

    public static int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public void registerBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public static ArrayList<Bullet> getBullets() {
        return bullets;
    }



}

/*

<?xml version="1.0" encoding="UTF-8"?>
<map version="1.2" tiledversion="1.2.2" orientation="orthogonal" renderorder="right-down" width="96" height="48" tilewidth="8" tileheight="8" infinite="0" nextlayerid="2" nextobjectid="1">
 <tileset firstgid="1" source="D:/Agility_tiles.tsx"/>
 <layer id="1" name="Слой тайлов 1" width="96" height="48">
  <data encoding="csv">
3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,
3,3,100,100,100,100,107,100,100,100,100,100,100,107,100,100,100,100,100,100,100,100,100,100,100,100,100,107,107,107,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,3,3,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,3,3,
3,82,0,0,7,0,54,7,120,120,120,120,120,7,0,0,7,0,7,7,0,7,0,0,0,7,0,0,0,0,7,0,0,7,0,7,0,0,7,0,7,120,120,7,0,7,0,7,120,0,7,7,0,93,82,0,7,0,7,7,0,152,150,151,120,120,120,120,0,7,0,0,7,7,0,7,0,120,120,120,120,57,7,0,120,7,152,150,150,150,151,120,120,7,93,3,
3,82,0,0,0,0,0,7,0,0,0,0,0,7,0,0,7,0,0,0,0,7,0,0,0,0,0,128,0,0,7,0,0,7,0,7,0,0,0,0,7,0,0,7,0,7,0,7,0,0,7,7,118,93,82,0,7,0,0,7,0,0,0,0,0,0,0,0,0,7,0,0,0,7,0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,7,93,3,
3,82,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,0,0,0,0,7,0,0,0,0,0,130,0,0,7,0,0,7,0,0,0,0,0,0,0,0,183,0,0,7,0,0,0,184,7,7,0,93,82,0,7,0,0,7,0,0,0,0,183,0,0,0,0,0,0,0,0,7,0,0,0,183,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,93,3,
3,82,137,134,137,138,0,143,138,138,54,138,0,91,0,0,0,0,0,54,138,138,142,138,91,0,138,147,138,175,138,138,0,7,0,0,138,0,138,138,84,138,0,91,138,138,57,138,138,0,84,7,134,93,82,121,7,0,0,7,84,91,138,138,138,138,134,137,138,138,0,84,174,0,91,0,138,138,138,57,138,138,138,138,0,84,0,138,0,0,0,0,0,0,93,3,
3,3,65,65,65,65,78,78,78,65,65,65,65,65,66,0,0,0,0,77,78,78,78,78,78,78,78,65,65,65,65,66,0,7,0,0,77,78,78,78,78,78,78,78,65,65,65,65,65,65,65,65,65,3,82,0,7,0,0,77,65,65,65,65,65,65,65,65,65,65,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,66,0,0,0,0,93,3,
3,3,100,100,100,100,100,100,100,100,100,100,100,3,82,0,0,0,0,93,3,3,3,100,100,100,100,100,100,100,3,82,0,7,0,0,93,3,100,100,107,100,100,100,100,100,100,107,100,100,100,100,100,3,82,0,0,0,118,93,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,82,0,0,128,0,93,3,
3,82,153,153,151,7,7,120,120,7,152,153,153,93,82,0,128,0,0,93,3,3,82,153,153,151,0,7,0,7,93,82,121,7,0,0,93,82,0,7,0,120,0,7,0,0,128,0,0,120,120,7,0,93,82,0,0,0,118,93,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,82,121,0,130,0,93,3,
3,82,0,0,0,7,0,0,0,7,0,0,118,93,82,0,130,0,0,93,3,3,82,0,0,0,0,7,0,0,93,82,0,0,0,118,93,82,0,7,0,0,0,0,0,0,130,0,0,0,0,7,0,93,82,121,0,0,118,93,3,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,108,0,0,130,0,93,3,
3,82,0,183,0,7,0,0,0,7,0,0,0,93,82,0,130,0,118,93,3,3,82,0,0,128,0,7,0,0,93,82,0,0,0,0,93,82,0,7,0,0,0,0,0,0,130,0,0,0,0,0,0,93,82,0,0,0,0,93,82,0,0,7,0,7,7,0,120,0,7,120,0,7,7,120,120,0,0,7,0,120,7,7,0,120,0,7,0,7,0,0,130,0,93,3,
3,82,137,0,54,7,0,0,0,7,0,84,134,93,82,0,130,0,0,93,3,3,82,121,0,130,0,0,84,134,93,82,121,0,128,0,93,82,0,0,0,0,0,0,0,128,130,0,0,0,0,0,0,93,82,121,0,0,0,93,82,121,0,7,0,0,7,0,0,0,7,0,0,7,7,0,0,0,0,0,0,0,7,0,0,0,0,7,0,7,0,0,112,0,93,3,
3,3,65,65,65,66,0,0,0,7,77,65,65,3,82,0,130,0,118,93,3,3,82,0,0,130,0,77,65,65,3,82,0,0,147,0,93,82,0,0,0,0,0,0,0,130,130,0,0,0,0,0,0,93,82,121,0,128,0,93,82,0,0,7,0,0,7,0,0,0,0,0,183,0,7,0,0,0,0,0,0,0,7,0,0,0,0,0,183,7,0,0,0,0,93,3,
3,3,100,100,100,108,0,0,0,7,99,100,100,100,108,0,130,0,0,99,100,107,108,121,0,130,0,99,107,107,3,82,0,0,147,0,93,82,137,175,84,138,138,0,0,130,130,0,0,0,143,91,134,93,82,0,0,130,118,93,82,137,175,84,0,0,7,0,0,0,138,91,91,138,138,138,138,0,84,0,0,0,7,0,0,84,138,138,138,138,138,138,142,134,93,3,
3,82,0,7,0,7,0,0,0,7,0,7,7,0,0,0,130,0,0,7,120,120,7,0,0,130,0,7,7,0,93,82,0,0,130,0,93,3,65,65,65,65,65,66,0,130,130,0,77,77,77,77,65,3,82,0,0,130,0,93,3,65,65,65,66,0,7,0,0,77,65,65,65,65,65,65,65,65,65,66,0,0,7,0,77,65,78,78,78,78,78,78,78,78,3,3,
3,82,0,7,0,0,0,0,0,0,0,7,7,0,0,0,112,0,0,7,0,0,0,0,0,130,0,7,0,118,93,82,121,0,130,0,99,100,100,107,100,107,100,108,0,130,130,0,99,107,107,107,107,107,108,0,0,130,0,99,100,100,100,3,82,0,7,0,0,93,3,3,3,3,3,3,3,3,3,82,121,0,7,0,93,3,3,3,3,3,3,3,3,3,3,3,
3,82,0,7,0,0,0,0,54,0,0,183,0,0,0,0,183,0,0,0,0,0,0,0,0,130,0,0,0,0,93,82,121,0,130,0,54,7,0,7,0,7,0,0,0,130,130,0,38,0,7,0,7,0,7,0,0,130,0,7,7,0,7,93,82,0,7,0,0,93,3,100,100,100,100,100,100,100,100,108,0,0,7,0,99,100,100,100,100,100,100,100,100,100,3,3,
3,82,0,7,0,0,0,138,54,84,0,138,138,138,174,138,138,84,138,138,0,91,142,0,138,147,138,84,0,134,93,82,0,0,130,0,0,7,0,0,0,7,0,0,0,130,130,0,54,0,7,0,7,0,0,0,0,130,0,0,7,0,7,93,82,121,0,0,118,93,82,0,7,0,0,120,7,0,0,7,0,0,7,0,0,7,0,120,120,120,7,0,7,0,93,3,
3,82,0,0,0,0,77,65,65,78,65,65,65,65,65,65,65,65,65,78,65,65,65,78,65,65,65,65,65,78,3,82,0,0,130,0,0,7,0,0,183,0,0,0,0,130,130,0,54,0,7,0,0,0,0,183,0,130,0,0,7,0,0,93,82,0,0,0,0,93,82,121,7,0,0,0,0,0,0,7,0,0,0,0,0,7,0,0,0,0,7,0,7,0,93,3,
3,82,0,0,0,0,93,3,100,100,100,100,100,100,107,100,100,100,100,100,100,107,100,100,100,100,100,100,100,100,3,82,121,0,147,0,0,138,138,138,138,138,175,91,0,147,147,0,54,138,7,84,158,138,138,138,138,147,138,0,0,0,0,93,82,0,0,128,0,93,82,0,7,0,183,0,0,184,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,0,93,3,
3,82,0,0,0,0,93,82,0,7,41,0,120,7,0,0,7,7,0,0,7,0,120,0,7,0,7,0,0,0,93,82,0,0,130,0,77,65,65,65,65,65,65,66,160,161,161,162,77,65,65,65,65,65,65,65,65,65,66,0,0,0,118,93,82,0,0,130,0,93,82,137,142,84,84,54,138,138,138,138,138,138,134,137,0,84,0,138,0,0,0,0,7,0,93,3,
3,82,0,0,0,118,93,82,0,0,57,0,0,7,0,0,7,0,0,0,7,0,183,0,0,0,7,0,184,0,93,82,0,0,130,118,93,3,3,3,3,3,3,82,177,176,176,190,93,3,3,3,3,3,3,3,3,3,82,121,0,0,0,93,82,0,0,130,118,93,3,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,66,0,0,0,0,93,3,
3,82,0,0,128,118,93,82,137,0,57,0,0,7,0,0,138,138,138,0,0,138,138,138,138,0,7,134,137,134,93,82,121,0,147,0,93,3,3,3,3,3,3,3,65,65,65,65,3,3,3,3,3,3,3,3,3,3,82,0,0,0,0,93,82,121,0,130,0,93,3,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,3,82,0,0,0,0,93,3,
3,82,0,0,130,0,93,3,65,65,66,0,0,7,0,77,65,65,65,78,78,65,65,65,78,65,65,65,65,65,3,82,0,0,147,0,93,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,82,0,0,0,0,93,82,121,0,130,0,93,82,0,7,0,0,150,0,7,0,7,0,7,0,7,41,7,7,0,93,82,121,0,0,0,93,3,
3,82,0,0,130,0,99,100,100,100,108,0,0,0,0,99,100,100,100,100,100,100,100,100,100,100,100,100,100,100,3,82,121,0,130,0,99,100,107,100,100,100,100,100,100,100,100,100,100,100,100,3,3,100,100,100,100,100,108,0,0,0,118,93,82,0,0,130,0,99,108,0,7,0,0,0,0,0,0,7,0,0,0,7,57,0,7,0,93,82,0,0,128,0,93,3,
3,82,0,0,130,0,7,0,7,0,0,0,0,0,0,0,7,120,120,7,0,0,0,120,7,120,0,120,0,0,93,82,121,0,130,0,0,7,120,0,7,7,7,0,120,120,120,120,7,7,0,93,82,0,7,7,7,120,7,0,0,0,118,93,82,0,0,112,0,7,0,0,0,0,0,0,0,0,0,7,0,0,0,7,57,0,0,0,93,82,121,0,130,0,93,3,
3,82,0,0,130,0,0,0,7,0,0,0,0,0,128,0,7,0,0,7,0,0,0,0,0,0,0,0,0,0,93,82,121,0,147,0,0,7,0,0,7,7,0,0,128,0,128,0,0,7,0,93,82,0,0,7,7,0,7,0,0,0,0,93,82,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,57,0,0,0,93,82,121,0,130,0,93,3,
3,82,0,0,130,0,0,0,7,0,0,0,0,0,130,0,0,0,0,7,0,183,0,57,0,0,0,0,0,0,93,82,0,0,130,0,0,0,183,0,0,7,0,0,112,0,112,0,0,0,0,93,82,0,0,7,0,0,7,0,183,0,0,93,82,0,0,0,0,0,0,0,0,0,183,0,0,183,0,0,0,0,0,7,57,183,0,0,93,82,121,0,130,0,93,3,
3,82,137,91,147,138,141,138,138,173,138,134,137,138,147,0,138,138,138,138,138,0,84,57,134,137,138,185,0,134,93,82,137,0,147,84,138,138,138,138,138,138,0,0,0,0,0,0,84,0,134,93,82,0,0,7,0,54,0,133,84,133,134,93,82,137,84,54,138,138,91,134,137,133,133,133,133,133,134,137,84,175,133,133,57,0,0,134,93,82,0,0,130,0,93,3,
3,3,65,65,78,65,65,65,78,65,65,65,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,3,3,65,65,65,65,65,65,65,65,65,65,66,0,0,0,0,77,78,78,78,3,82,0,0,7,0,77,78,65,78,78,78,3,3,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,3,82,0,0,130,0,93,3,
3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,82,0,0,0,0,93,3,100,100,100,108,0,0,0,0,93,3,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,3,82,121,0,130,0,93,3,
3,3,100,100,100,100,100,100,100,100,100,3,3,3,3,3,100,100,100,100,100,100,100,100,100,100,100,100,100,3,3,3,3,3,107,107,107,107,107,107,107,107,108,135,0,0,118,93,82,153,153,151,7,0,0,128,118,93,82,150,151,7,120,120,7,7,120,152,150,150,150,151,120,120,120,7,120,120,54,7,0,0,7,0,7,7,0,150,93,82,0,0,147,0,93,3,
3,82,153,153,151,7,7,120,152,153,153,93,3,3,3,82,153,153,153,151,120,120,7,152,153,153,153,41,153,93,3,3,3,82,153,153,153,151,7,7,120,152,153,151,0,0,118,93,82,0,0,0,7,0,0,130,118,93,82,0,0,7,0,0,7,7,0,0,0,0,0,0,0,0,0,7,0,0,54,0,0,0,0,0,7,0,0,0,93,82,0,0,130,0,93,3,
3,82,0,0,0,0,7,0,0,0,0,93,3,3,3,82,0,0,0,0,0,0,7,0,0,0,0,57,0,93,3,3,3,82,0,0,0,0,7,7,0,0,0,0,0,0,0,93,82,0,0,0,7,0,0,130,0,93,82,0,0,0,0,0,0,7,0,0,0,0,0,183,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,93,82,0,0,130,0,93,3,
3,82,0,128,0,0,0,0,0,0,118,93,3,3,3,82,0,0,128,0,0,0,0,0,0,0,0,57,0,93,3,3,3,82,0,0,0,0,0,7,0,0,0,0,0,183,0,93,82,0,0,0,7,0,84,147,134,93,82,0,128,0,0,0,128,0,0,0,0,0,54,0,0,91,91,0,0,54,134,137,0,84,91,0,0,0,0,0,99,108,0,0,147,0,93,3,
3,82,0,130,0,0,0,0,0,0,0,99,100,100,100,108,0,0,130,0,0,84,133,133,0,0,0,57,0,99,100,100,100,108,0,0,0,0,0,84,138,138,138,134,137,84,134,93,82,0,0,0,0,77,65,65,65,3,82,0,130,0,0,0,130,0,0,0,77,65,65,65,65,65,65,65,65,65,65,65,65,65,65,66,0,0,0,0,7,0,0,0,130,0,93,3,
3,82,0,130,0,0,0,0,0,0,0,54,7,0,120,0,0,0,130,118,77,65,65,65,66,0,0,57,0,0,7,0,0,7,128,0,0,0,77,65,65,65,65,65,65,65,65,3,82,0,0,0,0,93,67,68,68,76,82,0,130,0,0,0,130,0,0,0,93,3,100,100,100,100,100,100,100,100,100,100,100,100,3,82,0,0,0,0,7,0,0,0,130,0,93,3,
3,82,0,130,0,0,77,66,0,0,0,0,7,0,0,0,0,0,130,118,93,3,3,3,82,0,0,0,0,0,7,0,0,7,130,0,0,0,93,3,100,100,100,100,100,100,100,3,82,0,128,0,0,93,83,67,76,92,82,0,130,181,0,0,130,0,0,0,93,82,153,151,7,0,7,7,120,120,7,7,152,153,93,82,121,0,0,183,7,0,0,0,130,0,93,3,
3,82,0,130,0,0,93,82,0,0,0,0,183,0,0,0,0,0,130,0,93,3,3,3,82,0,0,0,0,0,0,183,0,7,130,0,0,0,93,82,7,120,120,120,7,120,153,93,82,0,130,0,0,93,83,99,108,92,82,137,147,138,138,138,147,84,0,134,93,82,0,0,7,0,7,7,0,0,7,0,7,0,93,82,137,91,138,138,138,138,0,84,147,134,93,3,
3,82,0,130,0,118,93,82,137,0,0,84,133,133,91,0,134,137,147,134,93,3,3,3,82,137,0,57,175,133,133,133,84,84,147,133,57,134,93,82,7,0,0,0,0,0,0,93,82,0,130,0,0,93,99,100,100,108,3,65,65,65,65,65,65,65,65,65,3,82,0,0,0,0,7,0,0,0,7,0,7,0,93,3,65,65,65,65,65,65,65,65,65,65,3,3,
3,82,0,147,0,0,93,3,65,65,65,65,78,65,65,65,65,65,65,65,3,3,3,3,3,65,65,65,78,65,65,65,78,65,65,65,65,65,3,82,7,0,128,0,0,0,118,93,82,0,130,0,0,99,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,108,0,0,0,0,0,84,0,0,7,0,0,0,99,100,100,100,100,100,100,100,100,100,100,100,3,3,
3,82,0,112,0,0,99,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,108,0,0,130,0,0,0,118,93,82,0,130,0,0,7,0,7,7,0,0,7,0,7,7,120,120,120,7,0,7,120,0,0,0,0,77,65,65,66,0,0,0,0,0,57,0,7,0,7,120,120,120,7,7,0,93,3,
3,82,0,0,0,152,153,153,151,120,54,7,0,7,0,120,120,0,7,152,153,153,151,7,57,152,153,153,151,120,0,0,152,153,153,153,151,0,7,0,0,0,130,0,0,0,0,93,82,0,130,0,0,7,0,0,0,0,0,7,0,7,0,0,0,0,0,0,7,0,0,0,0,0,93,3,3,82,121,0,0,0,0,57,0,7,0,7,0,0,0,0,7,0,93,3,
3,82,0,0,0,0,183,0,0,0,0,7,0,0,0,0,0,0,7,0,0,183,0,0,57,183,0,0,0,0,0,0,0,0,0,0,0,0,7,0,183,0,130,0,0,184,0,93,82,0,130,183,0,7,0,0,0,0,183,0,0,7,0,0,0,0,0,0,7,0,0,183,0,0,93,3,3,82,0,0,0,0,0,0,0,0,183,7,0,184,0,183,7,0,93,3,
3,82,137,0,174,138,138,138,0,0,138,7,134,146,0,84,142,0,138,138,138,138,0,0,138,0,0,138,138,138,84,175,0,91,0,144,138,143,7,138,84,133,147,134,137,133,134,93,82,137,147,138,174,91,138,141,138,138,84,0,138,91,91,138,138,138,57,138,7,134,137,84,84,134,93,3,3,82,137,0,134,137,173,138,138,138,138,138,138,84,57,138,0,134,93,3,
3,3,65,65,65,78,65,65,65,65,65,78,78,65,65,65,65,65,65,65,65,65,78,78,78,78,78,65,65,65,65,65,65,78,65,65,65,65,65,65,65,65,65,78,65,65,65,3,3,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,3,3,3,3,65,65,65,65,65,65,65,65,65,65,65,65,65,65,78,78,3,3,
3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3

</data>
 </layer>
</map>

4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
4,4,101,101,101,101,108,101,101,101,101,101,101,108,101,101,101,101,101,101,101,101,101,101,101,101,101,108,108,108,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,4,4,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,4,4,
4,83,1,1,8,1,55,8,121,121,121,121,121,8,1,1,8,1,8,8,1,8,1,1,1,8,1,1,1,1,8,1,1,8,1,8,1,1,8,1,8,121,121,8,1,8,1,8,121,1,8,8,1,94,83,1,8,1,8,8,1,153,151,152,121,121,121,121,1,8,1,1,8,8,1,8,1,121,121,121,121,58,8,1,121,8,153,151,151,151,152,121,121,8,94,4,
4,83,1,1,1,1,1,8,1,1,1,1,1,8,1,1,8,1,1,1,1,8,1,1,1,1,1,129,1,1,8,1,1,8,1,8,1,1,1,1,8,1,1,8,1,8,1,8,1,1,8,8,119,94,83,1,8,1,1,8,1,1,1,1,1,1,1,1,1,8,1,1,1,8,1,1,1,1,1,1,1,1,8,1,1,1,1,1,1,1,1,1,1,8,94,4,
4,83,1,1,1,1,1,1,1,1,1,1,1,1,1,1,8,1,1,1,1,8,1,1,1,1,1,131,1,1,8,1,1,8,1,1,1,1,1,1,1,1,184,1,1,8,1,1,1,185,8,8,1,94,83,1,8,1,1,8,1,1,1,1,184,1,1,1,1,1,1,1,1,8,1,1,1,184,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,8,94,4,
4,83,138,135,138,139,1,144,139,139,55,139,1,92,1,1,1,1,1,55,139,139,143,139,92,1,139,148,139,176,139,139,1,8,1,1,139,1,139,139,85,139,1,92,139,139,58,139,139,1,85,8,135,94,83,122,8,1,1,8,85,92,139,139,139,139,135,138,139,139,1,85,175,1,92,1,139,139,139,58,139,139,139,139,1,85,1,139,1,1,1,1,1,1,94,4,
4,4,66,66,66,66,79,79,79,66,66,66,66,66,67,1,1,1,1,78,79,79,79,79,79,79,79,66,66,66,66,67,1,8,1,1,78,79,79,79,79,79,79,79,66,66,66,66,66,66,66,66,66,4,83,1,8,1,1,78,66,66,66,66,66,66,66,66,66,66,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,67,1,1,1,1,94,4,
4,4,101,101,101,101,101,101,101,101,101,101,101,4,83,1,1,1,1,94,4,4,4,101,101,101,101,101,101,101,4,83,1,8,1,1,94,4,101,101,108,101,101,101,101,101,101,108,101,101,101,101,101,4,83,1,1,1,119,94,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,83,1,1,129,1,94,4,
4,83,154,154,152,8,8,121,121,8,153,154,154,94,83,1,129,1,1,94,4,4,83,154,154,152,1,8,1,8,94,83,122,8,1,1,94,83,1,8,1,121,1,8,1,1,129,1,1,121,121,8,1,94,83,1,1,1,119,94,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,83,122,1,131,1,94,4,
4,83,1,1,1,8,1,1,1,8,1,1,119,94,83,1,131,1,1,94,4,4,83,1,1,1,1,8,1,1,94,83,1,1,1,119,94,83,1,8,1,1,1,1,1,1,131,1,1,1,1,8,1,94,83,122,1,1,119,94,4,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,109,1,1,131,1,94,4,
4,83,1,184,1,8,1,1,1,8,1,1,1,94,83,1,131,1,119,94,4,4,83,1,1,129,1,8,1,1,94,83,1,1,1,1,94,83,1,8,1,1,1,1,1,1,131,1,1,1,1,1,1,94,83,1,1,1,1,94,83,1,1,8,1,8,8,1,121,1,8,121,1,8,8,121,121,1,1,8,1,121,8,8,1,121,1,8,1,8,1,1,131,1,94,4,
4,83,138,1,55,8,1,1,1,8,1,85,135,94,83,1,131,1,1,94,4,4,83,122,1,131,1,1,85,135,94,83,122,1,129,1,94,83,1,1,1,1,1,1,1,129,131,1,1,1,1,1,1,94,83,122,1,1,1,94,83,122,1,8,1,1,8,1,1,1,8,1,1,8,8,1,1,1,1,1,1,1,8,1,1,1,1,8,1,8,1,1,113,1,94,4,
4,4,66,66,66,67,1,1,1,8,78,66,66,4,83,1,131,1,119,94,4,4,83,1,1,131,1,78,66,66,4,83,1,1,148,1,94,83,1,1,1,1,1,1,1,131,131,1,1,1,1,1,1,94,83,122,1,129,1,94,83,1,1,8,1,1,8,1,1,1,1,1,184,1,8,1,1,1,1,1,1,1,8,1,1,1,1,1,184,8,1,1,1,1,94,4,
4,4,101,101,101,109,1,1,1,8,100,101,101,101,109,1,131,1,1,100,101,108,109,122,1,131,1,100,108,108,4,83,1,1,148,1,94,83,138,176,85,139,139,1,1,131,131,1,1,1,144,92,135,94,83,1,1,131,119,94,83,138,176,85,1,1,8,1,1,1,139,92,92,139,139,139,139,1,85,1,1,1,8,1,1,85,139,139,139,139,139,139,143,135,94,4,
4,83,1,8,1,8,1,1,1,8,1,8,8,1,1,1,131,1,1,8,121,121,8,1,1,131,1,8,8,1,94,83,1,1,131,1,94,4,66,66,66,66,66,67,1,131,131,1,78,78,78,78,66,4,83,1,1,131,1,94,4,66,66,66,67,1,8,1,1,78,66,66,66,66,66,66,66,66,66,67,1,1,8,1,78,66,79,79,79,79,79,79,79,79,4,4,
4,83,1,8,1,1,1,1,1,1,1,8,8,1,1,1,113,1,1,8,1,1,1,1,1,131,1,8,1,119,94,83,122,1,131,1,100,101,101,108,101,108,101,109,1,131,131,1,100,108,108,108,108,108,109,1,1,131,1,100,101,101,101,4,83,1,8,1,1,94,4,4,4,4,4,4,4,4,4,83,122,1,8,1,94,4,4,4,4,4,4,4,4,4,4,4,
4,83,1,8,1,1,1,1,55,1,1,184,1,1,1,1,184,1,1,1,1,1,1,1,1,131,1,1,1,1,94,83,122,1,131,1,55,8,1,8,1,8,1,1,1,131,131,1,39,1,8,1,8,1,8,1,1,131,1,8,8,1,8,94,83,1,8,1,1,94,4,101,101,101,101,101,101,101,101,109,1,1,8,1,100,101,101,101,101,101,101,101,101,101,4,4,
4,83,1,8,1,1,1,139,55,85,1,139,139,139,175,139,139,85,139,139,1,92,143,1,139,148,139,85,1,135,94,83,1,1,131,1,1,8,1,1,1,8,1,1,1,131,131,1,55,1,8,1,8,1,1,1,1,131,1,1,8,1,8,94,83,122,1,1,119,94,83,1,8,1,1,121,8,1,1,8,1,1,8,1,1,8,1,121,121,121,8,1,8,1,94,4,
4,83,1,1,1,1,78,66,66,79,66,66,66,66,66,66,66,66,66,79,66,66,66,79,66,66,66,66,66,79,4,83,1,1,131,1,1,8,1,1,184,1,1,1,1,131,131,1,55,1,8,1,1,1,1,184,1,131,1,1,8,1,1,94,83,1,1,1,1,94,83,122,8,1,1,1,1,1,1,8,1,1,1,1,1,8,1,1,1,1,8,1,8,1,94,4,
4,83,1,1,1,1,94,4,101,101,101,101,101,101,108,101,101,101,101,101,101,108,101,101,101,101,101,101,101,101,4,83,122,1,148,1,1,139,139,139,139,139,176,92,1,148,148,1,55,139,8,85,159,139,139,139,139,148,139,1,1,1,1,94,83,1,1,129,1,94,83,1,8,1,184,1,1,185,1,1,1,1,1,1,1,1,1,1,1,1,1,1,8,1,94,4,
4,83,1,1,1,1,94,83,1,8,42,1,121,8,1,1,8,8,1,1,8,1,121,1,8,1,8,1,1,1,94,83,1,1,131,1,78,66,66,66,66,66,66,67,161,162,162,163,78,66,66,66,66,66,66,66,66,66,67,1,1,1,119,94,83,1,1,131,1,94,83,138,143,85,85,55,139,139,139,139,139,139,135,138,1,85,1,139,1,1,1,1,8,1,94,4,
4,83,1,1,1,119,94,83,1,1,58,1,1,8,1,1,8,1,1,1,8,1,184,1,1,1,8,1,185,1,94,83,1,1,131,119,94,4,4,4,4,4,4,83,178,177,177,191,94,4,4,4,4,4,4,4,4,4,83,122,1,1,1,94,83,1,1,131,119,94,4,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,67,1,1,1,1,94,4,
4,83,1,1,129,119,94,83,138,1,58,1,1,8,1,1,139,139,139,1,1,139,139,139,139,1,8,135,138,135,94,83,122,1,148,1,94,4,4,4,4,4,4,4,66,66,66,66,4,4,4,4,4,4,4,4,4,4,83,1,1,1,1,94,83,122,1,131,1,94,4,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,4,83,1,1,1,1,94,4,
4,83,1,1,131,1,94,4,66,66,67,1,1,8,1,78,66,66,66,79,79,66,66,66,79,66,66,66,66,66,4,83,1,1,148,1,94,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,83,1,1,1,1,94,83,122,1,131,1,94,83,1,8,1,1,151,1,8,1,8,1,8,1,8,42,8,8,1,94,83,122,1,1,1,94,4,
4,83,1,1,131,1,100,101,101,101,109,1,1,1,1,100,101,101,101,101,101,101,101,101,101,101,101,101,101,101,4,83,122,1,131,1,100,101,108,101,101,101,101,101,101,101,101,101,101,101,101,4,4,101,101,101,101,101,109,1,1,1,119,94,83,1,1,131,1,100,109,1,8,1,1,1,1,1,1,8,1,1,1,8,58,1,8,1,94,83,1,1,129,1,94,4,
4,83,1,1,131,1,8,1,8,1,1,1,1,1,1,1,8,121,121,8,1,1,1,121,8,121,1,121,1,1,94,83,122,1,131,1,1,8,121,1,8,8,8,1,121,121,121,121,8,8,1,94,83,1,8,8,8,121,8,1,1,1,119,94,83,1,1,113,1,8,1,1,1,1,1,1,1,1,1,8,1,1,1,8,58,1,1,1,94,83,122,1,131,1,94,4,
4,83,1,1,131,1,1,1,8,1,1,1,1,1,129,1,8,1,1,8,1,1,1,1,1,1,1,1,1,1,94,83,122,1,148,1,1,8,1,1,8,8,1,1,129,1,129,1,1,8,1,94,83,1,1,8,8,1,8,1,1,1,1,94,83,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,8,58,1,1,1,94,83,122,1,131,1,94,4,
4,83,1,1,131,1,1,1,8,1,1,1,1,1,131,1,1,1,1,8,1,184,1,58,1,1,1,1,1,1,94,83,1,1,131,1,1,1,184,1,1,8,1,1,113,1,113,1,1,1,1,94,83,1,1,8,1,1,8,1,184,1,1,94,83,1,1,1,1,1,1,1,1,1,184,1,1,184,1,1,1,1,1,8,58,184,1,1,94,83,122,1,131,1,94,4,
4,83,138,92,148,139,142,139,139,174,139,135,138,139,148,1,139,139,139,139,139,1,85,58,135,138,139,186,1,135,94,83,138,1,148,85,139,139,139,139,139,139,1,1,1,1,1,1,85,1,135,94,83,1,1,8,1,55,1,134,85,134,135,94,83,138,85,55,139,139,92,135,138,134,134,134,134,134,135,138,85,176,134,134,58,1,1,135,94,83,1,1,131,1,94,4,
4,4,66,66,79,66,66,66,79,66,66,66,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,79,4,4,66,66,66,66,66,66,66,66,66,66,67,1,1,1,1,78,79,79,79,4,83,1,1,8,1,78,79,66,79,79,79,4,4,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,4,83,1,1,131,1,94,4,
4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,83,1,1,1,1,94,4,101,101,101,109,1,1,1,1,94,4,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,4,83,122,1,131,1,94,4,
4,4,101,101,101,101,101,101,101,101,101,4,4,4,4,4,101,101,101,101,101,101,101,101,101,101,101,101,101,4,4,4,4,4,108,108,108,108,108,108,108,108,109,136,1,1,119,94,83,154,154,152,8,1,1,129,119,94,83,151,152,8,121,121,8,8,121,153,151,151,151,152,121,121,121,8,121,121,55,8,1,1,8,1,8,8,1,151,94,83,1,1,148,1,94,4,
4,83,154,154,152,8,8,121,153,154,154,94,4,4,4,83,154,154,154,152,121,121,8,153,154,154,154,42,154,94,4,4,4,83,154,154,154,152,8,8,121,153,154,152,1,1,119,94,83,1,1,1,8,1,1,131,119,94,83,1,1,8,1,1,8,8,1,1,1,1,1,1,1,1,1,8,1,1,55,1,1,1,1,1,8,1,1,1,94,83,1,1,131,1,94,4,
4,83,1,1,1,1,8,1,1,1,1,94,4,4,4,83,1,1,1,1,1,1,8,1,1,1,1,58,1,94,4,4,4,83,1,1,1,1,8,8,1,1,1,1,1,1,1,94,83,1,1,1,8,1,1,131,1,94,83,1,1,1,1,1,1,8,1,1,1,1,1,184,1,1,1,8,1,1,1,1,1,1,1,1,1,1,1,1,94,83,1,1,131,1,94,4,
4,83,1,129,1,1,1,1,1,1,119,94,4,4,4,83,1,1,129,1,1,1,1,1,1,1,1,58,1,94,4,4,4,83,1,1,1,1,1,8,1,1,1,1,1,184,1,94,83,1,1,1,8,1,85,148,135,94,83,1,129,1,1,1,129,1,1,1,1,1,55,1,1,92,92,1,1,55,135,138,1,85,92,1,1,1,1,1,100,109,1,1,148,1,94,4,
4,83,1,131,1,1,1,1,1,1,1,100,101,101,101,109,1,1,131,1,1,85,134,134,1,1,1,58,1,100,101,101,101,109,1,1,1,1,1,85,139,139,139,135,138,85,135,94,83,1,1,1,1,78,66,66,66,4,83,1,131,1,1,1,131,1,1,1,78,66,66,66,66,66,66,66,66,66,66,66,66,66,66,67,1,1,1,1,8,1,1,1,131,1,94,4,
4,83,1,131,1,1,1,1,1,1,1,55,8,1,121,1,1,1,131,119,78,66,66,66,67,1,1,58,1,1,8,1,1,8,129,1,1,1,78,66,66,66,66,66,66,66,66,4,83,1,1,1,1,94,68,69,69,77,83,1,131,1,1,1,131,1,1,1,94,4,101,101,101,101,101,101,101,101,101,101,101,101,4,83,1,1,1,1,8,1,1,1,131,1,94,4,
4,83,1,131,1,1,78,67,1,1,1,1,8,1,1,1,1,1,131,119,94,4,4,4,83,1,1,1,1,1,8,1,1,8,131,1,1,1,94,4,101,101,101,101,101,101,101,4,83,1,129,1,1,94,84,68,77,93,83,1,131,182,1,1,131,1,1,1,94,83,154,152,8,1,8,8,121,121,8,8,153,154,94,83,122,1,1,184,8,1,1,1,131,1,94,4,
4,83,1,131,1,1,94,83,1,1,1,1,184,1,1,1,1,1,131,1,94,4,4,4,83,1,1,1,1,1,1,184,1,8,131,1,1,1,94,83,8,121,121,121,8,121,154,94,83,1,131,1,1,94,84,100,109,93,83,138,148,139,139,139,148,85,1,135,94,83,1,1,8,1,8,8,1,1,8,1,8,1,94,83,138,92,139,139,139,139,1,85,148,135,94,4,
4,83,1,131,1,119,94,83,138,1,1,85,134,134,92,1,135,138,148,135,94,4,4,4,83,138,1,58,176,134,134,134,85,85,148,134,58,135,94,83,8,1,1,1,1,1,1,94,83,1,131,1,1,94,100,101,101,109,4,66,66,66,66,66,66,66,66,66,4,83,1,1,1,1,8,1,1,1,8,1,8,1,94,4,66,66,66,66,66,66,66,66,66,66,4,4,
4,83,1,148,1,1,94,4,66,66,66,66,79,66,66,66,66,66,66,66,4,4,4,4,4,66,66,66,79,66,66,66,79,66,66,66,66,66,4,83,8,1,129,1,1,1,119,94,83,1,131,1,1,100,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,109,1,1,1,1,1,85,1,1,8,1,1,1,100,101,101,101,101,101,101,101,101,101,101,101,4,4,
4,83,1,113,1,1,100,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,109,1,1,131,1,1,1,119,94,83,1,131,1,1,8,1,8,8,1,1,8,1,8,8,121,121,121,8,1,8,121,1,1,1,1,78,66,66,67,1,1,1,1,1,58,1,8,1,8,121,121,121,8,8,1,94,4,
4,83,1,1,1,153,154,154,152,121,55,8,1,8,1,121,121,1,8,153,154,154,152,8,58,153,154,154,152,121,1,1,153,154,154,154,152,1,8,1,1,1,131,1,1,1,1,94,83,1,131,1,1,8,1,1,1,1,1,8,1,8,1,1,1,1,1,1,8,1,1,1,1,1,94,4,4,83,122,1,1,1,1,58,1,8,1,8,1,1,1,1,8,1,94,4,
4,83,1,1,1,1,184,1,1,1,1,8,1,1,1,1,1,1,8,1,1,184,1,1,58,184,1,1,1,1,1,1,1,1,1,1,1,1,8,1,184,1,131,1,1,185,1,94,83,1,131,184,1,8,1,1,1,1,184,1,1,8,1,1,1,1,1,1,8,1,1,184,1,1,94,4,4,83,1,1,1,1,1,1,1,1,184,8,1,185,1,184,8,1,94,4,
4,83,138,1,175,139,139,139,1,1,139,8,135,147,1,85,143,1,139,139,139,139,1,1,139,1,1,139,139,139,85,176,1,92,1,145,139,144,8,139,85,134,148,135,138,134,135,94,83,138,148,139,175,92,139,142,139,139,85,1,139,92,92,139,139,139,58,139,8,135,138,85,85,135,94,4,4,83,138,1,135,138,174,139,139,139,139,139,139,85,58,139,1,135,94,4,
4,4,66,66,66,79,66,66,66,66,66,79,79,66,66,66,66,66,66,66,66,66,79,79,79,79,79,66,66,66,66,66,66,79,66,66,66,66,66,66,66,66,66,79,66,66,66,4,4,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,4,4,4,4,66,66,66,66,66,66,66,66,66,66,66,66,66,66,79,79,4,4,
4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4 */