package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import game.utils.Timer;


public class ReceiveAttackComponent implements Component {
    public Array<EntityAttacking> entitiesAttacking;


    public void receiveAttack(Entity e, double duration)
    {
        entitiesAttacking.add(new EntityAttacking(e,new Timer(duration)));
    }

    public boolean hasReceivedAttack(Entity e)
    {
        for (EntityAttacking entity : entitiesAttacking)
        {
            if (entity.entity.equals(e)) return true;
        }
        return false;
    }
}