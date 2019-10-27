package game.entity.system;

import game.entity.component.*;
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
        //if the player has a collision with an entity
        if(thisType.type == TypeComponent.PLAYER && collidedEntity != null)
        {
            //get the player entity
            PlayerComponent player = pm.get(entity);

            //get the collided entity type component
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

            //if the the collided entity has a type
            if(type != null)
            {
                if (type.type == TypeComponent.ENEMY)
                {
                    player.life.updateCurrent(-0.1f);
                }
            }
        }


        /* do enemy collision */
        //if the enemy has a collision with an entity
        if(thisType.type == TypeComponent.ENEMY && collidedEntity != null)
        {
            //get the enemy component of the entity
            EnemyComponent enemy = em.get(entity);

            //get the collided entity type component
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

            //if the the collided entity has a type
            if(type != null)
            {
                if (type.type == TypeComponent.PLAYER)
                {
                    PlayerComponent player = collidedEntity.getComponent(PlayerComponent.class);
                    ReceiveAttackComponent receiveAttack = entity.getComponent(ReceiveAttackComponent.class);
                    if (player.isAttacking)
                    {
                        if (!receiveAttack.hasReceivedAttack(collidedEntity))
                        {
                            enemy.life.updateCurrent(-player.damage);
                            receiveAttack.receiveAttack(collidedEntity, player.attackDuration.getMax());
                        }
                    }
                }
                //If collision with scenery
                //We treat the collision normally
                if (type.type == TypeComponent.SCENERY)
                {
                    enemy.collision = true; //so that he can change his direction
                    collision.collisionEntity = null; //there is no need to keep the collided entity
                }
            }
        }
    }
}