package com.agility.game.Utils;

import com.agility.game.Hint;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class HintsHandler {

    ArrayList<Hint> hints = new ArrayList<Hint>();

    public HintsHandler(ArrayList<Hint> hints) {
        this.hints = hints;
    }

    public void update() {
        for (Hint h:hints) {
            h.update();
        }
    }

}
