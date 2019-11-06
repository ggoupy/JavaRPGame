package game.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import game.CollisionListener;
import game.entity.component.PlayerComponent;
import game.entity.factory.EntityFactory;
import game.GDXGame;
import game.controller.InputsController;
import game.entity.system.*;
import game.entity.system.enemy.*;
import game.entity.system.player.*;
import game.loader.AssetsManager;
import game.utils.Constants;

import static game.utils.Constants.TILE_SIZE;


public class GameScreen implements Screen {

    private OrthographicCamera cameraUI;
    private OrthographicCamera cameraBox2D;
    private InputsController controller;
    private GDXGame game;
    private World world;
    private SpriteBatch spriteBatch;
    private PooledEngine engine;

    private TiledMap tiledMap;

    private EntityFactory entityFactory;

    private Entity player;

    public GameScreen(GDXGame g)
    {
        game = g;
        controller = new InputsController();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());

        /* CAMERAS */
        cameraBox2D = new OrthographicCamera();
        cameraBox2D.setToOrtho(false, TILE_SIZE, TILE_SIZE);
        cameraUI = new OrthographicCamera();
        cameraUI.setToOrtho(false, Constants.G_WIDTH, Constants.G_HEIGHT);

        /* RENDERING SYSTEM */
        spriteBatch = new SpriteBatch();
        tiledMap = game.assetsManager.manager.get(AssetsManager.tiledMap);
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch, cameraBox2D, cameraUI, tiledMap, g.assetsManager);

        /* ENGINE */
        engine = new PooledEngine();

        /* ENTITY FACTORY */
        entityFactory = EntityFactory.getInstance(world, engine, game.assetsManager);

        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        //engine.addSystem(new PhysicsDebugSystem(world, cameraBox2D));
        engine.addSystem(new CameraSystem(cameraBox2D, cameraUI));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerMovementSystem(controller));
        engine.addSystem(new PlayerHealthSystem(g, entityFactory));
        engine.addSystem(new PlayerAttackSystem(controller));
        engine.addSystem(new PlayerXpSystem());
        engine.addSystem(new EnemySpawnSystem(entityFactory));
        engine.addSystem(new EnemyMovementSystem(engine));
        engine.addSystem(new EnemyHealthSystem(entityFactory));
        engine.addSystem(new EnemyLevelSystem(cameraBox2D, cameraUI));
        engine.addSystem(new ReceiveAttackSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new PerspectiveSystem());

        //create entities
        player = entityFactory.createPlayer(
                    tiledMap.getLayers().get("playerPosition").getObjects().get("player"),
                    game.playerSpecialization,
                    game.playerName
        );
        entityFactory.createObjects(tiledMap.getLayers().get("mapObjects").getObjects());

        entityFactory.createEnemySpawns(tiledMap.getLayers());

        PlayerHUDSystem HUD = new PlayerHUDSystem(spriteBatch, game.assetsManager, player.getComponent(PlayerComponent.class));
        engine.addSystem(HUD);
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
        if (controller.map_key)
        {
            game.changeScreen(GDXGame.MAP_SCREEN);
        }
    }

    //package private
    InputsController getController() {return controller;}
    TiledMap getTiledMap() {return tiledMap;}
    Entity getPlayerEntity() {return player;}



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