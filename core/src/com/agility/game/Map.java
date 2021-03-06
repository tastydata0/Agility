package com.agility.game;

import java.util.Objects;

public class Map {
    private int[][] cells;

    public Map(int[][] cells) {
        this.cells = cells;
        Objects.requireNonNull(cells);
    }
    public int[][] getCells() {
        return cells;
    }
}
