package com.game.entity.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.entity.component.*;
import com.game.entity.factory.EntityFactory;
import com.game.utils.Constants;

import static com.game.entity.utils.Mappers.*;
import static com.game.utils.Constants.*;

public class EnemyHealthSystem extends IteratingSystem {

    private EntityFactory entityFactory; //reference to entity factory

    public EnemyHealthSystem(EntityFactory entityFactory) {
        super(Family.all(EnemyHealthComponent.class, AttachedComponent.class).get());

        this.entityFactory = entityFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //Health bar components
        EnemyHealthComponent health = enemyHealthMapper.get(entity);
        TransformComponent position = transformMapper.get(entity);
        AttachedComponent attached = attachedMapper.get(entity);
        TextureComponent tex = textureMapper.get(entity);

        //Enemy components
        TransformComponent enemyPos = transformMapper.get(attached.attachedEntity);
        EnemyComponent enemy = enemyMapper.get(attached.attachedEntity);
        ReceiveAttackComponent enemyAttacker = receiveAttackMapper.get(attached.attachedEntity);


        //Death test
        //Make it better in the future
        if (enemy.life.isEmpty()) {
            Entity lastAttacking = enemyAttacker.lastAttacking;

            /* Player experience */
            PlayerComponent player = playerMapper.get(lastAttacking);
            if (player != null)
            {
                player.xpBar.updateCurrent(enemy.xpGain);

                /* To manage Quest */
                player.lastKilled = enemy;
            }

            entityFactory.destroyEntity(entity);
            return;
        }

        //color of the health bar for passive enemy
        //texture region2 contains the aggressive nature health bar type (passive in this case)
        //texture region3 contains the other health bar type (aggressive in this case)
        //We need to create new texture region because bug if not
        //--- not very efficient ---//
        if (!enemy.aggressiveNature) {
            if (enemy.aggressive) //if currently aggressive
            {
                tex.region = new TextureRegion(tex.region3);
            } else //else currently passive
            {
                tex.region = new TextureRegion(tex.region2);
            }
        }

        //Update the enemy health bar
        float percentLife = enemy.life.getCurrent() / enemy.life.getMax();
        tex.region.setRegionWidth((int) (health.size.width * percentLife));
        position.position.x = enemyPos.position.x;
        position.position.y = enemyPos.position.y + 0.6f;
    }
}
