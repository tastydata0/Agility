package com.agility.game.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class AnimationWithOffset {
    public Animation<Sprite> animation;
    public float xOffset, yOffset, defaultXOffset;

    public AnimationWithOffset(Animation<Sprite> animation, float xOffset, float yOffset,float defaultXOffset) {
        this.animation = animation;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.defaultXOffset = defaultXOffset;
    }
}
