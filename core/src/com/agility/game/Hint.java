package com.agility.game;

import com.badlogic.gdx.math.Vector2;

public class Hint {
    public static final String[] hints = {
            //"Что бы побежать вправо, свайпните вправо",
            "Для прыжка нажмите в любом месте",
            "Чтобы перейти на следующий уровень, идите в портал",
            "Чтобы побежать влево, свайпните влево",
            "Подойдите к книге и нажмите кнопку в правой части,\nчто бы выбрать бонус",
            "Два нажатия дают двойной прыжок",
            "Чтобы сделать перекат, свайпните вниз.\nЭто поможет избежать вражеской атаки",
            "Вы можете прыгать от стен бесконечно",
            "Свайпните вверх для использования заклинания.\n Оно расходует всю ману, которая постепенно накапливается",
            "Рядом с противником, нажимая на экран,\nВы будете его атаковать вместо прыжка",
    };
    private int positionInQueue;
    private Vector2 position;
    private boolean passed;

    public Hint(Vector2 position, int positionInQueue) {
        this.positionInQueue = positionInQueue;
        this.position = position;
    }

    public void update() {
        if(Math.abs(Hero.getPosition().x - position.x) <= 8 &&
                Math.abs(Hero.getPosition().y - position.y) <= 20 && !passed) {
            activate();
        }
    }

    private void activate() {
        passed = true;
        if(positionInQueue == 7) {
            Game.getHero().setMana(1);
        }
        Game.getUi().hint(hints[positionInQueue]);
    }
}
