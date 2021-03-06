package com.agility.game.Utils;

import com.badlogic.gdx.audio.Sound;

public class SoundPlayer {

    public static void play(Sound sound) {
        if(Settings.SOUND_ENABLED) {
            sound.play(1f);
        }
    }
}
