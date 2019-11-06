package game.screen;

import com.badlogic.gdx.Screen;
import game.GDXGame;

public class LoadingScreen implements Screen {

    private GDXGame game;
    //would load things there in the future
    //add an animation before menu screen

    public LoadingScreen(GDXGame g)
    {
        game = g;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.changeScreen(GDXGame.MENU_SCREEN);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
