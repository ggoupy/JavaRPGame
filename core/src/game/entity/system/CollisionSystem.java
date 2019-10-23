package game.entity.system;

import game.entity.component.*;
import game.entity.component.player.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;


public class CollisionSystem  extends IteratingSystem {

    ComponentMapper<CollisionComponent> cm;
    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<EnemyComponent> em;


    public CollisionSystem()
    {
        //create an collision system for all entities containing a collision component
        super(Family.all(CollisionComponent.class).get());

        //create component mappers of component class
        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(PlayerComponent.class);
        em = ComponentMapper.getFor(EnemyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        CollisionComponent collision = cm.get(entity); //get the entity collision component
        TypeComponent thisType = entity.getComponent(TypeComponent.class); //get the entity type component

        Entity collidedEntity = collision.collisionEntity; //get collided entity

        /* Do Player Collisions */
        if(thisType.type == TypeComponent.PLAYER)
        {
            //if the player has a collision with an entity
            if(collidedEntity != null)
            {
                //get the collided entity type component
                TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

                //if the the collided entity has a type
                if(type != null)
                {
                    switch(type.type)
                    {
                        case TypeComponent.ENEMY:
                            System.out.println("player hit enemy");
                            //Do things
                            break;

                        case TypeComponent.SCENERY:
                            System.out.println("player hit scenery");
                            break;

                        case TypeComponent.OTHER:
                            System.out.println("player hit other");
                            break;
                    }

                    //remove the collided entity stored in the collision component of the entity having a collision
                    collision.collisionEntity = null;
                }
            }
        }



        /* do enemy collision */
        if(thisType.type == TypeComponent.ENEMY)
        {
            //if the enemy has a collision with an entity
            if(collidedEntity != null)
            {
                //get the enemy component of the entity
                //EnemyComponent enemy = entity.getComponent(EnemyComponent.class);
                EnemyComponent enemy = em.get(entity);

                //get the collided entity type component
                TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

                //if the the collided entity has a type
                if(type != null)
                {
                    switch (type.type)
                    {
                        case TypeComponent.SCENERY:
                            System.out.println("enemy hit scenery");
                            enemy.collision = true;
                            break;

                        case TypeComponent.PLAYER:
                            System.out.println("enemy hit player");
                            enemy.collision = true;
                            break;

                        case TypeComponent.ENEMY:
                            System.out.println("enemy hit enemy");
                            enemy.collision = true;
                            break;
                    }

                    //remove the collided entity stored in the collision component of the entity having a collision
                    collision.collisionEntity = null;
                }
            }
        }
    }
}