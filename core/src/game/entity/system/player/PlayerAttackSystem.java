package game.entity.system.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.controller.InputsController;
import game.entity.component.PlayerComponent;
import game.entity.component.StateComponent;
import static game.entity.utils.Mappers.*;


public class PlayerAttackSystem extends IteratingSystem {

    private InputsController controller;

    public PlayerAttackSystem(InputsController keyCon)
    {
        super(Family.all(PlayerComponent.class).get());
        controller = keyCon;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        PlayerComponent player = playerMapper.get(entity);
        StateComponent state = stateMapper.get(entity);

        //set the attack statement if the player is pressing the attack key and not already attacking
        if (controller.attack_key && player.isAttacking == false)
        {
            player.isAttacking = true;
            int last_state = state.get();

            if(last_state == StateComponent.MOVING || last_state == StateComponent.STANDING)
            {
                state.set(StateComponent.ATTACKING);
            }

            if(last_state == StateComponent.MOVING_UP || last_state == StateComponent.STANDING_UP)
            {
                state.set(StateComponent.ATTACKING_UP);
            }

            if(last_state == StateComponent.MOVING_DOWN || last_state == StateComponent.STANDING_DOWN)
            {
                state.set(StateComponent.ATTACKING_DOWN);
            }
        }

        //update the attack duration timer if the player is attacking
        if (player.isAttacking == true)
        {
            //time of animation attack
            //Update timer function returns a boolean, true if the attack duration has been reached
            if (player.attackDuration.update(deltaTime))
            {
                player.isAttacking = false;
                player.attackDuration.reset();
                setNewState(state);
            }
        }
    }

    public void setNewState(StateComponent stateCom)
    {
        int state = stateCom.get();
        if (state == StateComponent.ATTACKING) stateCom.set(StateComponent.STANDING);
        if (state == StateComponent.ATTACKING_UP) stateCom.set(StateComponent.STANDING_UP);
        if (state == StateComponent.ATTACKING_DOWN) stateCom.set(StateComponent.STANDING_DOWN);
    }
}
