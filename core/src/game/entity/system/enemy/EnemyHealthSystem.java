package game.entity.system.enemy;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import game.entity.component.*;
import game.utils.Constants;

public class EnemyHealthSystem extends IteratingSystem {

    private ComponentMapper<EnemyHealthComponent> hm;
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<AttachedComponent> am;

    private PooledEngine engine; //reference to the game engine
    private World world; //reference to game world

    public EnemyHealthSystem(PooledEngine engine, World world)
    {
        super(Family.all(EnemyHealthComponent.class, AttachedComponent.class).get());

        this.engine = engine;
        this.world = world;

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

        //Death test
        if (enemy.life.getCurrent() <= 0)
        {
            world.destroyBody(attached.attachedEntity.getComponent(BodyComponent.class).body);
            engine.removeEntity(attached.attachedEntity); //enemy entity
            engine.removeEntity(entity); //enemy health bar entity
        }

        float percentLife = enemy.life.getCurrent() / enemy.life.getMax();

        TextureComponent tex = entity.getComponent(TextureComponent.class);

        tex.region.setRegionWidth((int) (health.size.width * percentLife));

        position.position.x = enemyPos.position.x;
        position.position.y = enemyPos.position.y + 0.5f;
    }
}
