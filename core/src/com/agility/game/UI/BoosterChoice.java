package com.agility.game.UI;


import com.badlogic.gdx.scenes.scene2d.Stage;

public class BoosterChoice {
    protected BoosterChoiceItem item1, item2;
    protected UI ui;
    protected int kind;

    public BoosterChoice(UI ui) {
        this.ui = ui;
    }

    public void remove() {
        ui.getActors().removeValue(item1,false);
        ui.getActors().removeValue(item2,false);
        ui.getGame().chooseEnd();
    }
}
