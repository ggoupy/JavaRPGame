package com.game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.game.entity.component.ReceiveAttackComponent;
import com.game.entity.utils.EntityAttacking;

import static com.game.entity.utils.Mappers.receiveAttackMapper;


public class ReceiveAttackSystem extends IteratingSystem {

    public ReceiveAttackSystem() {
        super(Family.all(ReceiveAttackComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ReceiveAttackComponent receiveAttackCom = receiveAttackMapper.get(entity);

        for (EntityAttacking entityAttacking : receiveAttackCom.entitiesAttacking) {
            if (entityAttacking.attackDuration.update(deltaTime)) {
                receiveAttackCom.lastAttacking = entityAttacking.entity;
                receiveAttackCom.entitiesAttacking.removeValue(entityAttacking, true);
            }
        }
    }
}
