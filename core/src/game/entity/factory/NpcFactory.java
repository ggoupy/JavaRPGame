package game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import game.entity.component.*;
import game.loader.AssetsManager;
import game.quest.KillingObjective;
import game.quest.Objective;
import game.quest.Quest;

import static game.entity.utils.Mappers.*;

public class NpcFactory {

    //unique instance of this class = created only one time
    private static NpcFactory thisInstance = null;

    private EntityFactory entityFactory;


    public NpcFactory(EntityFactory entityFactory)
        {
            this.entityFactory = entityFactory;
        }

    public static NpcFactory getInstance(EntityFactory entityFactory)
    {
        if(thisInstance == null) thisInstance = new NpcFactory(entityFactory);
        return thisInstance;
    }

    public void createNPCs(MapLayer spawnLayer)
    {
        MapObjects spawns = spawnLayer.getObjects();
        for (MapObject npc : spawns) createNpc(npc);
    }

    public void createNpc(MapObject npcObj)
    {
        // Create the Entity and all the components that will go in the entity
        Entity entity = entityFactory.engine.createEntity();
        NpcComponent npc = entityFactory.engine.createComponent(NpcComponent.class);
        QuestComponent questCom = entityFactory.engine.createComponent(QuestComponent.class);
        TypeComponent type = entityFactory.engine.createComponent(TypeComponent.class);
        BodyComponent body = entityFactory.engine.createComponent(BodyComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        CollisionComponent collision = entityFactory.engine.createComponent(CollisionComponent.class);

        //get the rectangle of the mapObject (to have the coordinates)
        Rectangle rectangle = ((RectangleMapObject)npcObj).getRectangle();

        //convert rectangle coordinates into rectangle center coordinates in the world
        position.origin = BodyFactory.getTransformedCenterForRectangle(rectangle);

        //create a box in the world with coordinates and specific attributes
        body.body = entityFactory.bodyFactory.makeBox(rectangle, BodyDef.BodyType.DynamicBody, BodyFactory.HUMAN, true);

        position.position.set(position.origin.x,position.origin.y,0);

        texture.region = entityFactory.atlas.findRegion(npcObj.getProperties().get("type")+"-npc");

        type.type = TypeComponent.NPC;

        collision.collisionEntity = new Array<>();

        questCom.quests = createNpcQuests((String) npcObj.getName());

        //store a reference to the entity in the box2d box
        body.body.setUserData(entity);

        entity.add(npc);
        entity.add(questCom);
        entity.add(type);
        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(collision);

        entityFactory.engine.addEntity(entity);
    }

    @SuppressWarnings("unchecked")
    private Array<Quest> createNpcQuests(String npcName)
    {
        Array<Quest> quests = new Array<>();

        Array<ObjectMap> jsonQuests = entityFactory.assetsManager.json.fromJson(
                Array.class, ObjectMap.class,
                Gdx.files.internal(AssetsManager.npcCfg_path+npcName+".json")
        );

        for (int i=0; i<jsonQuests.size; ++i)
        {
            ObjectMap quest = jsonQuests.get(i);
            String title = (String) quest.get("title");
            String description = (String) quest.get("description");
            String type = (String) quest.get("type");
            if (type.equals("kill"))
            {
                int toKill = Integer.parseInt((String) quest.get("toKill"));
                String enemy = (String) quest.get("enemy");
                Objective obj = new KillingObjective(description, type, toKill, enemy);
                quests.add(new Quest(title, obj));
            }
        }

        return quests;
    }
}
