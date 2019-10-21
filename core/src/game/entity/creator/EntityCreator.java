package game.entity.creator;

import java.util.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import game.entity.component.*;
import game.entity.component.player.PlayerComponent;


public class EntityCreator {

    //References to game attributes
    private World world;
    private PooledEngine engine;

    private BodyFactory bodyFactory;

    //the EntityCreator class will be created only one time
    //So we will store the instance in the class
    //static -> single copy of variable is created and shared among all objects at class level (=efficient memory)
    private static EntityCreator thisInstance;


    //private constructor because it's singleton class
    private EntityCreator (World world, PooledEngine engine)
    {
        this.world = world;
        this.engine = engine;
        bodyFactory = bodyFactory.getInstance(world);
    }

    //get the body factory instance and create it if not instanced
    //used to create the instance (like a constructor)
    public static EntityCreator getInstance(World world, PooledEngine engine)
    {
        if (thisInstance == null) thisInstance = new EntityCreator(world, engine);
        return thisInstance;
    }


    //create a player box2D in the world according to his position in the tiled map
    public void createPlayer(MapObject playerObj, String spec, TextureAtlas atlas)
    {
        switch (spec)
        {
            //Create the good player
            //coming soon
        }

        //get the rectangle of the mapObject (to have the coordinates)
        Rectangle rectangle = ((RectangleMapObject)playerObj).getRectangle();

        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);

        //convert rectangle coordinates into rectangle center coordinates in the world
        Vector2 center = BodyFactory.getTransformedCenterForRectangle(rectangle);

        //create a box in the world with coordinates and specific attributes
        body.body = bodyFactory.makeCircleBox(center.x, center.y,1, BodyDef.BodyType.DynamicBody, BodyFactory.HUMAN);


        position.position.set(center.x,center.y,0);

        texture.region = atlas.findRegion("standingRight");

        type.type = TypeComponent.PLAYER;

        state.set(StateComponent.STANDING);

        //we add animations of the player in fonction of the state component
        //PlayMode.LOOP repeats animation after all frames shown
        Animation animStand = new Animation(1f, atlas.findRegions("standingRight"), Animation.PlayMode.NORMAL);
        Animation animStandUp = new Animation(1f, atlas.findRegions("standingUp"), Animation.PlayMode.NORMAL);
        Animation animStandDown = new Animation(1f, atlas.findRegions("standingDown"), Animation.PlayMode.NORMAL);
        Animation animWalkingX = new Animation(0.08f, atlas.findRegions("walkingRight"), Animation.PlayMode.LOOP);
        Animation animWalkingUp = new Animation(0.08f, atlas.findRegions("walkingUp"), Animation.PlayMode.LOOP);
        Animation animWalkingDown = new Animation(0.08f, atlas.findRegions("walkingDown"), Animation.PlayMode.LOOP);
        animation.animations.put(StateComponent.MOVING, animWalkingX);
        animation.animations.put(StateComponent.MOVING_UP, animWalkingUp);
        animation.animations.put(StateComponent.MOVING_DOWN, animWalkingDown);
        animation.animations.put(StateComponent.STANDING, animStand);
        animation.animations.put(StateComponent.STANDING_UP, animStandUp);
        animation.animations.put(StateComponent.STANDING_DOWN, animStandDown);

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
        System.out.println(uniqIndex_arr.size());
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

            body.body = bodyFactory.makeBox(rectangle, BodyDef.BodyType.StaticBody,BodyFactory.STONE);

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
}
