package com.agility.game.UI;

import com.agility.game.Game;
import com.agility.game.Hero;
import com.agility.game.UI.LevelSelection.LevelSelectionItemsHandler;
import com.agility.game.Utils.GameBalanceConstants;
import com.agility.game.Utils.UIButtonEvent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.logging.Level;

public class UI extends Stage {
    public boolean tapOnUI, swipeOpacityDecreaseUnlocked;
    private static BitmapFont font, nextLevelFont, currentLevelFont;
    private HeroHealthPanel healthPanel;
    private String message = "";
    private float opacity = 0.99f;
    private Game game;
    private Sprite point, end;
    private float swipeOpacity, stateTime, hint;
    public boolean drawText;
    private String hintMessage;
    public static String drawTextMessage;
    public float drawTextX,drawTextY,drawTextOpacity;

    private UIButton pause;

    private ItemEquipRequest itemEquipRequest;
    private BoosterTakeRequest boosterTakeRequest;

    private static ShapeRenderer debugRenderer = new ShapeRenderer();
    private boolean drawBossHealth, hintTriggered;
    private float healthPercents;
    private Texture hpbg;
    private Texture hpfg;


    public UI(final Game game) {
        super();

        healthPanel = new HeroHealthPanel(game);
        addActor(healthPanel);
        itemEquipRequest = new ItemEquipRequest();
        addActor(itemEquipRequest);
        boosterTakeRequest = new BoosterTakeRequest();
        addActor(boosterTakeRequest);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("basis33OLD.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        parameter.size = 64;
        parameter.color = Color.GOLD;//new Color(141f/255f,207f/255f,228f/255f,0.8f);
        parameter.characters = GameBalanceConstants.RUSSIAN_CHARACTERS;
        nextLevelFont = generator.generateFont(parameter);
        parameter.size = 100;
        parameter.color = Color.GOLD;//new Color(141f/255f,207f/255f,228f/255f,0.8f);
        parameter.characters = GameBalanceConstants.RUSSIAN_CHARACTERS;
        currentLevelFont = generator.generateFont(parameter);

        point = new Sprite(new Texture(Gdx.files.internal("point.png")));
        point.setAlpha(0);

        end = new Sprite(new Texture(Gdx.files.internal("point.png")));
        end.setAlpha(0);

        pause = new UIButton(new UIButtonEvent() {
            @Override
            public void handle() {
                if(!Game.isPaused()) {
                    addPausePanel();
                }
            }
        }, new Vector2(Gdx.graphics.getWidth() - 128, Gdx.graphics.getHeight() - 128), 128, "Icon_Pause2");
        addActor(pause);

        this.game = game;
    }

    public void start() {
        stateTime = 0;
    }

    private void addPausePanel() {
        addActor(new PausePanel(game, Game.getUi()));
        game.pause(true);
        tapOnUI = true;
    }

    public void addFlingPiece(int x, int y) {
        addActor(new FlingPiece(x,y));
    }

    @Override
    public void draw() {
        super.draw();
        getBatch().begin();
        font.setColor(0.8f, 0.8f, 0.8f, opacity);
        font.draw(getBatch(), message, 40, 50);
        if(drawText) {
            font.setColor(0.8f, 0.8f, 0.8f, drawTextOpacity);
            font.draw(getBatch(),drawTextMessage,drawTextX,drawTextY);
            drawTextOpacity-=(1-drawTextOpacity)/25;
            if(drawTextOpacity <= 0.01) {
                drawText = false;
            }
        }

        if(drawBossHealth) {
            getBatch().draw(hpbg, Gdx.graphics.getWidth()/4-4, Gdx.graphics.getHeight()-100-4, Gdx.graphics.getWidth()/2+8, hpbg.getHeight()+4+8);
            getBatch().draw(hpfg,Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()-100, Gdx.graphics.getWidth()/2 * healthPercents, hpbg.getHeight()+4);
            drawBossHealth = false;
        }
        if(game.getCurrentState() == Game.STATE_IN_GAME) {
            if(LevelSelectionItemsHandler.items.length != Game.getCurrentLevelNumber()) {
                Vector2 exitPortalTextPos = game.exitPortal.getTextDrawPosition();
                nextLevelFont.draw(getBatch(), LevelSelectionItemsHandler.items[Game.getCurrentLevelNumber()].drawableName, exitPortalTextPos.x + (8 - LevelSelectionItemsHandler.items[Game.getCurrentLevelNumber()].drawableName.length()) * (12), exitPortalTextPos.y);
            }
            if(stateTime < 8) {
                String levelName = LevelSelectionItemsHandler.items[Game.getCurrentLevelNumber()-1].drawableName;

                GlyphLayout glyphLayout = new GlyphLayout();
                glyphLayout.setText(currentLevelFont, levelName);
                float w = glyphLayout.width;
                currentLevelFont.setColor(currentLevelFont.getColor().r, currentLevelFont.getColor().g,
                        currentLevelFont.getColor().b, (float) Math.sin((stateTime-2)/4*Math.PI));
                currentLevelFont.draw(getBatch(), levelName, Gdx.graphics.getWidth()/2-w/2, Gdx.graphics.getHeight()*3/4);
            }
            else if(Game.getCurrentLevelNumber() == 1 && !hintTriggered){
                hintTriggered = true;
                hint("Что бы побежать вправо, свайпните вправо");
            }
        }
        if(hint > 0) {
            Color prevColor = currentLevelFont.getColor();
            currentLevelFont.getData().setScale(0.5f);
            currentLevelFont.setColor(Color.GOLD);
            hint-=Gdx.graphics.getDeltaTime();
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(currentLevelFont, hintMessage);
            float w = glyphLayout.width;
            currentLevelFont.draw(getBatch(), hintMessage, Gdx.graphics.getWidth()/2-w/2, Gdx.graphics.getHeight()*3/4);
            currentLevelFont.setColor(prevColor);
        }

        Hero.blood.draw(Game.getUi().getBatch(), (float)Math.pow(1 - game.getHero().getHealth()/game.getHero().getMaxHealth(),2));
        if(game.getHero().damaged > 0) {
            Hero.blood.draw(Game.getUi().getBatch(),0.7f*(game.getHero().damaged/20f));
        }
        getBatch().end();
        opacity-=(1-opacity)/25;
        stateTime += Gdx.graphics.getDeltaTime();
    }

    public void hint(String message){
        hintMessage = message;
        hint = 100;
        Game.getHero().stop();
    }

    public boolean tap(int x, int y) {
        tapOnUI = false;
        for (int i = 0; i < getActors().size; i++) {
            try {
                getActors().get(i).hit((float) x, (float) y, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tapOnUI;
    }

    public void drawText(String text, float x, float y) {
        drawText = true;
        drawTextX = x;
        drawTextY = y;
        drawTextMessage = text;
        drawTextOpacity = 0.99f;
    }

    @Override
    public void act() {
        super.act();
    }


    public void log(String message) {
        this.message = message;
        opacity = 0.99f;
    }

    public void point(float x, float y) {
        point.setAlpha(1);
        point.setCenter(x,y);

        end.setAlpha(0);
        swipeOpacity = 0.99999f;
    }
    public void swipeEnd(float x, float y) {
        end.setAlpha(1);
        end.setCenter(x,y);
        swipeOpacityDecreaseUnlocked = true;
        swipeOpacity = 0.99999f;
    }

    public static void drawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix, float opacity)
    {
        Gdx.gl.glLineWidth(5);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.LIGHT_GRAY);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public void logFPS() {
        Game.log("FPS: "+Gdx.graphics.getFramesPerSecond());
    }

    public Game getGame() {
        return game;
    }

    public void drawBossHealth(float healthPercents, Texture hpbg, Texture hpfg) {
        drawBossHealth = true;
        this.healthPercents = healthPercents;
        this.hpbg = hpbg;
        this.hpfg = hpfg;
    }
}
