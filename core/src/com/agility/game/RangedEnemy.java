package com.agility.game;

import com.agility.game.Utils.EnemyDef;
import com.agility.game.WorldObjects.Bullet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class RangedEnemy extends Enemy {
    public RangedEnemy(EnemyDef def, World world, Vector2 position, Game game) {

        super(def, world, position, game);
    }

    @Override
    protected void attack() {
        super.attack();


        throw new NullPointerException("FUCK YEAH");
    }
}
