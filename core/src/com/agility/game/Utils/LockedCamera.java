package com.agility.game.Utils;

import com.agility.game.Hero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class LockedCamera extends OrthographicCamera {
    private Hero hero;
    private boolean movedToHero = false;
    private boolean canMoveToHero = true;
    private Vector2 unlockedPosition = new Vector2(0,0);
    public LockedCamera(float viewportWidth, float viewportHeight, Hero hero) {
        super(viewportWidth, viewportHeight);
        this.hero = hero;
    }
// 95 150
    @Override
    public void update() {
        super.update();

        if(hero != null && hero.getBody() != null && canMoveToHero) {
            if(!movedToHero) {
                position.x = Hero.getPosition().x;
                position.y = Hero.getPosition().y;
                movedToHero = true;
            }
            float dx = position.x - Hero.getPosition().x;
            float dy = position.y - Hero.getPosition().y;
            position.x += dx*-0.02f;
            position.y += dy*-0.03f;
            unlockedPosition.x = Hero.getPosition().x;
            unlockedPosition.y = Hero.getPosition().y;
        }
        else if(hero != null && hero.getBody() != null && !canMoveToHero) {
            float dx = position.x - unlockedPosition.x;
            float dy = position.y - unlockedPosition.y;
            position.x += dx*-0.01f;
            position.y += dy*-0.015f;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            unlockedPosition.y+=0.5f;
            canMoveToHero = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            unlockedPosition.y-=0.5f;
            canMoveToHero = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            unlockedPosition.x+=0.5f;
            canMoveToHero = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            unlockedPosition.x-=0.5f;
            canMoveToHero = false;
        }
    }
    public void shake(int direction) {
        position.x += 1*direction;
    }
}
