package com.game.entity.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.entity.component.*;
import com.game.loader.AssetsManager;
import com.game.quest.KillingObjective;
import com.game.quest.Objective;
import com.game.quest.Quest;

import static com.game.entity.utils.Mappers.transformMapper;

class NpcFactory {

    //unique instance of this class = created only one time
    private static NpcFactory thisInstance = null;

    private EntityFactory entityFactory;


    private NpcFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    static NpcFactory getInstance(EntityFactory entityFactory) {
        if (thisInstance == null) thisInstance = new NpcFactory(entityFactory);
        return thisInstance;
    }

    void createNPCs(MapLayer spawnLayer) {
        MapObjects spawns = spawnLayer.getObjects();
        for (MapObject npc : spawns) createNpc(npc);
    }

    private void createNpc(MapObject npcObj) {
        // Create the Entity and all the components that will go in the entity
        Entity entity = entityFactory.engine.createEntity();
        NpcComponent npc = entityFactory.engine.createComponent(NpcComponent.class);
        QuestComponent questCom = entityFactory.engine.createComponent(QuestComponent.class);
        TypeComponent type = entityFactory.engine.createComponent(TypeComponent.class);
        BodyComponent body = entityFactory.engine.createComponent(BodyComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        CollisionComponent collision = entityFactory.engine.createComponent(CollisionComponent.class);
        AttachedComponent attached = entityFactory.engine.createComponent(AttachedComponent.class);

        //get the rectangle of the mapObject (to have the coordinates)
        Rectangle rectangle = ((RectangleMapObject) npcObj).getRectangle();

        //convert rectangle coordinates into rectangle center coordinates in the world
        position.origin = BodyFactory.getTransformedCenterForRectangle(rectangle);

        //create a box in the world with coordinates and specific attributes
        body.body = entityFactory.bodyFactory.makeBox(rectangle, BodyDef.BodyType.DynamicBody, BodyFactory.HUMAN, true);

        position.position.set(position.origin.x, position.origin.y, 0);

        texture.region = entityFactory.atlas.findRegion(npcObj.getProperties().get("type") + "-npc");

        type.type = TypeComponent.NPC;

        createNpcQuests(npcObj.getName(), questCom.quests);

        //store a reference to the entity in the box2d box
        body.body.setUserData(entity);

        entity.add(npc);
        entity.add(questCom);
        entity.add(type);
        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(collision);

        attached.attachedEntity = createQuestIcon(entity);
        entity.add(attached);

        entityFactory.engine.addEntity(entity);
    }

    private Entity createQuestIcon(Entity npcEntity) {
        Entity entity = entityFactory.engine.createEntity();
        TextureComponent texture = entityFactory.engine.createComponent(TextureComponent.class);
        TransformComponent position = entityFactory.engine.createComponent(TransformComponent.class);
        AttachedComponent attached = entityFactory.engine.createComponent(AttachedComponent.class);

        texture.region = null;
        texture.region2 = entityFactory.assetsManager.getAtlas().findRegion("quest-icon");
        texture.region3 = entityFactory.assetsManager.getAtlas().findRegion("completed-quest-icon");

        position.isHidden = true;
        Vector3 npcPos = transformMapper.get(npcEntity).position;
        position.position.set(npcPos.x, npcPos.y + 1, npcPos.z);

        entity.add(texture);
        entity.add(position);
        entity.add(attached);
        entityFactory.engine.addEntity(entity);

        return entity;
    }

    @SuppressWarnings("unchecked")
    private void createNpcQuests(String npcName, Array<Quest> quests) {
        Array<ObjectMap> jsonQuests = entityFactory.assetsManager.json.fromJson(
                Array.class, ObjectMap.class,
                Gdx.files.internal(AssetsManager.npcCfg_path + npcName + ".json")
        );

        for (int i = 0; i < jsonQuests.size; ++i) {
            ObjectMap quest = jsonQuests.get(i);
            String title = (String) quest.get("title");
            String description = (String) quest.get("description");
            Array<String> areas = (Array<String>) quest.get("area");
            int xp = Integer.parseInt((String) quest.get("xp"));
            String type = (String) quest.get("type");
            if (type.equals("kill")) {
                int toKill = Integer.parseInt((String) quest.get("toKill"));
                String enemy = (String) quest.get("enemy");
                Objective obj = new KillingObjective(description, type, toKill, enemy, areas);
                quests.add(new Quest(title, obj, xp));
            }
        }
    }
}
