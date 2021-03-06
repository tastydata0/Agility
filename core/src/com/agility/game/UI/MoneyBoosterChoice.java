package com.agility.game.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class MoneyBoosterChoice extends BoosterChoice {
    public MoneyBoosterChoice(UI ui) {
        super(ui);
        item1 = new BoosterChoiceItem(BoosterChoiceItem.COINS, 500, 0, ui);
        item2 = new BoosterChoiceItem(BoosterChoiceItem.DIAMONDS, 10, 1, ui);
    }
}
