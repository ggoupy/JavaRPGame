package game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import game.entity.component.AnimationComponent;
import game.entity.component.AttachedComponent;
import game.entity.component.BodyComponent;
import game.entity.component.TypeComponent;
import game.loader.AssetsManager;


public class EntityFactory {

    //Friendly attributes
    World world;
    PooledEngine engine;
    TextureAtlas atlas;
    AssetsManager assetsManager;
    BodyFactory bodyFactory;

    private EnemyFactory enemyFactory;
    private PlayerFactory playerFactory;

    //the EntityCreator class will be created only one time
    //So we will store the instance in the class
    //static -> single copy of variable is created and shared among all objects at class level (=efficient memory)
    private static EntityFactory thisInstance;


    //private constructor because it's singleton class
    private EntityFactory (World world, PooledEngine engine, AssetsManager assetsManager)
    {
        this.world = world;
        this.engine = engine;
        this.atlas = assetsManager.getAtlas();
        this.assetsManager = assetsManager;
        bodyFactory = bodyFactory.getInstance(world);
        enemyFactory = enemyFactory.getInstance(this);
        playerFactory = playerFactory.getInstance(this);
    }

    //get the body factory instance and create it if not instanced
    //used to create the instance (like a constructor)
    public static EntityFactory getInstance(World world, PooledEngine engine, AssetsManager assetsManager)
    {
        if (thisInstance == null) thisInstance = new EntityFactory(world, engine, assetsManager);
        return thisInstance;
    }


    //convert mapObjects of tiled map into box2D objects in the world
    public void createObjects(MapObjects objects)
    {
        for (MapObject object: objects)
        {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            Entity entity = engine.createEntity();
            BodyComponent body = engine.createComponent(BodyComponent.class);
            TypeComponent type = engine.createComponent(TypeComponent.class);

            body.body = bodyFactory.makeBox(rectangle, BodyDef.BodyType.StaticBody, BodyFactory.STONE);
            type.type = TypeComponent.SCENERY;

            //store a reference to the entity in the box2d box
            body.body.setUserData(entity);

            entity.add(body);
            entity.add(type);
            engine.addEntity(entity);
        }
    }


    //create a player box2D in the world according to his position in the tiled map
    public Entity createPlayer(MapObject playerObj, String spec, String name)
    {
        return playerFactory.createPlayer(playerObj,spec,name);
    }


    //create all enemy spawns from the tile map
    public void createEnemySpawns(MapLayers layers)
    {
        //for each layer, check if its name contains "enemySpawn"
        //if yes, create a new spawn
        for(MapLayer layer : layers)
        {
            if (layer.getName().contains("enemySpawn"))
            {
                createEnemySpawn(layer);
            }
        }
    }

    //create an enemy spawn in the world
    public void createEnemySpawn(MapLayer spawnLayer)
    {
        enemyFactory.createEnemySpawn(spawnLayer);
    }


    //create enemy box2D from a spawn entity and the index of the spawn
    public void createEnemy(Entity spawnEntity, int index)
    {
        enemyFactory.createEnemy(spawnEntity, index);
    }


    //create an animation with parameters given to a animation component
    public void createAnimation(AnimationComponent a, int state, float frameDuration, String atlasRegions, Animation.PlayMode playmode)
    {
        a.animations.put(state, new Animation(frameDuration, atlas.findRegions(atlasRegions), playmode));
    }


    //Destroy an entity and all components attached to it
    public void destroyEntity(Entity entity)
    {
        destroyEntity(entity, true);
    }

    public void destroyEntity(Entity entity, boolean removeAttached)
    {
        if (removeAttached) //if we chose to remove the attached entity too
        {
            AttachedComponent attached = entity.getComponent(AttachedComponent.class);
            if (attached != null) //if the entity contains an attached entity
            {
                //remove the box2D of the attached entity in the world
                BodyComponent body = attached.attachedEntity.getComponent(BodyComponent.class);
                if (body != null) //if the entity has a box2D in the game world
                {
                    world.destroyBody(body.body);
                }

                attached.attachedEntity.removeAll(); //remove all components of the attached entity
                engine.removeEntity(attached.attachedEntity); //remove the attached entity from the engine
            }
        }
        else
        {
            //if we want to keep the attached entity
            //we need to set to 'null' his attached component, which is the current entity
            Entity attachedEntity = entity.getComponent(AttachedComponent.class).attachedEntity;
            if (attachedEntity != null) //if the entity contains an attached entity
            {
                AttachedComponent attached = attachedEntity.getComponent(AttachedComponent.class);
                if (attached != null) //if the attached entity has an attached entity (lol)
                {
                    attached.attachedEntity = null; //we set it to null (the entity that will be remove)
                }
            }
        }

        BodyComponent body = entity.getComponent(BodyComponent.class);
        if (body != null) //if the entity has a box2D in the game world
        {
            world.destroyBody(entity.getComponent(BodyComponent.class).body); //remove the B2Body from the world
        }

        entity.removeAll(); //remove all components of the entity
        engine.removeEntity(entity); //remove the entity from the engine
        engine.clearPools(); //remove unused entity from the pool
    }
}
