package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.BodyComponent;
import game.entity.component.EnemyComponent;

public class EnemySystem extends IteratingSystem {

    private ComponentMapper<EnemyComponent>  em;
    private ComponentMapper<BodyComponent> bm;

    public EnemySystem() {
        super(Family.all(EnemyComponent.class).get());

        em = ComponentMapper.getFor(EnemyComponent.class);
        bm = ComponentMapper.getFor(BodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent enemyCom = em.get(entity); // get EnemyComponent
        BodyComponent bodyCom = bm.get(entity); // get B2dBodyComponent

        //Doing nothing for the moment
    }
}
