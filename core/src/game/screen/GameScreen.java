package game.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import game.CollisionListener;
import game.GDXGame;
import game.UserInterface;
import game.controller.InputsControllerGame;
import game.entity.component.PlayerComponent;
import game.entity.factory.EntityFactory;
import game.entity.system.*;
import game.entity.system.enemy.EnemyHealthSystem;
import game.entity.system.enemy.EnemyLevelSystem;
import game.entity.system.enemy.EnemyMovementSystem;
import game.entity.system.enemy.EnemySpawnSystem;
import game.entity.system.player.*;
import game.loader.AssetsManager;
import game.utils.Constants;

import static game.entity.utils.Mappers.playerMapper;


public class GameScreen implements Screen {

    private OrthographicCamera cameraUI;
    private OrthographicCamera cameraBox2D;
    private InputsControllerGame controller;
    private GDXGame game;
    private World world;
    private SpriteBatch spriteBatch;
    private PooledEngine engine;

    private TiledMap tiledMap;

    private EntityFactory entityFactory;

    private UserInterface ui;


    public GameScreen(GDXGame g) {
        game = g; //reference to GDXGame


        /* CAMERAS */
        cameraBox2D = new OrthographicCamera();
        cameraBox2D.setToOrtho(false, Constants.TILE_SIZE, Constants.TILE_SIZE);
        cameraUI = new OrthographicCamera();
        cameraUI.setToOrtho(false, Constants.G_WIDTH, Constants.G_HEIGHT);


        /* INPUTS CONTROLLER */
        controller = new InputsControllerGame(game);


        /* BOX2D WORLD */
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());


        /* RENDERING SYSTEM */
        spriteBatch = new SpriteBatch();
        tiledMap = game.assetsManager.manager.get(AssetsManager.tiledMap);
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch, cameraBox2D, cameraUI, tiledMap, game.assetsManager);


        /* ENGINE */
        engine = new PooledEngine();


        /* ENTITY FACTORY */
        entityFactory = EntityFactory.getInstance(world, engine, game.assetsManager);


        /* CREATE ENTITIES OF ENGINE */
        entityFactory.createPlayer(
                tiledMap.getLayers().get("playerPosition").getObjects().get("player"),
                game.playerSpecialization,
                game.playerName
        );
        entityFactory.createObjects(tiledMap.getLayers().get("mapObjects").getObjects());
        entityFactory.createEnemySpawns(tiledMap.getLayers());
        entityFactory.createNPCs(tiledMap.getLayers().get("npcPositions"));


        /*USER INTERFACE*/
        ui = new UserInterface(game,this);


        /* ADD SYSTEMS TO ENGINE */
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        //engine.addSystem(new PhysicsDebugSystem(world, cameraBox2D));
        engine.addSystem(new CameraSystem(cameraBox2D, cameraUI));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerMovementSystem(controller));
        engine.addSystem(new PlayerHealthSystem(game, entityFactory));
        engine.addSystem(new PlayerAttackSystem(controller));
        engine.addSystem(new PlayerXpSystem());
        engine.addSystem(new PlayerQuestSystem(ui));
        engine.addSystem(new EnemySpawnSystem(entityFactory));
        engine.addSystem(new EnemyMovementSystem(engine));
        engine.addSystem(new EnemyHealthSystem(entityFactory));
        engine.addSystem(new EnemyLevelSystem(cameraBox2D, cameraUI));
        engine.addSystem(new ReceiveAttackSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new PerspectiveSystem());
        engine.addSystem(new NpcSystem(ui, controller, getPlayerEntity()));
    }


    @Override
    public void show()
    {
        controller.setToCurrentController();
    }


    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

        ui.updateUI();
    }


    //Work when only have one player (would be need to modify later)
    public Entity getPlayerEntity() {
        return engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
    }
    public PooledEngine getEngine() {return engine;}
    public AssetsManager getAssetsManager() {return game.assetsManager;}
    public InputsControllerGame getController() {return controller;}
    public SpriteBatch getSpriteBatch() {return spriteBatch;}
    TiledMap getTiledMap() {return tiledMap;} //package private


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