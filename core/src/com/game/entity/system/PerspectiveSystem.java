package com.game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.game.entity.component.TransformComponent;
import com.game.entity.component.TypeComponent;

import static com.game.entity.utils.Mappers.transformMapper;


//This system only update z axe of entities transform components
//Z axe is used to sort textures in the game, in order to render one before the other
//We set the Z axe = to the Y axe so more a texture is down in the screen ("more close")
//More it will be render after, to respect perspective
public class PerspectiveSystem extends IteratingSystem {

    public PerspectiveSystem() {
        super(Family.all(TransformComponent.class, TypeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent position = transformMapper.get(entity);
        position.position.z = position.position.y;
    }
}
