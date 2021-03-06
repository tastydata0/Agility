package com.agility.game.Utils;

import com.agility.game.Enemy;
import com.agility.game.Game;
import com.agility.game.RangedEnemy;
import com.agility.game.WorldObjects.Bullet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.ArrayList;
import java.util.Iterator;

public class ContactHandler implements ContactListener {
    private Game game;



    public ContactHandler(Game game) {
        this.game = game;
    }

    @Override
    public void beginContact(Contact contact) {
        String userDataA = contact.getFixtureA().getBody().getUserData().toString();
        String userDataB = contact.getFixtureB().getBody().getUserData().toString();

        if((userDataA.equals("wizardsBullet") && userDataB.equals("player") || userDataA.equals("player") && userDataB.equals("wizardsBullet"))){
            Game.getHero().damage(GameBalanceConstants.WIZARD_SPELL_DAMAGE);
        }

        if(userDataA.equals("wizardsBullet")){
            for (Bullet bullet:game.getBullets()) {
                if(bullet.getBody().equals(contact.getFixtureA().getBody())) {
                    bullet.destroy();
                }
            }
        }
        else if(userDataB.equals("wizardsBullet")) {
            for (Bullet bullet:game.getBullets()) {
                if(bullet.getBody().equals(contact.getFixtureB().getBody())) {
                    bullet.destroy();
                }
            }
        }

        if(userDataA.equals("player") && userDataB.equals("block") || userDataB.equals("player") && userDataA.equals("block")){
            game.getHero().touchBlock(contact);
        }
        else if(userDataA.equals("player") && userDataB.equals("startWeapon") || userDataB.equals("player") && userDataA.equals("startWeapon")){
            game.getStartWeapon().removeSword();
            game.getHero().grabSword(Game.startWeapon);
        }
        else if(userDataA.equals("enemy") && userDataB.equals("weaponSwipe") || userDataB.equals("enemy") && userDataA.equals("weaponSwipe")){
            ArrayList<Enemy> enemies = game.getEnemies();
            if(userDataA.equals("enemy")) {
                for (Enemy enemy:enemies) {
                    if(contact.getFixtureA().getBody().equals(enemy.getBody())) {
                        game.getHero().hitEnemy(enemy);

                        break;
                    }
                }
            }
            else {
                for (Enemy enemy:enemies) {
                    if(contact.getFixtureB().getBody().equals(enemy.getBody())) {
                        game.getHero().hitEnemy(enemy);

                        break;
                    }

                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        String userDataA = contact.getFixtureA().getBody().getUserData().toString();
        String userDataB = contact.getFixtureB().getBody().getUserData().toString();

        if(userDataA.equals("player") && userDataB.equals("block") || userDataB.equals("player") && userDataA.equals("block")){
            game.getHero().releaseTouch(contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
