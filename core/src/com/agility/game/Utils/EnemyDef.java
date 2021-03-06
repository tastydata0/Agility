package com.agility.game.Utils;

import java.util.HashMap;

public class EnemyDef {


    public EnemyDef() {

    }

    public boolean ranged, boss;
    public int cooldownInStart, cooldown;
    public float maxHealth, visibilityY = 5, visibilityX = 50, attackRange, runVelocity, damageDealt, stateTimeSlash;
    public HashMap<String,AnimationWithOffset> animations;

}
