package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
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
import game.entity.component.EnemyHealthComponent;
import game.entity.component.TextureComponent;
import game.entity.component.TransformComponent;
import game.utils.Constants;

import java.util.Comparator;

import static game.loader.AssetsManager.*;

public class RenderingSystem extends SortedIteratingSystem {

    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TiledMap tiledMap;
    private MapRenderer mapRenderer;
    private int [] bgLayersIndex; //index of tile map layers to render before character
    private int [] fgLayersIndex; //index of tile map layers to render after character

    private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
    private Comparator<Entity> comparator; // a comparator to sort images based on the z position of the transformComponent

    // component mappers to get components from entities
    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;

    public RenderingSystem(SpriteBatch batch, OrthographicCamera camera, TiledMap tiledMap) {

        // gets all entities with a TransformComponent and TextureComponent
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        this.batch = batch;
        this.camera = camera;
        this.tiledMap = tiledMap;
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/Constants.TILE_SIZE);

        //Get layers to render it in a specific order
        MapLayers map = tiledMap.getLayers();
        bgLayersIndex = new int[] {
                map.getIndex(tmBackground),
                map.getIndex(tmGround)
        };
        fgLayersIndex = new int[] {
                map.getIndex(tmForeground)
        };

        //create componentMappers
        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        // create array for sorting entities
        renderQueue = new Array<Entity>();
    }

    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render(bgLayersIndex);
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
            TextureComponent tex = textureM.get(entity);
            TransformComponent t = transformM.get(entity);

            if (tex.region == null || t.isHidden) {
                continue;
            }

            float width = tex.region.getRegionWidth();
            float height = tex.region.getRegionHeight();

            float originX = width/2f;
            float originY = height/2f;

            batch.draw(tex.region,
                    t.position.x - originX, t.position.y - originY,
                    originX, originY,
                    width, height,
                    PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y),
                    t.rotation);
        }
        batch.end();
        renderQueue.clear();

        mapRenderer.render(fgLayersIndex);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    // convenience method to convert pixels to meters
    public static float PixelsToMeters(float pixelValue){
        return pixelValue * Constants.PIXELS_TO_METRES;
    }
}
