package game.entity.system.enemy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.EnemyComponent;
import game.entity.component.PlayerComponent;

public class EnemyAttackSystem extends IteratingSystem {

    public EnemyAttackSystem()
    {
        super(Family.all(EnemyComponent.class, PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        //I actually don't know how to manage it for the moment
    }
}
