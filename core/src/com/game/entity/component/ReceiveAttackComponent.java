package com.game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.game.entity.utils.EntityAttacking;
import com.game.utils.Timer;


public class ReceiveAttackComponent implements Component {
    public Array<EntityAttacking> entitiesAttacking = new Array<>();
    public Entity lastAttacking = null;


    public void receiveAttack(Entity e, double duration) {
        entitiesAttacking.add(new EntityAttacking(e, new Timer(duration)));
        lastAttacking = e;
    }

    public boolean hasReceivedAttack(Entity e) {
        for (EntityAttacking entity : entitiesAttacking) {
            if (entity.entity.equals(e)) return true;
        }
        return false;
    }
}