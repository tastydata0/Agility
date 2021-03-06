package com.agility.game.UI;

import com.agility.game.Hero;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.agility.game.Game;

public class HealthBoosterChoice extends BoosterChoice {
    public HealthBoosterChoice(UI ui) {
        super(ui);
        item1 = new BoosterChoiceItem(BoosterChoiceItem.HEALTH, Game.getHero().getMaxHealth()/2, 0, ui);
        item2 = new BoosterChoiceItem(BoosterChoiceItem.MAX_HEALTH, 200, 1, ui);
    }
}
