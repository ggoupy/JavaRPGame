package game.entity.creator;

import java.util.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import game.entity.component.*;
import game.entity.component.PlayerComponent;
import game.utils.Bar;


public class EntityCreator {

    //References to game attributes
    private World world;
    private PooledEngine engine;
    private TextureAtlas atlas;

    private BodyFactory bodyFactory;

    //the EntityCreator class will be created only one time
    //So we will store the instance in the class
    //static -> single copy of variable is created and shared among all objects at class level (=efficient memory)
    private static EntityCreator thisInstance;


    //private constructor because it's singleton class
    private EntityCreator (World world, PooledEngine engine, TextureAtlas atlas)
    {
        this.world = world;
        this.engine = engine;
        this.atlas = atlas;
        bodyFactory = bodyFactory.getInstance(world);
    }

    //get the body factory instance and create it if not instanced
    //used to create the instance (like a constructor)
    public static EntityCreator getInstance(World world, PooledEngine engine, TextureAtlas atlas)
    {
        if (thisInstance == null) thisInstance = new EntityCreator(world, engine, atlas);
        return thisInstance;
    }


    //create a player box2D in the world according to his position in the tiled map
    public void createPlayer(MapObject playerObj, String spec)
    {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        createPlayerDefinition(player, spec);
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);

        //get the rectangle of the mapObject (to have the coordinates)
        Rectangle rectangle = ((RectangleMapObject)playerObj).getRectangle();

        //convert rectangle coordinates into rectangle center coordinates in the world
        Vector2 center = BodyFactory.getTransformedCenterForRectangle(rectangle);

        //create a box in the world with coordinates and specific attributes
        body.body = bodyFactory.makeCircleBox(center.x, center.y,1, BodyDef.BodyType.DynamicBody, BodyFactory.HUMAN);

        position.position.set(center.x,center.y,0);

        texture.region = atlas.findRegion(player.spec+"-standingDown");

        type.type = TypeComponent.PLAYER;

        state.set(StateComponent.STANDING_DOWN);

        //we add animations of the player in function of the state component
        //PlayMode.LOOP repeats animation after all frames shown
        createAnimation(animation,StateComponent.MOVING,0.08f, player.spec+"-walkingRight", PlayMode.LOOP);
        createAnimation(animation,StateComponent.MOVING_UP,0.08f, player.spec+"-walkingUp", PlayMode.LOOP);
        createAnimation(animation,StateComponent.MOVING_DOWN,0.08f, player.spec+"-walkingDown", PlayMode.LOOP);
        createAnimation(animation,StateComponent.STANDING,1f, player.spec+"-standingRight", PlayMode.NORMAL);
        createAnimation(animation,StateComponent.STANDING_UP,1f,player.spec+"-standingUp", PlayMode.NORMAL);
        createAnimation(animation,StateComponent.STANDING_DOWN,1f, player.spec+"-standingDown", PlayMode.NORMAL);

        //store a reference to the entity in the box2d box
        body.body.setUserData(entity);

        // add the components to the entity
        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(animation);
        entity.add(player);
        entity.add(collision);
        entity.add(type);
        entity.add(state);

        // add the entity to the engine
        engine.addEntity(entity);
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

    //create enemies box2D from spawn object in tiled map
    public void createEnemies(MapObjects spawns)
    {
        //get a random number between nb of spawn in a the spawn layer object and nb of spawn / 3
        //To define the number of enemies that will spawn in the layer area
        int nbSpawns = spawns.getCount();
        Random rand = new Random();
        int toSpawnLeft = rand.nextInt(nbSpawns - nbSpawns/3) + nbSpawns/3;

        //get a list[toSpawnLeft] of uniques index between 0 and nb of spawns
        Set<Integer> uniqIndex_arr = new HashSet<>(); //HashSet is fast to use contains() method
       // toSpawnLeft = 1; //PROVISOIRE
        while (toSpawnLeft > 0)
        {
            int index = rand.nextInt(nbSpawns);
            if (!uniqIndex_arr.contains(index))
            {
                uniqIndex_arr.add(index);
                toSpawnLeft--;
            }
        }

        //Iterates all the index array and create an enemy at the spawn i
        for (Integer i: uniqIndex_arr)
        {
            Rectangle rectangle = ((RectangleMapObject) spawns.get(i)).getRectangle();

            Entity entity = engine.createEntity();
            EnemyComponent enemy = engine.createComponent(EnemyComponent.class);
            BodyComponent body = engine.createComponent(BodyComponent.class);
            TypeComponent type = engine.createComponent(TypeComponent.class);
            CollisionComponent collision = engine.createComponent(CollisionComponent.class);
            StateComponent state = engine.createComponent(StateComponent.class);
            AnimationComponent animation = engine.createComponent(AnimationComponent.class);
            TextureComponent texture = engine.createComponent(TextureComponent.class);
            TransformComponent position = engine.createComponent(TransformComponent.class);

            type.type = TypeComponent.ENEMY;

            //convert rectangle coordinates into rectangle center coordinates in the world
            Vector2 center = BodyFactory.getTransformedCenterForRectangle(rectangle);

            body.body = bodyFactory.makeBox(rectangle, BodyDef.BodyType.DynamicBody, BodyFactory.STONE);

            position.position.set(center.x,center.y,0);

            enemy.origin = new Vector2(center.x, center.y);
            enemy.movingTime = 2 + Math.random() * 2; //random between 2 and 4
            enemy.standingTime = 2 + Math.random() * 2; //random between 2 and 4

            texture.region = atlas.findRegion("skeleton-standingDown");

            state.set(StateComponent.STANDING_DOWN);

            //we add animations of the player in function of the state component
            //PlayMode.LOOP repeats animation after all frames shown
            createAnimation(animation,StateComponent.MOVING,0.2f, "skeleton-walkingRight", PlayMode.LOOP);
            createAnimation(animation,StateComponent.MOVING_UP,0.2f, "skeleton-walkingUp", PlayMode.LOOP);
            createAnimation(animation,StateComponent.MOVING_DOWN,0.2f, "skeleton-walkingDown", PlayMode.LOOP);
            createAnimation(animation,StateComponent.STANDING,1f, "skeleton-standingRight", PlayMode.NORMAL);
            createAnimation(animation,StateComponent.STANDING_UP,1f, "skeleton-standingUp", PlayMode.NORMAL);
            createAnimation(animation,StateComponent.STANDING_DOWN,1f, "skeleton-standingDown", PlayMode.NORMAL);

            //store a reference to the entity in the box2d box
            body.body.setUserData(entity);

            // add the components to the entity
            entity.add(body);
            entity.add(position);
            entity.add(texture);
            entity.add(animation);
            entity.add(enemy);
            entity.add(collision);
            entity.add(type);
            entity.add(state);

            engine.addEntity(entity);
        }
    }

    //create an animation with parameters given to a animation component
    private void createAnimation(AnimationComponent a, int state, float frameDuration, String atlasRegions, PlayMode playmode)
    {
        a.animations.put(state, new Animation(frameDuration, atlas.findRegions(atlasRegions), playmode));
    }

    //set player attributes according to his specialization
    private void createPlayerDefinition(PlayerComponent player, String spec)
    {
        switch (spec)
        {
            case "mage":
            {
                player.life = new Bar(80); //manage current and max life
                player.action = new Bar(200);
                player.damage = 10;
                player.speed = 2f;
                break;
            }
            case "hunter":
            {
                player.life = new Bar(100); //manage current and max life
                player.action = new Bar(150);
                player.damage = 8;
                player.speed = 3f;
                break;
            }
            case "warrior":
            {
                player.life = new Bar(200); //manage current and max life
                player.action = new Bar(100);
                player.damage = 5;
                player.speed = 1.5f;
                break;
            }
        }

        player.level = 1;
        player.spec = spec;
    }
}
