package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.EntityAttacking;
import game.entity.component.ReceiveAttackComponent;

public class ReceiveAttackSystem extends IteratingSystem {

    private ComponentMapper<ReceiveAttackComponent> rm;


    public ReceiveAttackSystem()
    {
        super(Family.all(ReceiveAttackComponent.class).get());

        rm = ComponentMapper.getFor(ReceiveAttackComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        ReceiveAttackComponent receiveAttackCom = rm.get(entity);

        for(EntityAttacking entityAttacking : receiveAttackCom.entitiesAttacking)
        {
            if (entityAttacking.attackDuration.update(deltaTime))
            {
                receiveAttackCom.entitiesAttacking.removeValue(entityAttacking,true);
            }
        }
    }
}
