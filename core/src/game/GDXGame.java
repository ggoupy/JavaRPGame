package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import game.loader.AssetsManager;
import game.screen.*;

public class GDXGame extends Game {

	public static final int MENU_SCREEN = 0;
	public static final int SELECTION_SCREEN = 1;
	public static final int GAME_SCREEN = 2;
	public static final int END_SCREEN = 3;
	public static final int MAP_SCREEN = 4;

	public AssetsManager assetsManager;

	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;
	private SelectionScreen selectionScreen;
	private GameScreen gameScreen;
	private MapScreen mapScreen;
	private EndScreen endScreen;

	public String playerSpecialization;
	public String playerName;

	private boolean resetGame; //to start a new game after game over


	@Override
	public void create()
	{
		playerSpecialization = "";
		playerName = "";
		resetGame = false;

		assetsManager = new AssetsManager();
		assetsManager.queueAddAssets();
		assetsManager.manager.finishLoading();

		loadingScreen = new LoadingScreen(this);
		this.setScreen(loadingScreen);
	}


	public void changeScreen(int screen) {

		switch (screen) {
			case MENU_SCREEN:
				if (menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;

			case SELECTION_SCREEN:
				if (selectionScreen == null) selectionScreen = new SelectionScreen(this);
				this.setScreen(selectionScreen);
				break;

			case GAME_SCREEN:
				if (gameScreen == null) {gameScreen = new GameScreen(this);}
				if (resetGame) {gameScreen.resetGame(); resetGame = false;}
				this.setScreen(gameScreen);
				break;

			case MAP_SCREEN:
				if (mapScreen == null) mapScreen = new MapScreen(this, gameScreen);
				this.setScreen(mapScreen);
				break;

			case END_SCREEN:
				if (endScreen == null) endScreen = new EndScreen(this);
				resetGame();
				this.setScreen(endScreen);
				break;
		}
	}

	private void resetGame()
	{
		playerSpecialization = ""; //reset player
		playerName = ""; //reset player
		resetGame = true; //able to create new game
	}


	@Override
	public void dispose() {}
}