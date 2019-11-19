package com.game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.utils.Constants;
import com.game.entity.component.FontComponent;
import com.game.entity.component.TextureComponent;
import com.game.entity.component.TransformComponent;
import com.game.entity.utils.ZComparator;
import com.game.loader.AssetsManager;

import static com.game.entity.utils.Mappers.*;
import static com.game.loader.AssetsManager.*;

public class RenderingSystem extends SortedIteratingSystem {

    private Viewport gameViewport;
    private Viewport UIViewport;
    private OrthographicCamera cameraUI;
    private OrthographicCamera cameraBox2D;
    private SpriteBatch batch;
    private MapRenderer mapRenderer;
    private AssetsManager assetsManager;
    private int[] bgLayersIndex; //index of tile map layers to render at the beginning
    private int[] fgLayersIndex; //index of tile map layers to render at the end

    private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
    private ZComparator perspectiveComparator; // a comparator to sort images based on the z position of the transformComponent


    public RenderingSystem(SpriteBatch batch, Viewport gameViewport, Viewport UIViewport, TiledMap tiledMap, AssetsManager am) {
        // gets all entities with a TransformComponent and TextureComponent
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        this.UIViewport = UIViewport;
        this.gameViewport = gameViewport;
        this.cameraUI = (OrthographicCamera) UIViewport.getCamera();
        this.cameraBox2D = (OrthographicCamera) gameViewport.getCamera();

        this.perspectiveComparator = new ZComparator();

        this.batch = batch;

        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / Constants.TILE_SIZE);

        this.assetsManager = am;

        //Get layers to render it in a specific order
        MapLayers map = tiledMap.getLayers();
        this.bgLayersIndex = new int[]{
                map.getIndex(tmBackground),
                map.getIndex(tmGround)
        };
        this.fgLayersIndex = new int[]{
                map.getIndex(tmForeground)
        };

        // create array for sorting entities
        this.renderQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime);

        //sort entities according to Z axes in order to display one before other
        renderQueue.sort(perspectiveComparator);

        gameViewport.apply();

        mapRenderer.setView(cameraBox2D);
        mapRenderer.render(bgLayersIndex);

        batch.setProjectionMatrix(cameraBox2D.combined);
        batch.enableBlending();
        batch.begin();

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
            TextureComponent tex = textureMapper.get(entity);
            TransformComponent t = transformMapper.get(entity);
            FontComponent fontCom = fontMapper.get(entity);

            if (tex.region == null || t.isHidden) continue;


            float width = tex.region.getRegionWidth();
            float height = tex.region.getRegionHeight();

            float originX = width / 2f;
            float originY = height / 2f;

            batch.draw(tex.region,
                    t.position.x - originX, t.position.y - originY,
                    originX, originY,
                    width, height,
                    Constants.PixelsToMeters(t.scale.x), Constants.PixelsToMeters(t.scale.y),
                    t.rotation);

            if (fontCom != null) //entity has a font component
            {
                UIViewport.apply();
                batch.setProjectionMatrix(cameraUI.combined); //we change the camera to deal with pixels

                assetsManager.getFont().draw(batch, fontCom.text, fontCom.screenPos.x, fontCom.screenPos.y);

                batch.setProjectionMatrix(cameraBox2D.combined); //we set the world camera
                gameViewport.apply();
            }
        }

        batch.end();
        renderQueue.clear();

        mapRenderer.render(fgLayersIndex);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }
}
