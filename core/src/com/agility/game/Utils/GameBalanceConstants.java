package com.agility.game.Utils;

public class GameBalanceConstants {

    public static final int DEFAULT_HERO_MAX_HEALTH = 1000;
    public static final int WIZARD_SPELL_DAMAGE = 125;
    public static final double DAMAGE_MULTIPLIER = 1.1;
    public static final double HEALTH_MULTIPLIER = 1.15;
    public static final float REQUIRED_KILLS = 0.85f;
    public static final double WEAPON_MULTIPLIER = 1.15;
    public static final float ENEMY_DAMAGE_MULTIPLIER_ABSOLUTE = 0.3f;
    public static final String RUSSIAN_CHARACTERS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
            + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
            + "1234567890.,:;_¡!¿?\"'+-*/()[]={}";

    @Deprecated
    private GameBalanceConstants() {
        // Do not use
    }

    public static final float EQUIPMENT_DROP_CHANCE = .15f;
}
