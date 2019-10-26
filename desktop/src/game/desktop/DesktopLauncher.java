package game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import java.awt.*;
import game.GDXGame;

import game.utils.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.forceExit = false; //avoid an error with java
		cfg.title = "The Survival RPG Game";
		cfg.height = Constants.G_HEIGHT;
		cfg.width = Constants.G_WIDTH;
		new LwjglApplication(new GDXGame(), cfg);
	}
}
