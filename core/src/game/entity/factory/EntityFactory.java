package game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import game.entity.component.*;
import game.entity.component.PlayerComponent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import game.utils.Bar;
import game.utils.Timer;


public class EntityFactory {

    //References to game attributes
    private World world;
    private PooledEngine engine;
    private TextureAtlas atlas;

    private BodyFactory bodyFactory;

    //the EntityCreator class will be created only one time
    //So we will store the instance in the class
    //static -> single copy of variable is created and shared among all objects at class level (=efficient memory)
    private static EntityFactory thisInstance;


    //private constructor because it's singleton class
    private EntityFactory (World world, PooledEngine engine, TextureAtlas atlas)
    {
        this.world = world;
        this.engine = engine;
        this.atlas = atlas;
        bodyFactory = bodyFactory.getInstance(world);
    }

    //get the body factory instance and create it if not instanced
    //used to create the instance (like a constructor)
    public static EntityFactory getInstance(World world, PooledEngine engine, TextureAtlas atlas)
    {
        if (thisInstance == null) thisInstance = new EntityFactory(world, engine, atlas);
        return thisInstance;
    }


    //create a player box2D in the world according to his position in the tiled map
    public Entity createPlayer(MapObject playerObj, String spec, String name)
    {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        ReceiveAttackComponent receiveAttack = engine.createComponent(ReceiveAttackComponent.class);

        //player definition
        createPlayerDefinition(player, spec);
        player.name = name;

        //get the rectangle of the mapObject (to have the coordinates)
        Rectangle rectangle = ((RectangleMapObject)playerObj).getRectangle();

        //convert rectangle coordinates into rectangle center coordinates in the world
        Vector2 center = BodyFactory.getTransformedCenterForRectangle(rectangle);

        //create a box in the world with coordinates and specific attributes
        body.body = bodyFactory.makeCircleBox(center.x, center.y,0.75f, BodyDef.BodyType.DynamicBody, BodyFactory.HUMAN);

        position.position.set(center.x,center.y,0);

        texture.region = atlas.findRegion(player.spec+"-standingDown");

        type.type = TypeComponent.PLAYER;

        receiveAttack.entitiesAttacking = new Array<>();

        collision.collisionEntity = new Array<>();

        state.set(StateComponent.STANDING_DOWN);

        //we add animations of the player in function of the state component
        //PlayMode.LOOP repeats animation after all frames shown
        createAnimation(animation,StateComponent.MOVING,0.08f, player.spec+"-walkingRight", PlayMode.LOOP);
        createAnimation(animation,StateComponent.MOVING_UP,0.08f, player.spec+"-walkingUp", PlayMode.LOOP);
        createAnimation(animation,StateComponent.MOVING_DOWN,0.08f, player.spec+"-walkingDown", PlayMode.LOOP);
        createAnimation(animation,StateComponent.STANDING,1f, player.spec+"-standingRight", PlayMode.NORMAL);
        createAnimation(animation,StateComponent.STANDING_UP,1f,player.spec+"-standingUp", PlayMode.NORMAL);
        createAnimation(animation,StateComponent.STANDING_DOWN,1f, player.spec+"-standingDown", PlayMode.NORMAL);
        createAnimation(animation,StateComponent.ATTACKING,0.08f, player.spec+"-attackingRight", PlayMode.NORMAL);
        createAnimation(animation,StateComponent.ATTACKING_UP,0.08f,player.spec+"-attackingUp", PlayMode.NORMAL);
        createAnimation(animation,StateComponent.ATTACKING_DOWN,0.08f, player.spec+"-attackingDown", PlayMode.NORMAL);

        //get the attack duration to disable movements when player attacking
        player.attackDuration = new Timer(animation.animations.get(StateComponent.ATTACKING).getAnimationDuration());

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
        entity.add(receiveAttack);

        // add the entity to the engine
        engine.addEntity(entity);

        return entity; //to save the player
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
        //toSpawnLeft = 1; //PROVISOIRE
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
            AttachedComponent attached = engine.createComponent(AttachedComponent.class);
            ReceiveAttackComponent receiveAttack = engine.createComponent(ReceiveAttackComponent.class);

            type.type = TypeComponent.ENEMY;

            //convert rectangle coordinates into rectangle center coordinates in the world
            Vector2 center = BodyFactory.getTransformedCenterForRectangle(rectangle);

            body.body = bodyFactory.makeBox(rectangle, BodyDef.BodyType.DynamicBody, BodyFactory.STONE, true);

            position.position.set(center.x,center.y,0);

            enemy.origin = new Vector2(center.x, center.y);
            enemy.movingTime = 2 + Math.random() * 2; //random between 2 and 4
            enemy.standingTime = 2 + Math.random() * 2; //random between 2 and 4

            receiveAttack.entitiesAttacking = new Array<>();

            collision.collisionEntity = new Array<>();

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
            entity.add(receiveAttack);

            //need to be after to store firstly all components in the entity
            attached.attachedEntity = createEnemyHealthBar(entity);
            entity.add(attached);

            engine.addEntity(entity);
        }
    }

    private Entity createEnemyHealthBar(Entity enemy)
    {
        Entity entity = engine.createEntity();
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        AttachedComponent attached = engine.createComponent(AttachedComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        EnemyHealthComponent health = engine.createComponent(EnemyHealthComponent.class);

        attached.attachedEntity = enemy;

        texture.region = new TextureRegion(atlas.findRegion("enemy-healthbar"));

        Vector3 enemyPos = enemy.getComponent(TransformComponent.class).position;
        position.position.set(enemyPos);

        health.size = new Rectangle(texture.region.getRegionX(), texture.region.getRegionY(),
                                    texture.region.getRegionWidth(), texture.region.getRegionHeight()
        );

        entity.add(texture);
        entity.add(position);
        entity.add(attached);
        entity.add(health);
        engine.addEntity(entity);

        return entity;
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
                player.damage = 20;
                player.speed = 2f;
                break;
            }
            case "hunter":
            {
                player.life = new Bar(100); //manage current and max life
                player.action = new Bar(150);
                player.damage = 12;
                player.speed = 3f;
                break;
            }
            case "warrior":
            {
                player.life = new Bar(200); //manage current and max life
                player.action = new Bar(100);
                player.damage = 15;
                player.speed = 1.5f;
                break;
            }
        }

        player.level = 1;
        player.spec = spec;
    }



    //Destroy an entity and all components attached to it
    public void destroyEntity(Entity entity) {destroyEntity(entity, true);}
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
    }
}