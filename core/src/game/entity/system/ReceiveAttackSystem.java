package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.utils.EntityAttacking;
import game.entity.component.ReceiveAttackComponent;
import static game.entity.utils.Mappers.*;


public class ReceiveAttackSystem extends IteratingSystem {

    public ReceiveAttackSystem()
    {
        super(Family.all(ReceiveAttackComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        ReceiveAttackComponent receiveAttackCom = receiveAttackMapper.get(entity);

        for(EntityAttacking entityAttacking : receiveAttackCom.entitiesAttacking)
        {
            if (entityAttacking.attackDuration.update(deltaTime))
            {
                receiveAttackCom.entitiesAttacking.removeValue(entityAttacking,true);
            }
        }
    }
}
