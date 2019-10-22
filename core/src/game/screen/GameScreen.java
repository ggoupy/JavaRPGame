package game.screen;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.CollisionListener;
import game.entity.creator.EntityCreator;
import game.GDXGame;
import game.controller.InputsController;
import game.entity.system.*;


public class GameScreen implements Screen {

    private Viewport viewport;
    private OrthographicCamera camera;
    private InputsController controller;
    private GDXGame game;
    private World world;
    private SpriteBatch spriteBatch;
    private PooledEngine engine;

    public static final float TILE_SIZE = 16f;

    private TiledMap tiledMap;
    private TextureAtlas atlas;
    private EntityCreator entityCreator;

    public GameScreen(GDXGame g)
    {
        game = g;
        controller = new InputsController();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());

        /* LOAD AND GET ASSETS */
        game.assetsManager.queueAddAssets();
        game.assetsManager.manager.finishLoading();
        atlas = game.assetsManager.getAtlas();

        /* RENDERING SYSTEM */
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false,TILE_SIZE, TILE_SIZE);
        camera.position.x = 200 / 2;
        camera.position.y = 200 / 2;
        tiledMap = game.assetsManager.manager.get(game.assetsManager.tiledMap);
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch, camera, tiledMap);

        /* ENGINE */
        engine = new PooledEngine();
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, camera));
        engine.addSystem(new CameraSystem(camera));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new EnemySystem());

        //create entities
        entityCreator = entityCreator.getInstance(world, engine, atlas);
        entityCreator.createPlayer(
                tiledMap.getLayers().get("playerPosition").getObjects().get("player"),
                game.playerSpecialization
        );
        entityCreator.createObjects(tiledMap.getLayers().get("mapObjects").getObjects());
        entityCreator.createEnemies(tiledMap.getLayers().get("enemySpawnForest").getObjects());
    }

    @Override
    //Like the constructor (called when created)
    public void show()
    {
        Gdx.input.setInputProcessor(controller);
    }

   @Override
   public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

       engine.update(delta);
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