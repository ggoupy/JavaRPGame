package game;

import com.badlogic.gdx.Game;
import game.loader.AssetsManager;
import game.screen.GameScreen;
import game.screen.LoadingScreen;
import game.screen.MenuScreen;
import game.screen.SelectionScreen;

public class GDXGame extends Game {

	public static final int MENU = 0;
	public static final int SELECTION = 1;
	public static final int APPLICATION = 2;

	public AssetsManager assetsManager;

	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;
	private SelectionScreen selectionScreen;
	private GameScreen gameScreen;

	public String playerSpecialization;

	@Override
	public void create ()
	{
		playerSpecialization = "";

		assetsManager = new AssetsManager();
		assetsManager.queueAddAssets();
		assetsManager.manager.finishLoading();

		loadingScreen = new LoadingScreen(this);
		this.setScreen(loadingScreen);
	}
	
	@Override
	public void dispose () {}


	public void changeScreen(int screen) {

		switch (screen) {
			case MENU:
				if (menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;

			case SELECTION:
				if (selectionScreen == null) selectionScreen = new SelectionScreen(this);
				this.setScreen(selectionScreen);
				break;

			case APPLICATION:
				if (gameScreen == null) gameScreen = new GameScreen(this);
				this.setScreen(gameScreen);
				break;
		}
	}
}