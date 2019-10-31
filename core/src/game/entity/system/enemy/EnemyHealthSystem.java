package game.entity.system.enemy;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.*;
import game.entity.factory.EntityFactory;
import static game.entity.utils.Mappers.*;

public class EnemyHealthSystem extends IteratingSystem {

    private static EntityFactory entityFactory; //reference to entity factory

    public EnemyHealthSystem(EntityFactory entityFactory)
    {
        super(Family.all(EnemyHealthComponent.class, AttachedComponent.class).get());

        this.entityFactory = entityFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        //Health bar components
        EnemyHealthComponent health = enemyHealthMapper.get(entity);
        TransformComponent position = transformMapper.get(entity);
        AttachedComponent attached = attachedMapper.get(entity);

        //Enemy components
        TransformComponent enemyPos = attached.attachedEntity.getComponent(TransformComponent.class);
        EnemyComponent enemy = attached.attachedEntity.getComponent(EnemyComponent.class);

        //Death test
        if (enemy.life.getCurrent() <= 0)
        {
            entityFactory.destroyEntity(entity);
            return;
        }

        //Update the enemy health bar
        float percentLife = enemy.life.getCurrent() / enemy.life.getMax();
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        tex.region.setRegionWidth((int) (health.size.width * percentLife));
        position.position.x = enemyPos.position.x;
        position.position.y = enemyPos.position.y + 0.5f;
    }
}
