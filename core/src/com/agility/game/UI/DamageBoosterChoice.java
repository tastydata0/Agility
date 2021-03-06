package com.agility.game.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class DamageBoosterChoice extends BoosterChoice {
    public DamageBoosterChoice(UI ui) {
        super(ui);
        item1 = new BoosterChoiceItem(BoosterChoiceItem.DAMAGE, 10, 0, ui);
        item2 = new BoosterChoiceItem(BoosterChoiceItem.CRITICAL_STRIKE, 1, 1, ui);
    }
}
