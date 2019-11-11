package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import game.GDXGame;
import game.controller.InputsControllerUI;
import game.entity.component.TransformComponent;
import game.utils.Constants;


public class MapScreen implements Screen {

    private GDXGame game;
    private GameScreen gameScreen;
    private InputsControllerUI controller;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private float worldWidth;
    private float worldHeight;
    private OrthogonalTiledMapRenderer map;
    private TextureRegion playerPoint;
    private TransformComponent playerPos;
    private Vector2 cameraMovement;


    public MapScreen(GDXGame game, GameScreen gameScreen)
    {
        this.game = game;
        this.gameScreen = gameScreen;

        /* INPUTS CONTROLLER */
        controller = new InputsControllerUI(game);

        //Game tiled map
        TiledMap tiledMap = gameScreen.getTiledMap();
        worldWidth = tiledMap.getProperties().get("width", float.class);
        worldHeight = tiledMap.getProperties().get("height", float.class);

        //Map camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight);

        //Tile map renderer
        map = new OrthogonalTiledMapRenderer(tiledMap, 1/Constants.TILE_SIZE);

        //Sprite batch to draw player on map
        batch = new SpriteBatch();

        //save camera movement and apply it to camera position if not outside of map
        cameraMovement = new Vector2();
    }


    //called every time we set screen to this one
    //so we need to (re) init some variables
    @Override
    public void show()
    {
        //Set UI controller
        controller.setToCurrentController();

        //Set camera options
        camera.position.x = worldWidth/2;
        camera.position.y = worldHeight/2;
        camera.zoom = 1;

        //Texture of the player point
        playerPoint = game.assetsManager.getAtlas().findRegion(game.playerSpecialization+"-standingDown");

        //Position of the player
        playerPos = gameScreen.getPlayerEntity().getComponent(TransformComponent.class);
    }

    private void inputsController()
    {
        //Quit
        if (controller.exitMap)
        {
            controller.resetKeys(); //we force keys to be false (keyUp) to avoid problems in next screen
            game.changeScreen(GDXGame.GAME_SCREEN);
            return;
        }

        //camera.zoom = 1 by default
        //to zoom: camera.zoom-- / to de-zoom: camera.zoom++
        if (controller.e) //Zoom
        {
            if (camera.zoom > 0.1) camera.zoom-=0.01;
        }
        if (controller.r) //De-zoom
        {
            if (camera.zoom < 1) camera.zoom+=0.01;
        }

        //Move
        float cameraSpeed = camera.zoom * 2; //movement speed according to camera zoom
        if (controller.up) cameraMovement.y=camera.position.y+cameraSpeed;
        if (controller.down) cameraMovement.y=camera.position.y-cameraSpeed;
        if (controller.right) cameraMovement.x=camera.position.x+cameraSpeed;
        if (controller.left) cameraMovement.x=camera.position.x-cameraSpeed;

        //set camera position according to world bounds (in order to do not move out of map)
        float minCameraX = camera.zoom * (camera.viewportWidth / 2);
        float maxCameraX = worldWidth - minCameraX;
        float minCameraY = camera.zoom * (camera.viewportHeight / 2);
        float maxCameraY = worldHeight - minCameraY;
        camera.position.set(
                Math.min(maxCameraX, Math.max(cameraMovement.x, minCameraX)),
                Math.min(maxCameraY, Math.max(cameraMovement.y, minCameraY)),
                0
        );
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        inputsController();

        camera.update();

        map.setView(camera);
        map.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //textureCoeff to increase player texture in map according to camera zoom
        float textureCoeff = 6f * camera.zoom;
        textureCoeff = (textureCoeff<1) ? 1 : textureCoeff; //assert that > 1
        float width = playerPoint.getRegionWidth()*(textureCoeff);
        float height = playerPoint.getRegionHeight()*(textureCoeff);
        float originX = width/2f;
        float originY = height/2f;

        batch.draw(playerPoint,
                playerPos.position.x - originX, playerPos.position.y - originY,
                originX, originY,
                width, height,
                PixelsToMeters(playerPos.scale.x), PixelsToMeters(playerPos.scale.y),
                playerPos.rotation
        );

        batch.end();
    }

    // convenience method to convert pixels to meters
    public static float PixelsToMeters(float pixelValue){
        return pixelValue * Constants.PIXELS_TO_METRES;
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
