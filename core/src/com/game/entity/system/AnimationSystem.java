package com.game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.entity.component.AnimationComponent;
import com.game.entity.component.StateComponent;
import com.game.entity.component.TextureComponent;

import static com.game.entity.utils.Mappers.*;


public class AnimationSystem extends IteratingSystem {

    public AnimationSystem() {
        //create an animation system for all entities containing a
        //texture , animation and state component
        super(Family.all(TextureComponent.class,
                AnimationComponent.class,
                StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        AnimationComponent ani = animationMapper.get(entity); //get the animation component of the entity
        StateComponent state = stateMapper.get(entity); //get the state component of the entity

        //Check if there is an animation in the AnimationComponent array of the entity
        //for the key of the current statement of this entity
        if (ani.animations.containsKey(state.get())) {
            //get the texture component of the entity
            TextureComponent tex = textureMapper.get(entity);
            //update the texture region of the entity with the current frame (according to the time passed)
            tex.region = (TextureRegion) ani.animations.get(state.get()).getKeyFrame(state.time, state.isLooping);
            if (state.horizontalFlip && !tex.region.isFlipX()) tex.region.flip(true, false);
            if (!state.horizontalFlip && tex.region.isFlipX()) tex.region.flip(true, false);
        }

        //update the time of the entity state component
        state.time += deltaTime;
    }
}
