package com.agility.game;

import com.agility.game.WorldObjects.Block;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class BlockFactory {
    private static Texture atlas;
    public static final int TILES_FOR_X = 16;

    private static final ArrayList<Integer> midLayerIds = new ArrayList<Integer>();
    private static final ArrayList<Integer> fgLayerIds = new ArrayList<Integer>();
    private static final ArrayList<Integer> reserv = new ArrayList<Integer>();

    // Decorations
    public static ArrayList<Vector2> anvilsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> barrelsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> chestsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> cobblestonesPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> firesPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> fountainsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> pristsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> signsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> vasesPos = new ArrayList<Vector2>();


    private static final BlockFactory ourInstance = new BlockFactory();
    public static Vector2 heroStartPos;
    public static Vector2 startWeaponPos;
    public static Vector2 bossPos;
    public static ArrayList<Vector2> enemiesPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> boostsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> gatesPos = new ArrayList<Vector2>();

    // NPC
    public static ArrayList<Vector2> castersPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> ninjasPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> warriorsPos = new ArrayList<Vector2>();
    public static ArrayList<Vector2> witchesPos = new ArrayList<Vector2>();


    public static Vector2 exitPos;


    public static BlockFactory getInstance() {
        if (atlas == null) {
            atlas = new Texture(Gdx.files.internal("block_tiles.png"));
        }
        return ourInstance;
    }

    private BlockFactory() {

        final int[] ml = {16, 17, 18, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30, 31, 32, 33,
                34, 35, 36, 43, 44, 45, 46, 47, 48, 49,  51, 52,
                59, 60, 62, 63, 64, 65, 66, 67, 68, 69, 74, 75,
                76, 77, 78, 79, 80, 82,83,92,93,95,98,99,100,107,108
                ,109,208,209,210,214,215,216,217,218,230,231,232,233,
                234,246,247,248,249,250,262,263,264,265,266,278,279,
                294,295,224,225,226,240,241,242, 211,212,213,227,228,
                229,243,244,245,513,514,529,530,546};
        for (int i = 0; i < ml.length; i++) {
            midLayerIds.add(ml[i]);
        }

        final int[] fgl = {90,106,42,58,94,11,111,116,141,156,123,124,125,126,155,156,157,158,159,168,169,170,50,165, 61, 170,91, 178, 160,161,162, 166};
        for (int i = 0; i < fgl.length; i++) {
            fgLayerIds.add(fgl[i]);
        }
        final int[] reserved = {181, 182, 183, 184, 185, 186, 141, 142, 143, 157, 158, 159, 173, 174, 175, 187, 203, 219, 235, 251};
        for (int i = 0; i < reserved.length; i++) {
            reserv.add(reserved[i]);
        }
    }
    public Block create(int tileId, Vector2 position, World bg, World mid, World fg) {
        TextureRegion tile = null;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.x = position.x+4;
        def.position.y = position.y+4;
        Body body = null;

        int layer = 1;
        if(midLayerIds.contains(tileId)) {
            body = mid.createBody(def);
            layer = 2;
            body.setSleepingAllowed(true);
            body.setGravityScale(0);

            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(4,4);
            fixtureDef.shape = shape;
            fixtureDef.friction = 1f;
            fixtureDef.density = 1;

            body.createFixture(fixtureDef);
            body.setUserData("block");
        }
        else if(reserv.contains(tileId)) {
            switch (tileId) {
                case (181):
                    heroStartPos = position;
                    break;
                case (182):
                    startWeaponPos = position;
                    break;
                case (183):
                    enemiesPos.add(position);
                    break;
                case (184):
                    boostsPos.add(position);
                    break;
                case (185):
                    exitPos = position;
                    break;
                case (186):
                    gatesPos.add(position);
                    break;
                case (187):
                    bossPos = position;
                    break;

                // Decorations
                case (141):
                    anvilsPos.add(position);
                    break;
                case (142):
                    barrelsPos.add(position);
                    break;
                case (143):
                    chestsPos.add(position);
                    break;
                case (157):
                    cobblestonesPos.add(position);
                    break;
                case (158):
                    firesPos.add(position);
                    break;
                case (159):
                    fountainsPos.add(position);
                    break;
                case (173):
                    pristsPos.add(position);
                    break;
                case (174):
                    signsPos.add(position);
                    break;
                case (175):
                    vasesPos.add(position);
                    break;

                // NPC
                case (203):
                    castersPos.add(position);
                    break;
                case (219):
                    ninjasPos.add(position);
                    break;
                case (235):
                    warriorsPos.add(position);
                    break;
                case (251):
                    witchesPos.add(position);
                    break;
            }
            layer = 1;
            tile = new TextureRegion(atlas, 0, 0, 8, 8);
        }
        else if(fgLayerIds.contains(tileId)){
            layer = 99999;
        }




        Vector2 tilePosition = new Vector2(tileId%TILES_FOR_X,tileId/TILES_FOR_X);
        if(tile == null) {
            tile = new TextureRegion(atlas, (int) tilePosition.x * 8, (int) tilePosition.y * 8, 8, 8);
        }



        return new Block(tile,body,layer,position,tileId);
    }

    public static void refreshVariables() {
        heroStartPos = null;
        startWeaponPos = null;
        gatesPos.clear();
        exitPos = null;
        enemiesPos.clear();
        boostsPos.clear();
        bossPos = null;

        // Clear decorations positions
        anvilsPos.clear();
        barrelsPos.clear();
        chestsPos.clear();
        cobblestonesPos.clear();
        firesPos.clear();
        fountainsPos.clear();
        pristsPos.clear();
        signsPos.clear();
        vasesPos.clear();

        // Clear NPC positions
        castersPos.clear();
        ninjasPos.clear();
        warriorsPos.clear();
        witchesPos.clear();
    }
}
