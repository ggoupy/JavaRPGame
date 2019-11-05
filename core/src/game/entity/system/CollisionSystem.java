package game.entity.system;

import com.badlogic.gdx.utils.Array;
import game.entity.component.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import static game.entity.utils.Mappers.*;


public class CollisionSystem  extends IteratingSystem {

    public CollisionSystem()
    {
        //create an collision system for all entities containing a collision component
        super(Family.all(CollisionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        CollisionComponent collision = collisionMapper.get(entity); //get the entity collision component
        TypeComponent thisType = entity.getComponent(TypeComponent.class); //get the entity type component

        Array<Entity> collidedEntities = collision.collisionEntity; //get array of collided entities

        /* Do Player Collisions */
        //if the player has a collision with an entity
        if(thisType.type == TypeComponent.PLAYER && collidedEntities.size != 0)
        {
            //get the player entity
            PlayerComponent player = playerMapper.get(entity);

            for(Entity collidedEntity : collidedEntities)
            {
                //get the collided entity type component
                TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

                //if the the collided entity has a type
                if(type != null)
                {
                    if (type.type == TypeComponent.ENEMY)
                    {
                        EnemyComponent enemyAttacking = enemyMapper.get(collidedEntity);
                        if (enemyAttacking.aggressive) //aggressive enemy attacking player at collision
                        {
                            player.life.updateCurrent(-enemyAttacking.damage);
                            player.lastDamageDuration = 0;
                        }
                    }
                }
            }
        }


        /* do enemy collision */
        //if the enemy has a collision with an entity
        if(thisType.type == TypeComponent.ENEMY)
        {
            //get the enemy component of the entity
            EnemyComponent enemy = enemyMapper.get(entity);

            enemy.attacking = false; //set to false every frame, and change it to true in case later

            if (collidedEntities.size != 0) //if it has a collision with one or more entities
            {
                for (Entity collidedEntity : collidedEntities)
                {
                    //get the collided entity type component
                    TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

                    //if the the collided entity has a type
                    if (type != null) {
                        if (type.type == TypeComponent.PLAYER) {
                            PlayerComponent player = collidedEntity.getComponent(PlayerComponent.class);
                            ReceiveAttackComponent receiveAttack = entity.getComponent(ReceiveAttackComponent.class);
                            if (player.isAttacking) {
                                if (!receiveAttack.hasReceivedAttack(collidedEntity)) {
                                    receiveAttack.receiveAttack(collidedEntity, player.attackDuration.getMax());
                                    enemy.life.updateCurrent(-player.damage);
                                    enemy.aggressive = true; //if he receives an attack, he becomes aggressive
                                }
                            }

                            if (enemy.aggressive) enemy.attacking = true; //the enemy attacks when he collides with the player
                        }

                        //If collision with scenery
                        //We treat the collision normally
                        if (type.type == TypeComponent.SCENERY) {
                            enemy.collision = true; //so that he can change his direction
                            collision.removeCollidedEntity(collidedEntity);
                        }
                    }
                }
            }
        }
    }
}