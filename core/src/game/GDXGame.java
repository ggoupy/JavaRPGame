package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import game.loader.AssetsManager;
import game.screen.*;

public class GDXGame extends Game {

	public static final int MENU_SCREEN = 0;
	public static final int PREFERENCE_SCREEN = 1;
	public static final int SELECTION_SCREEN = 2;
	public static final int GAME_SCREEN = 3;
	public static final int MAP_SCREEN = 4;

	public AssetsManager assetsManager;

	private AppPreferences preferences;
	private MenuScreen menuScreen;
	private PreferenceScreen preferenceScreen;
	private SelectionScreen selectionScreen;
	private GameScreen gameScreen;
	private MapScreen mapScreen;

	public String playerSpecialization;
	public String playerName;

	public boolean gameLaunched = false; //to know if the user has started a new game


	@Override
	public void create()
	{
		playerSpecialization = "";
		playerName = "";

        preferences = new AppPreferences();

		assetsManager = new AssetsManager();
		assetsManager.queueAddAssets();
		assetsManager.manager.finishLoading();

		this.changeScreen(MENU_SCREEN);
	}


	public void changeScreen(int screen)
    {
        switch (screen)
        {
            case MENU_SCREEN:
                if (menuScreen == null) menuScreen = new MenuScreen(this);
                this.setScreen(menuScreen);
                break;

            case PREFERENCE_SCREEN:
                if (preferenceScreen == null) preferenceScreen = new PreferenceScreen(this);
                this.setScreen(preferenceScreen);
                break;

            case SELECTION_SCREEN:
                if (selectionScreen == null) selectionScreen = new SelectionScreen(this);
                this.setScreen(selectionScreen);
                break;

            case GAME_SCREEN:
                if (gameScreen == null) {
                    gameScreen = new GameScreen(this);
                    gameLaunched = true;
                }
                this.setScreen(gameScreen);
                break;

            case MAP_SCREEN:
                if (mapScreen == null) mapScreen = new MapScreen(this, gameScreen);
                this.setScreen(mapScreen);
                break;
        }
    }

    public AppPreferences getPreferences() {return preferences;}

	@Override
	public void dispose() {}
}