package com.game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.game.entity.component.*;

import static com.game.entity.utils.Mappers.*;


public class CollisionSystem extends IteratingSystem {

    public CollisionSystem() {
        //create an collision system for all entities containing a collision component
        super(Family.all(CollisionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collision = collisionMapper.get(entity); //get the entity collision component
        TypeComponent thisType = entity.getComponent(TypeComponent.class); //get the entity type component

        Array<Entity> collidedEntities = collision.collisionEntity; //get array of collided entities

        /* Do Player Collisions */
        //if the player has a collision with an entity
        if (thisType.type == TypeComponent.PLAYER && collidedEntities.size != 0) {
            //get the player entity
            PlayerComponent player = playerMapper.get(entity);

            for (Entity collidedEntity : collidedEntities) {
                //get the collided entity type component
                TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

                //if the the collided entity has a type
                if (type != null) {
                    if (type.type == TypeComponent.ENEMY) {
                        EnemyComponent enemyAttacking = enemyMapper.get(collidedEntity);

                        if (enemyAttacking.aggressive) //aggressive enemy attacking player at collision
                        {
                            //Enemy damage + coeff according to player level
                            float enemyDamage = enemyAttacking.damage + ((enemyAttacking.level - player.level) * enemyAttacking.damage);
                            player.life.updateCurrent(-enemyDamage);
                            player.lastDamageDuration = 0;
                        }
                    }
                }
            }
        }


        /* do enemy collision */
        //if the enemy has a collision with an entity
        if (thisType.type == TypeComponent.ENEMY) {
            //get the enemy component of the entity
            EnemyComponent enemy = enemyMapper.get(entity);
            BodyComponent enemyBody = bodyMapper.get(entity);

            enemy.attacking = false; //set to false every frame, and change it later in case he attacks

            if (collidedEntities.size != 0) //if it has a collision with one or more entities
            {
                for (Entity collidedEntity : collidedEntities) {
                    //get the collided entity type component
                    TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

                    //if the the collided entity has a type
                    if (type != null) {

                        //If collision with scenery
                        //We treat the collision normally
                        //Enemy fixtures are sensors so they don't collide with the world
                        //To treat normally the collision with scenery, we need to set its sensor as false during collision
                        //After treating the collision we can set it back as sensor
                        if (type.type == TypeComponent.SCENERY) {
                            enemyBody.body.getFixtureList().get(0).setSensor(false); //We set it as a non sensor = has a body
                        }
                        else  enemyBody.body.getFixtureList().get(0).setSensor(true); //We reset it as a sensor


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

                            if (enemy.aggressive)
                                enemy.attacking = true; //the enemy attacks when he collides with the player
                        }
                    }
                }
            }
        }
    }
}