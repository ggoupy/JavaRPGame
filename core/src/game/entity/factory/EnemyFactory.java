package game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import game.entity.component.*;
import game.entity.utils.Spawn;
import game.utils.Bar;
import game.utils.Timer;

import static game.entity.utils.Mappers.*;


public class EnemyFactory {

    public static final String SKELETON = "skeleton";
    public static final String SPIDER = "spider";

    //unique instance of this class = created only one time
    private static EnemyFactory thisInstance = null;

    private ArrayMap<String, EnemyComponent> prototypes;

    private EntityFactory entityFactory;


    public EnemyFactory(EntityFactory entityFactory)
    {
        this.entityFactory = entityFactory;

        this.prototypes = new ArrayMap<>();

        loadEnemyDefinition(SKELETON);
    }


    public static EnemyFactory getInstance(EntityFactory entityFactory)
    {
        if(thisInstance == null) thisInstance = new EnemyFactory(entityFactory);
        return thisInstance;
    }


    //load a enemy prototype from a json file
    public void loadEnemyDefinition(String prototype)
    {
        ObjectMap enemyCfg = entityFactory.assetsManager.json.fromJson(
                ObjectMap.class,
                Gdx.files.internal(entityFactory.assetsManager.enemyCfg_path+prototype+".json")
        );

        EnemyComponent enemy = new EnemyComponent();
        enemy.life = new Bar((float) enemyCfg.get("life"));
        enemy.speed = (float) enemyCfg.get("speed");
        enemy.damage = (float) enemyCfg.get("damage");
        enemy.aggressive = Boolean.parseBoolean((String) enemyCfg.get("aggressive"));
        enemy.aggroRange = (float) enemyCfg.get("aggroRange");
        enemy.movingRange = (float) enemyCfg.get("movingRange");
        enemy.xpGain = (float) enemyCfg.get("xpGain");
        enemy.level = 1;

        prototypes.put(prototype, enemy);
    }


    //create an enemy component using a prototype for a given level
    public EnemyComponent createEnemyComponent(String prototype, int level)
    {
        EnemyComponent enemyCom = entityFactory.engine.createComponent(EnemyComponent.class);
        EnemyComponent toCopy = prototypes.get(prototype);
        enemyCom.life = new Bar(toCopy.life.getMax());
        enemyCom.speed = toCopy.speed;
        enemyCom.damage = toCopy.damage;
        enemyCom.aggressive = toCopy.aggressive;
        enemyCom.aggroRange = toCopy.aggroRange;
        enemyCom.movingRange = toCopy.movingRange;
        enemyCom.movingTime = (float) (2 + Math.random() * 2); //rand between 2 and 4
        enemyCom.standingTime = (float) (2 + Math.random() * 2); //rand between 2 and 4
        enemyCom.xpGain = (float) (toCopy.xpGain + Math.random() * 30f); //random between xpGain and xpGain+30;
        enemyCom.level = level;
        adjustLevel(enemyCom); //up some stats according to level
        return enemyCom;
    }

    //adjust stats of an enemy component according to its level
    public void adjustLevel(EnemyComponent enemyCom)
    {
        enemyCom.life.setMax(enemyCom.life.getMax()*enemyCom.level*1.1f,true);
        enemyCom.damage *= enemyCom.level*1.1f;
        enemyCom.xpGain *= enemyCom.level*1.1f;
    }


    //create an enemy spawn entity containing various spawns in an area
    public void createEnemySpawn(MapObjects spawns)
    {
        Entity entitySpawn = entityFactory.engine.createEntity();
        EnemySpawnComponent enemySpawnCom = entityFactory.engine.createComponent(EnemySpawnComponent.class);
        enemySpawnCom.spawns = new Array<>();
        enemySpawnCom.RespawnTimer = new Timer(spawns.getCount()*3); //new enemy all x seconds
        entitySpawn.add(enemySpawnCom);
        entityFactory.engine.addEntity(entitySpawn);
        for(int i=0; i<spawns.getCount(); ++i)
        {
            Rectangle r = ((RectangleMapObject) spawns.get(i)).getRectangle();
            enemySpawnCom.addSpawn(r, false);
            createEnemy(entitySpawn, i, SKELETON, 1); //create an new enemy in this spawn
        }
    }


    //create enemy box2D from a spawn entity and the index of the spawn
    public void createEnemy(Entity spawnEntity, int index, String enemyType, int level)
    {
        //get the spawn component from the spawn entity
        EnemySpawnComponent spawnCom = enemySpawnMapper.get(spawnEntity);
        Spawn enemySpawn = spawnCom.spawns.get(index);

        //to do not create an new enemy if already used
        if (enemySpawn.user != null) return;

        //set the spawn taken
        enemySpawn.taken = true;

        //get the position rectangle from the array of spawns in the spawn component
        Rectangle spawnPos = enemySpawn.spawnPos;

        /* Create the enemy entity */
        Entity entity = entityFactory.engine.createEntity();
        EnemyComponent enemy = createEnemyComponent(enemyType, level); //according to enemy type
        BodyComponent body = entityFactory.engine.createComponent(BodyComponent.class);
        TypeComponent type = entityFactory.engine.createComponent(TypeComponent.class);
        CollisionComponent collision = entityFactory.engine.createComponent(CollisionComponent.class);
        StateComponent state = entityFactory.engine.createComponent(StateComponent.class);
        AnimationComponent animation = entityFactory.engine.createComponent(AnimationComponent.class);
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        AttachedComponent attached = entityFactory.engine.createComponent(AttachedComponent.class);
        ReceiveAttackComponent receiveAttack = entityFactory.engine.createComponent(ReceiveAttackComponent.class);

        type.type = TypeComponent.ENEMY;

        //convert rectangle coordinates into rectangle center coordinates in the world
        Vector2 center = BodyFactory.getTransformedCenterForRectangle(spawnPos);

        body.body = entityFactory.bodyFactory.makeBox(spawnPos, BodyDef.BodyType.DynamicBody, BodyFactory.STONE, true);

        position.position.set(center.x,center.y,0);

        enemy.origin = new Vector2(center.x, center.y);
        enemy.direction = new Vector2(0,0);

        receiveAttack.entitiesAttacking = new Array<>();

        collision.collisionEntity = new Array<>();

        texture.region = entityFactory.atlas.findRegion("skeleton-standingDown");

        state.set(StateComponent.STANDING_DOWN);

        //we add animations of the player in function of the state component
        //PlayMode.LOOP repeats animation after all frames shown
        entityFactory.createAnimation(animation,StateComponent.MOVING,0.2f, "skeleton-walkingRight", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation,StateComponent.MOVING_UP,0.2f, "skeleton-walkingUp", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation,StateComponent.MOVING_DOWN,0.2f, "skeleton-walkingDown", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation,StateComponent.STANDING,1f, "skeleton-standingRight", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation,StateComponent.STANDING_UP,1f, "skeleton-standingUp", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation,StateComponent.STANDING_DOWN,1f, "skeleton-standingDown", Animation.PlayMode.NORMAL);

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

        //need to be at the end, to store firstly all components in the entity
        //create the enemy health bar entity
        attached.attachedEntity = createEnemyHealthBar(entity);
        entity.add(attached);

        entityFactory.engine.addEntity(entity); //add the enemy entity to the engine
        enemySpawn.user = entity; //store the enemy entity in the spawn entity
    }


    //create a health bar entity attached to an enemy
    private Entity createEnemyHealthBar(Entity enemy)
    {
        Entity entity = entityFactory.engine.createEntity();
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        AttachedComponent attached = entityFactory.engine.createComponent(AttachedComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        EnemyHealthComponent health = entityFactory.engine.createComponent(EnemyHealthComponent.class);

        attached.attachedEntity = enemy;

        texture.region = new TextureRegion(entityFactory.atlas.findRegion("enemy-healthbar"));

        Vector3 enemyPos = enemy.getComponent(TransformComponent.class).position;
        position.position.set(enemyPos);

        health.size = new Rectangle(texture.region.getRegionX(), texture.region.getRegionY(),
                texture.region.getRegionWidth(), texture.region.getRegionHeight()
        );

        entity.add(texture);
        entity.add(position);
        entity.add(attached);
        entity.add(health);
        entityFactory.engine.addEntity(entity);

        return entity;
    }
}
