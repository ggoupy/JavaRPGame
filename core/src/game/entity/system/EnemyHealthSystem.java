package game.entity.system;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import game.entity.component.*;
import game.utils.Constants;

public class EnemyHealthSystem extends IteratingSystem {

    private ComponentMapper<EnemyHealthComponent> hm;
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<AttachedComponent> am;

    public EnemyHealthSystem()
    {
        super(Family.all(EnemyHealthComponent.class, AttachedComponent.class).get());

        hm = ComponentMapper.getFor(EnemyHealthComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
        am = ComponentMapper.getFor(AttachedComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        EnemyHealthComponent health = hm.get(entity);
        TransformComponent position = tm.get(entity);
        AttachedComponent attached = am.get(entity);

        TransformComponent enemyPos = attached.attachedEntity.getComponent(TransformComponent.class);
        EnemyComponent enemy = attached.attachedEntity.getComponent(EnemyComponent.class);

        float percentLife = enemy.life.getCurrent() / enemy.life.getMax();

        TextureComponent tex = entity.getComponent(TextureComponent.class);

        tex.region.setRegionWidth((int) (health.size.width * percentLife));

        position.position.x = enemyPos.position.x;
        position.position.y = enemyPos.position.y + 0.5f;
    }
}
