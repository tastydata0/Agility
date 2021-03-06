package com.agility.game.Utils;

import com.agility.game.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class MapParser {
    private static final MapParser ourInstance = new MapParser();

    public static MapParser getInstance() {
        return ourInstance;
    }

    private MapParser() {
    }

    public Map parse(String filename) {
        int[][] cells = null;
        try {
            FileHandle handle = Gdx.files.internal("maps/"+filename);
            String map = handle.readString();

            String[] strings = map.split("\n");
            cells = new int[strings[0].split(",").length][strings.length];
            for (int i = 0; i < strings.length; i++) {
                String[] ids = strings[i].split(",");
                for (int j = 0; j < strings[0].split(",").length; j++) {
                    cells[j][strings.length-1-i] = Integer.parseInt(ids[j].trim());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new Map(cells);
    }
}
