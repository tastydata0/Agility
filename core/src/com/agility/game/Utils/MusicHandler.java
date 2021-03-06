package com.agility.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicHandler {

    private static Music[] soundtracks = new Music[6];
    private static Music[] bossSoundtracks = new Music[2];
    private static Music nowPlaying;

    public MusicHandler() {
        for (int i = 0; i < soundtracks.length; i++) {
            soundtracks[i] = Gdx.audio.newMusic(Gdx.files.internal("music/s"+i+".mp3"));
        }
        bossSoundtracks[0] = Gdx.audio.newMusic(Gdx.files.internal("music/boss.mp3"));
        bossSoundtracks[1] = Gdx.audio.newMusic(Gdx.files.internal("music/boss2.mp3"));
    }

    public static void begin(int level) {
        if(level == 8) {
            // Boss
            nowPlaying = bossSoundtracks[0];
        }
        else {
            nowPlaying = soundtracks[level % soundtracks.length];
        }
        nowPlaying.setLooping(true);
        nowPlaying.setVolume(1);
        nowPlaying.play();
    }

    public static void refresh() {
        if (Settings.SOUND_ENABLED) {
            nowPlaying.play();
        }
        else {
            nowPlaying.pause();
        }
    }

    public static void stop() {
        nowPlaying.stop();
    }

    public void setVolume(float v) {
        nowPlaying.setVolume(v);
    }
}
