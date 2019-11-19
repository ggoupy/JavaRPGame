package com.game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.entity.component.*;
import com.game.loader.AssetsManager;
import com.game.utils.Bar;
import com.game.utils.Timer;

import static com.game.entity.utils.Mappers.*;


class PlayerFactory {

    //unique instance of this class = created only one time
    private static PlayerFactory thisInstance = null;

    private EntityFactory entityFactory;

    private ObjectMap xpForLevel;


    private PlayerFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.xpForLevel = entityFactory.assetsManager.json.fromJson(
                ObjectMap.class,
                Gdx.files.internal(AssetsManager.heroXpCfg)
        );
    }

    static PlayerFactory getInstance(EntityFactory entityFactory) {
        if (thisInstance == null) thisInstance = new PlayerFactory(entityFactory);
        return thisInstance;
    }


    //create a player box2D in the world according to his position in the tiled map
    void createPlayer(MapObject playerObj, String spec, String name) {
        // Create the Entity and all the components that will go in the entity
        Entity entity = entityFactory.engine.createEntity();
        PlayerComponent player = entityFactory.engine.createComponent(PlayerComponent.class);
        BodyComponent body = entityFactory.engine.createComponent(BodyComponent.class);
        TypeComponent type = entityFactory.engine.createComponent(TypeComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        StateComponent state = entityFactory.engine.createComponent(StateComponent.class);
        AnimationComponent animation = entityFactory.engine.createComponent(AnimationComponent.class);
        CollisionComponent collision = entityFactory.engine.createComponent(CollisionComponent.class);
        ReceiveAttackComponent receiveAttack = entityFactory.engine.createComponent(ReceiveAttackComponent.class);
        QuestComponent questCom = entityFactory.engine.createComponent(QuestComponent.class);

        //player definition
        createPlayerDefinition(player, spec);
        player.name = name;

        //get the rectangle of the mapObject (to have the coordinates)
        Rectangle rectangle = ((RectangleMapObject) playerObj).getRectangle();

        //convert rectangle coordinates into rectangle center coordinates in the world
        position.origin = BodyFactory.getTransformedCenterForRectangle(rectangle);

        //create a box in the world with coordinates and specific attributes
        body.body = entityFactory.bodyFactory.makeCircleBox(position.origin.x, position.origin.y, 0.75f, BodyDef.BodyType.DynamicBody, BodyFactory.HUMAN);

        position.position.set(position.origin.x, position.origin.y, 0);

        texture.region = entityFactory.atlas.findRegion(player.spec + "-standingDown");

        type.type = TypeComponent.PLAYER;

        state.set(StateComponent.STANDING_DOWN);

        //we add animations of the player in function of the state component
        //PlayMode.LOOP repeats animation after all frames shown
        entityFactory.createAnimation(animation, StateComponent.MOVING, 0.08f, player.spec + "-walkingRight", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation, StateComponent.MOVING_UP, 0.08f, player.spec + "-walkingUp", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation, StateComponent.MOVING_DOWN, 0.08f, player.spec + "-walkingDown", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation, StateComponent.STANDING, 1f, player.spec + "-standingRight", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation, StateComponent.STANDING_UP, 1f, player.spec + "-standingUp", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation, StateComponent.STANDING_DOWN, 1f, player.spec + "-standingDown", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation, StateComponent.ATTACKING, 0.08f, player.spec + "-attackingRight", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation, StateComponent.ATTACKING_UP, 0.08f, player.spec + "-attackingUp", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation, StateComponent.ATTACKING_DOWN, 0.08f, player.spec + "-attackingDown", Animation.PlayMode.NORMAL);

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
        entity.add(questCom);

        // add the entity to the engine
        entityFactory.engine.addEntity(entity);
    }

    //set player attributes according to his specialization
    @SuppressWarnings("unchecked")
    private void createPlayerDefinition(PlayerComponent player, String spec) {
        ObjectMap heroCfg = entityFactory.assetsManager.json.fromJson(
                ObjectMap.class,
                Gdx.files.internal(AssetsManager.heroCfg_path + spec + ".json")
        );

        player.life = new Bar((float) heroCfg.get("life"));
        player.action = new Bar((float) heroCfg.get("action"));
        player.damage = (float) heroCfg.get("damage");
        player.speed = (float) heroCfg.get("speed");
        player.lifeRegeneration = (float) heroCfg.get("lifeRegeneration");
        player.actionRegeneration = (float) heroCfg.get("actionRegeneration");
        player.level = 1;
        player.xpBar = new Bar(getXpForLevel(1), 0);
        player.spec = spec;
    }

    //reset player
    void killPlayer(Entity playerEntity) {
        //We reset the player life
        PlayerComponent player = playerMapper.get(playerEntity);
        player.life.setCurrent(1);

        //We remove all entities that had collision with player before to die
        //Collisions are usually removed when a contact end but like we set manually player pos, we need to remove them
        CollisionComponent collision = collisionMapper.get(playerEntity);
        collision.removeCollidedEntities();

        //We set body position of the player in the world to spawn player position
        //Body position will update automatically transform component position
        BodyComponent body = bodyMapper.get(playerEntity);
        TransformComponent pos = transformMapper.get(playerEntity);
        body.body.setTransform(pos.origin.x, pos.origin.y, 0);
    }

    @SuppressWarnings("unchecked")
    int getXpForLevel(int level) {
        return Integer.parseInt((String) xpForLevel.get("lvl " + level));
    }
}
