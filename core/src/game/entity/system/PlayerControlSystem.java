package game.entity.system;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.controller.InputsController;
import game.entity.component.BodyComponent;
import game.entity.component.TextureComponent;
import game.entity.component.player.*;
import game.entity.component.StateComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import game.entity.component.player.PlayerComponent;

public class PlayerControlSystem extends IteratingSystem{

    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<BodyComponent> bodm;
    ComponentMapper<StateComponent> sm;

    InputsController controller;


    public PlayerControlSystem(InputsController keyCon)
    {
        //We create a player control system for all entities containing a player component
        super(Family.all(PlayerComponent.class).get());

        //We get the class managing user inputs
        controller = keyCon;

        //We create components mapper of the component class
        pm = ComponentMapper.getFor(PlayerComponent.class);
        bodm = ComponentMapper.getFor(BodyComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        BodyComponent body = bodm.get(entity); //get the body component of the entity
        StateComponent state = sm.get(entity); //get the state component of the entity

        //Check the velocity in x and y to update the current state of the entity
        //We update the state only if he is already not currently in this state (for animations purpose)
        if(body.body.getLinearVelocity().y>0 && state.get()!=StateComponent.MOVING_UP) state.set(StateComponent.MOVING_UP);
        if(body.body.getLinearVelocity().y==0 && state.get()==StateComponent.MOVING_UP) state.set(StateComponent.STANDING_UP);
        if(body.body.getLinearVelocity().y<0 && state.get()!=StateComponent.MOVING_DOWN) state.set(StateComponent.MOVING_DOWN);
        if(body.body.getLinearVelocity().y==0 && state.get()==StateComponent.MOVING_DOWN) state.set(StateComponent.STANDING_DOWN);

        //We set a horizontal moving only if he is not already moving up or down
        //In order to manage diagonal movement (with up and down animations)
        if(state.get() != StateComponent.MOVING_UP && state.get() != StateComponent.MOVING_DOWN)
        {
            if(body.body.getLinearVelocity().x!=0 && state.get()!=StateComponent.MOVING) state.set(StateComponent.MOVING);
        }
        if(body.body.getLinearVelocity().x==0 && state.get()==StateComponent.MOVING) state.set(StateComponent.STANDING);


        //Setting or removing velocity in x or y according to user inputs
        if(controller.left)
        {
            state.horizontalFlip = true;
            body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, -2f, 0.2f),
                                        body.body.getLinearVelocity().y);
        }
        if(controller.right)
        {
            state.horizontalFlip = false;
            body.body.setLinearVelocity(MathUtils.lerp(body.body.getLinearVelocity().x, 2f, 0.2f),
                                        body.body.getLinearVelocity().y);
        }
        if(!controller.left && ! controller.right)
        {
            body.body.setLinearVelocity(0, body.body.getLinearVelocity().y);
        }

        if(controller.up)
        {
            state.horizontalFlip = false;
            body.body.setLinearVelocity(body.body.getLinearVelocity().x,
                                        MathUtils.lerp(body.body.getLinearVelocity().y, 2f, 0.2f));
        }
        if(controller.down)
        {
            state.horizontalFlip = false;
            body.body.setLinearVelocity(body.body.getLinearVelocity().x,
                                        MathUtils.lerp(body.body.getLinearVelocity().y, -2f, 0.2f));
        }
        if(!controller.up && !controller.down)
        {
            body.body.setLinearVelocity(body.body.getLinearVelocity().x, 0);
        }
    }
}