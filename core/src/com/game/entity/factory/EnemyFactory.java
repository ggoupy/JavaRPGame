package com.game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.entity.component.*;
import com.game.entity.utils.Spawn;
import com.game.loader.AssetsManager;
import com.game.utils.Bar;
import com.game.utils.JarUtils;
import com.game.utils.Timer;

import static com.game.entity.utils.Mappers.enemyMapper;
import static com.game.entity.utils.Mappers.enemySpawnMapper;

class EnemyFactory {

    //unique instance of this class = created only one time
    private static EnemyFactory thisInstance = null;

    private ArrayMap<String, EnemyComponent> prototypes;

    private EntityFactory entityFactory;


    private EnemyFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;

        this.prototypes = new ArrayMap<>();

        loadEnemyDefinitions();
    }


    static EnemyFactory getInstance(EntityFactory entityFactory) {
        if (thisInstance == null) thisInstance = new EnemyFactory(entityFactory);
        return thisInstance;
    }


    //load all enemy prototypes from json config files
    @SuppressWarnings("unchecked")
    private void loadEnemyDefinitions()
    {
        FileHandle cfgDir = Gdx.files.internal(AssetsManager.enemyCfg_path);
        for (FileHandle f: cfgDir.list())
        {
            readEnemyDefinition(f);
        }

        /* TO LOAD IF COMES FROM JAR */
        //The previous for loop will not do anything if running from a jar
        FileHandle []  cfgDirJar = JarUtils.listFromJarIfNecessary(AssetsManager.enemyCfg_path);
        for (FileHandle f: cfgDirJar)
        {
            readEnemyDefinition(f);
        }
    }


    //Read an enemy config file
    @SuppressWarnings("unchecked")
    private void readEnemyDefinition(FileHandle f)
    {
        String prototype = f.nameWithoutExtension();
        ObjectMap enemyCfg = entityFactory.assetsManager.json.fromJson(
                ObjectMap.class,
                Gdx.files.internal(AssetsManager.enemyCfg_path + f.name())
        );

        EnemyComponent enemy = new EnemyComponent();
        enemy.name = prototype;
        enemy.life = new Bar((float) enemyCfg.get("life"));
        enemy.speed = (float) enemyCfg.get("speed");
        enemy.damage = (float) enemyCfg.get("damage");
        enemy.aggressiveNature = Boolean.parseBoolean((String) enemyCfg.get("aggressiveNature"));
        enemy.aggroRange = (float) enemyCfg.get("aggroRange");
        enemy.movingRange = (float) enemyCfg.get("movingRange");
        enemy.xpGain = (float) enemyCfg.get("xpGain");
        enemy.level = 1;

        //We check if the config has a standing time value, else we set the normal procedure
        Object standingTime = enemyCfg.get("standingTime");
        enemy.standingTime = (standingTime != null) ? (float) standingTime : -1; //to inform that no standing time set

        prototypes.put(prototype, enemy);
    }


    //create an enemy component using a prototype for a given level
    private EnemyComponent createEnemyComponent(String prototype, int level) {
        EnemyComponent enemyCom = entityFactory.engine.createComponent(EnemyComponent.class);
        EnemyComponent toCopy = prototypes.get(prototype);
        enemyCom.name = toCopy.name;
        enemyCom.life = new Bar(toCopy.life.getMax());
        enemyCom.speed = toCopy.speed;
        enemyCom.damage = toCopy.damage;
        enemyCom.aggressiveNature = toCopy.aggressiveNature;
        enemyCom.aggressive = enemyCom.aggressiveNature;
        enemyCom.aggroRange = toCopy.aggroRange;
        enemyCom.movingRange = toCopy.movingRange;
        enemyCom.movingTime = (float) (2 + Math.random() * 2); //rand between 2 and 4
        enemyCom.standingTime = toCopy.standingTime == -1 ? (float) (2 + Math.random() * 2) : toCopy.standingTime; //we set a standing time if not defined
        enemyCom.xpGain = (float) (toCopy.xpGain + Math.random() * 30f); //random between xpGain and xpGain+30;
        enemyCom.level = level;
        adjustLevel(enemyCom, toCopy); //up some stats according to level
        return enemyCom;
    }

    //adjust stats of an enemy component according to its level
    //stat are based with player level stats, but is not perfect and would be modified later
    private void adjustLevel(EnemyComponent enemyCom, EnemyComponent prototype) {
        if (enemyCom.level > 1) //we do not multiply stats of lvl 1
        {
            enemyCom.life.setMax(prototype.life.getMax() * ((float) Math.pow(1.1, enemyCom.level-1)), true);

           enemyCom.damage = enemyCom.damage * ((float) Math.pow(1.05, enemyCom.level-1));

            //To have the enemy XP gain according to its level (+ its initial xp = bonus)
            enemyCom.xpGain += (enemyCom.level * 5) + 50;
        }
    }


    //create an enemy spawn entity containing various spawns in an area
    void createEnemySpawn(MapLayer spawnLayer) {
        MapObjects spawns = spawnLayer.getObjects();

        Entity entitySpawn = entityFactory.engine.createEntity();
        EnemySpawnComponent enemySpawnCom = entityFactory.engine.createComponent(EnemySpawnComponent.class);

        enemySpawnCom.name = spawnLayer.getName();

        float respawnTime = (spawns.getCount() < 4) ? 10f : spawns.getCount() * 3f; //re-spawn time is at least 10s
        enemySpawnCom.RespawnTimer = new Timer(respawnTime); //new enemy all x seconds
        enemySpawnCom.enemyType = (String) spawnLayer.getProperties().get("enemyType");
        enemySpawnCom.enemyLevel = (int) spawnLayer.getProperties().get("enemyLevel");
        entitySpawn.add(enemySpawnCom);
        entityFactory.engine.addEntity(entitySpawn);
        for (int i = 0; i < spawns.getCount(); ++i) {
            Rectangle r = ((RectangleMapObject) spawns.get(i)).getRectangle();
            enemySpawnCom.addSpawn(r, false);
            createEnemy(entitySpawn, i); //create an new enemy in this spawn
        }
    }


    //create enemy box2D from a spawn entity and the index of the spawn
    void createEnemy(Entity spawnEntity, int index) {
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
        EnemyComponent enemy = createEnemyComponent(spawnCom.enemyType, spawnCom.enemyLevel);
        BodyComponent body = entityFactory.engine.createComponent(BodyComponent.class);
        TypeComponent type = entityFactory.engine.createComponent(TypeComponent.class);
        CollisionComponent collision = entityFactory.engine.createComponent(CollisionComponent.class);
        StateComponent state = entityFactory.engine.createComponent(StateComponent.class);
        AnimationComponent animation = entityFactory.engine.createComponent(AnimationComponent.class);
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        AttachedComponent attached = entityFactory.engine.createComponent(AttachedComponent.class);
        ReceiveAttackComponent receiveAttack = entityFactory.engine.createComponent(ReceiveAttackComponent.class);
        FontComponent font = entityFactory.engine.createComponent(FontComponent.class);

        type.type = TypeComponent.ENEMY;

        //convert rectangle coordinates into rectangle center coordinates in the world
        Vector2 center = BodyFactory.getTransformedCenterForRectangle(spawnPos);

        body.body = entityFactory.bodyFactory.makeCircleBox(center.x, center.y, 1, BodyDef.BodyType.DynamicBody, BodyFactory.STONE, true);

        position.position.set(center.x, center.y, 0);

        enemy.origin = new Vector2(center.x, center.y);
        enemy.direction = new Vector2(0, 0);
        enemy.spawnName = spawnCom.name;

        texture.region = entityFactory.atlas.findRegion(spawnCom.enemyType + "-standingDown");

        state.set(StateComponent.STANDING_DOWN);

        //we add animations of the player in function of the state component
        //PlayMode.LOOP repeats animation after all frames shown
        entityFactory.createAnimation(animation, StateComponent.MOVING, 0.2f, spawnCom.enemyType + "-walkingRight", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation, StateComponent.MOVING_UP, 0.2f, spawnCom.enemyType + "-walkingUp", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation, StateComponent.MOVING_DOWN, 0.2f, spawnCom.enemyType + "-walkingDown", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation, StateComponent.STANDING, 1f, spawnCom.enemyType + "-standingRight", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation, StateComponent.STANDING_UP, 1f, spawnCom.enemyType + "-standingUp", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation, StateComponent.STANDING_DOWN, 1f, spawnCom.enemyType + "-standingDown", Animation.PlayMode.NORMAL);

        //store a reference to the entity in the box2d box
        body.body.setUserData(entity);

        font.text = "Lv. " + enemy.level;

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
        entity.add(font);

        //need to be at the end, to store firstly all components in the entity
        //create the enemy health bar entity
        attached.attachedEntity = createEnemyHealthBar(entity);
        entity.add(attached);

        entityFactory.engine.addEntity(entity); //add the enemy entity to the engine
        enemySpawn.user = entity; //store the enemy entity in the spawn entity
    }


    //create a health bar entity attached to an enemy
    private Entity createEnemyHealthBar(Entity enemy) {
        Entity entity = entityFactory.engine.createEntity();
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        AttachedComponent attached = entityFactory.engine.createComponent(AttachedComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        EnemyHealthComponent health = entityFactory.engine.createComponent(EnemyHealthComponent.class);

        attached.attachedEntity = enemy;

        //we store both health bar type (aggressive/passive) in tex.region2 & tex.region3 and
        //display the wanted one with tex.region
        //we would need to use it in case of the enemy becomes aggressive or passive
        //We need to create new texture region as we will use it for different enemies (bug if not)
        String healthBarType = (enemyMapper.get(enemy).aggressiveNature) ? "aggressive" : "passive";
        String healthBarOtherType = (enemyMapper.get(enemy).aggressiveNature) ? "passive" : "aggressive";
        texture.region2 = new TextureRegion(entityFactory.atlas.findRegion("enemy-healthbar-" + healthBarType));
        texture.region3 = new TextureRegion(entityFactory.atlas.findRegion("enemy-healthbar-" + healthBarOtherType));
        texture.region = new TextureRegion(texture.region2);

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
