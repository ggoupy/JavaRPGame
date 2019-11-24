package com.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.game.GDXGame;
import com.game.utils.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.forceExit = false; //avoid an error with java
		cfg.resizable = false; //not able to manage different sizing for the moment
		cfg.fullscreen = true;
		cfg.title = "The RPG Game";
		cfg.height = Constants.G_HEIGHT;
		cfg.width = Constants.G_WIDTH;
		new LwjglApplication(new GDXGame(), cfg);
	}
}
