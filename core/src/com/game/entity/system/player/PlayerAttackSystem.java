package com.game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.game.controller.InputsControllerGame;
import com.game.entity.component.PlayerComponent;
import com.game.entity.component.StateComponent;
import com.game.loader.SoundsManager;

import static com.game.entity.utils.Mappers.playerMapper;
import static com.game.entity.utils.Mappers.stateMapper;


public class PlayerAttackSystem extends IteratingSystem {

    private InputsControllerGame controller;
    private SoundsManager soundsManager;

    public PlayerAttackSystem(InputsControllerGame keyCon, SoundsManager soundsManager) {
        super(Family.all(PlayerComponent.class).get());
        this.controller = keyCon;
        this.soundsManager = soundsManager;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);
        StateComponent state = stateMapper.get(entity);

        //set the attack statement if the player is pressing the attack key,
        //not already attacking and has enough action points
        if (controller.att1 && !player.isAttacking && player.action.getCurrent() >= player.attackPoints) {
            soundsManager.playEffect("hero-attack");
            player.isAttacking = true;
            int last_state = state.get();

            if (last_state == StateComponent.MOVING || last_state == StateComponent.STANDING) {
                state.set(StateComponent.ATTACKING);
            }

            if (last_state == StateComponent.MOVING_UP || last_state == StateComponent.STANDING_UP) {
                state.set(StateComponent.ATTACKING_UP);
            }

            if (last_state == StateComponent.MOVING_DOWN || last_state == StateComponent.STANDING_DOWN) {
                state.set(StateComponent.ATTACKING_DOWN);
            }

            player.action.updateCurrent(-player.attackPoints);
        }

        //update the attack duration timer if the player is attacking
        if (player.isAttacking) {
            //time of animation attack
            //Update timer function returns a boolean, true if the attack duration has been reached
            if (player.attackDuration.update(deltaTime)) {
                player.isAttacking = false;
                player.attackDuration.reset();
                setNewState(state);
            }
        }
    }

    private void setNewState(StateComponent stateCom) {
        int state = stateCom.get();
        if (state == StateComponent.ATTACKING) stateCom.set(StateComponent.STANDING);
        if (state == StateComponent.ATTACKING_UP) stateCom.set(StateComponent.STANDING_UP);
        if (state == StateComponent.ATTACKING_DOWN) stateCom.set(StateComponent.STANDING_DOWN);
    }
}
