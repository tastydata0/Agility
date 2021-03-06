package com.agility.game.UI.LevelSelection;

import com.agility.game.BlockFactory;
import com.agility.game.Map;
import com.agility.game.Utils.MapParser;
import com.agility.game.WorldObjects.Block;
import com.badlogic.gdx.math.Vector2;
import com.agility.game.Game;

public class Level {

    private Map map;
    private Block[][] block;
    private String name;
    private Game game;
    private int number;

    public Level(Game game, String name) {
        this.name = name;
        this.game = game;
    }

    public void init() {
        map = MapParser.getInstance().parse(name + ".csv");
        block = new Block[map.getCells().length][map.getCells()[0].length];

        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[0].length; j++) {
                block[i][j] = BlockFactory.getInstance().create(map.getCells()[i][j], new Vector2(8 * i, 8 * j), game.getBackgroundWorld(), game.getMainWorld(), game.getForegroundWorld());
                if(block[i][j].layer != 99999) {
                    block[i][j].setZIndex(block[i][j].layer);
                    Game.getStage().addActor(block[i][j]);
                }
            }
        }
    }

    public void initForeground() {
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[0].length; j++) {
                if(block[i][j].layer == 99999) {
                    block[i][j].setZIndex(99999+i);
                    block[i][j].setName("foregroundBlock");
                    Game.getStage().addActor(block[i][j]);
                }
            }
        }
    }

    public Map getMap() {
        return map;
    }

    public String getName() {
        return name;
    }

    public Block[][] getBlock() {
        return block;
    }

    public void start() {
        game.prepare(this);
    }

    public Level getLevel() {
        return this;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
