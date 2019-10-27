package game.entity.component;

import com.badlogic.ashley.core.Entity;
import game.utils.Timer;

public class EntityAttacking {
    public Entity entity;
    public Timer attackDuration;

    public EntityAttacking(Entity e, Timer t)
    {
        this.entity = e;
        this.attackDuration = t;
    }
}
