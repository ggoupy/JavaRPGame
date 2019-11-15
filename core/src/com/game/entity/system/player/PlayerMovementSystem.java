package com.game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.game.controller.InputsControllerGame;
import com.game.entity.component.BodyComponent;
import com.game.entity.component.PlayerComponent;
import com.game.entity.component.StateComponent;

import static com.game.entity.utils.Mappers.*;


public class PlayerMovementSystem extends IteratingSystem {

    private InputsControllerGame controller;

    public PlayerMovementSystem(InputsControllerGame keyCon) {
        //We create a player control system for all entities containing a player component
        super(Family.all(PlayerComponent.class).get());
        //We get the class managing user inputs
        controller = keyCon;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent body = bodyMapper.get(entity); //get the body component of the entity
        StateComponent state = stateMapper.get(entity); //get the state component of the entity
        PlayerComponent player = playerMapper.get(entity); //get the player component of the entity

        //Set the player statement
        setStatement(body, state, player);

        //Update velocity in x or y according to user inputs
        setVelocity(body, state, player);
    }


    //Update the statement of the player
    //We use to update the statement only if he is not already in this state as process entity function is called in loop
    public void setStatement(BodyComponent body, StateComponent state, PlayerComponent player) {
        //Moving state
        //we update the moving state only if we make the player moving by himself and not doing another action
        //to prevent state changes by other forces in the world (enemy collision for example)
        if (isMovingKeyPressed() && !player.isAttacking) {
            //Check the velocity in x and y to update the current state of the entity
            //We update the state only if he is already not currently in this state (for animations purpose)
            //The OR condition allows to have a vertical moving statement during collisions which set the player velocity to 0
            if ((body.body.getLinearVelocity().y > 0 || controller.up) && state.get() != StateComponent.MOVING_UP)
                state.set(StateComponent.MOVING_UP);
            if ((body.body.getLinearVelocity().y < 0 || controller.down) && state.get() != StateComponent.MOVING_DOWN)
                state.set(StateComponent.MOVING_DOWN);

            //We set a horizontal moving only if he is not already moving up or down
            //In order to manage diagonal movement (with up and down animations)
            if (state.get() != StateComponent.MOVING_UP && state.get() != StateComponent.MOVING_DOWN) {
                if (body.body.getLinearVelocity().x != 0 && state.get() != StateComponent.MOVING)
                    state.set(StateComponent.MOVING);
            }
        }

        //Standing state
        if (body.body.getLinearVelocity().x == 0 && state.get() == StateComponent.MOVING)
            state.set(StateComponent.STANDING);
        if (body.body.getLinearVelocity().y == 0 && state.get() == StateComponent.MOVING_UP)
            state.set(StateComponent.STANDING_UP);
        if (body.body.getLinearVelocity().y == 0 && state.get() == StateComponent.MOVING_DOWN)
            state.set(StateComponent.STANDING_DOWN);
    }


    //Set the velocity of the body according to the current player statement
    public void setVelocity(BodyComponent body, StateComponent state, PlayerComponent player) {
        if (controller.left) {
            state.horizontalFlip = true;
            body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, -player.speed, 0.2f),
                    body.body.getLinearVelocity().y);
        }
        if (controller.right) {
            state.horizontalFlip = false;
            body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, player.speed, 0.2f),
                    body.body.getLinearVelocity().y);
        }
        if (!controller.left && !controller.right) {
            body.body.setLinearVelocity(0, body.body.getLinearVelocity().y);
        }

        if (controller.up) {
            state.horizontalFlip = false;
            body.body.setLinearVelocity(body.body.getLinearVelocity().x,
                    MathUtils.lerp(body.body.getLinearVelocity().y, player.speed, 0.2f));
        }
        if (controller.down) {
            state.horizontalFlip = false;
            body.body.setLinearVelocity(body.body.getLinearVelocity().x,
                    MathUtils.lerp(body.body.getLinearVelocity().y, -player.speed, 0.2f));
        }
        if (!controller.up && !controller.down) {
            body.body.setLinearVelocity(body.body.getLinearVelocity().x, 0);
        }
    }


    //Return true if a moving key is pressed (upd,left,down,right)
    public boolean isMovingKeyPressed() {
        return (controller.down || controller.left || controller.right || controller.up);
    }
}