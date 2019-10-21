package game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import java.awt.*;
import game.GDXGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "The Survival Game";
		cfg.height = (int) (dimension.getHeight()*0.8); //64*15;
		cfg.width = (int) (dimension.getWidth()*0.8); //64*25
		new LwjglApplication(new GDXGame(), cfg);
	}
}
