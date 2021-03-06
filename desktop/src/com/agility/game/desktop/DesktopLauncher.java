package com.agility.game.desktop;

import com.agility.game.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.agility.game.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1440;//LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = 720;//LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.fullscreen = true;
		new LwjglApplication(new Game(), config);
	}
}
