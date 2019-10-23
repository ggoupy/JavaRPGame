package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.EnemyComponent;
import game.entity.component.TypeComponent;
import game.entity.component.player.PlayerComponent;

public class EnemyAttackSystem extends IteratingSystem {

    private ComponentMapper<EnemyComponent> em;
    private ComponentMapper<PlayerComponent> pm;


    public EnemyAttackSystem()
    {
        super(Family.all(EnemyComponent.class, PlayerComponent.class).get());

        em = ComponentMapper.getFor(EnemyComponent.class);
        pm = ComponentMapper.getFor(PlayerComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        //I actually don't know how to manage it for the moment
    }
}
