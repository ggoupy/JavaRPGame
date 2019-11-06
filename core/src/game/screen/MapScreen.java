package game.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import game.GDXGame;
import game.controller.InputsController;
import game.entity.component.PlayerComponent;
import game.utils.Constants;


public class MapScreen implements Screen {

    private GDXGame game;
    private GameScreen gameScreen;
    private InputsController controller;

    private OrthographicCamera camera;
    private FillViewport viewport;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer map;


    public MapScreen(GDXGame game, GameScreen gameScreen)
    {
        this.game = game;
        this.gameScreen = gameScreen;
        this.controller = gameScreen.getController();

        TiledMap tiledMap = gameScreen.getTiledMap();
        float worldWidth = tiledMap.getProperties().get("width", float.class);
        float worldHeight = tiledMap.getProperties().get("height", float.class);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight);

        batch = new SpriteBatch();

        map = new OrthogonalTiledMapRenderer(tiledMap, 1/Constants.TILE_SIZE);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (controller.exitMap_key) game.changeScreen(GDXGame.GAME_SCREEN);

        camera.update();

        map.setView(camera);
        map.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        //batch.draw();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
