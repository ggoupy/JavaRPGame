package game.screen;

import com.badlogic.gdx.Screen;
import game.GDXGame;

public class LoadingScreen implements Screen {

    private GDXGame game;

    public LoadingScreen(GDXGame g)
    {
        game = g;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.changeScreen(GDXGame.MENU);
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
