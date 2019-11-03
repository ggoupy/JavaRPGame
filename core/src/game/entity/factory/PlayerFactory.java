package game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import game.entity.component.*;
import game.utils.Bar;
import game.utils.Timer;


public class PlayerFactory {

    //unique instance of this class = created only one time
    private static PlayerFactory thisInstance = null;

    private EntityFactory entityFactory;


    public PlayerFactory(EntityFactory entityFactory)
    {
        this.entityFactory = entityFactory;
    }

    public static PlayerFactory getInstance(EntityFactory entityFactory)
    {
        if(thisInstance == null) thisInstance = new PlayerFactory(entityFactory);
        return thisInstance;
    }


    //create a player box2D in the world according to his position in the tiled map
    public Entity createPlayer(MapObject playerObj, String spec, String name)
    {
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

        //player definition
        createPlayerDefinition(player, spec);
        player.name = name;

        //get the rectangle of the mapObject (to have the coordinates)
        Rectangle rectangle = ((RectangleMapObject)playerObj).getRectangle();

        //convert rectangle coordinates into rectangle center coordinates in the world
        Vector2 center = BodyFactory.getTransformedCenterForRectangle(rectangle);

        //create a box in the world with coordinates and specific attributes
        body.body = entityFactory.bodyFactory.makeCircleBox(center.x, center.y,0.75f, BodyDef.BodyType.DynamicBody, BodyFactory.HUMAN);

        position.position.set(center.x,center.y,0);

        texture.region = entityFactory.atlas.findRegion(player.spec+"-standingDown");

        type.type = TypeComponent.PLAYER;

        receiveAttack.entitiesAttacking = new Array<>();

        collision.collisionEntity = new Array<>();

        state.set(StateComponent.STANDING_DOWN);

        //we add animations of the player in function of the state component
        //PlayMode.LOOP repeats animation after all frames shown
        entityFactory.createAnimation(animation,StateComponent.MOVING,0.08f, player.spec+"-walkingRight", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation,StateComponent.MOVING_UP,0.08f, player.spec+"-walkingUp", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation,StateComponent.MOVING_DOWN,0.08f, player.spec+"-walkingDown", Animation.PlayMode.LOOP);
        entityFactory.createAnimation(animation,StateComponent.STANDING,1f, player.spec+"-standingRight", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation,StateComponent.STANDING_UP,1f,player.spec+"-standingUp", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation,StateComponent.STANDING_DOWN,1f, player.spec+"-standingDown", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation,StateComponent.ATTACKING,0.08f, player.spec+"-attackingRight", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation,StateComponent.ATTACKING_UP,0.08f,player.spec+"-attackingUp", Animation.PlayMode.NORMAL);
        entityFactory.createAnimation(animation,StateComponent.ATTACKING_DOWN,0.08f, player.spec+"-attackingDown", Animation.PlayMode.NORMAL);

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
        entityFactory.engine.addEntity(entity);

        return entity; //to save the player
    }

    //set player attributes according to his specialization
    private void createPlayerDefinition(PlayerComponent player, String spec)
    {
        ObjectMap heroCfg = entityFactory.assetsManager.json.fromJson(
                ObjectMap.class,
                Gdx.files.internal(entityFactory.assetsManager.heroCfg_path+spec+".json")
        );

        player.life = new Bar((float) heroCfg.get("life"));
        player.action = new Bar((float) heroCfg.get("action"));
        player.damage = (float) heroCfg.get("damage");
        player.speed = (float) heroCfg.get("speed");
        player.lifeRegeneration = (float) heroCfg.get("lifeRegeneration");
        player.level = 1;
        player.xpBar = new Bar(100,0);
        player.spec = spec;
    }
}
