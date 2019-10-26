package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.BodyComponent;
import game.entity.component.PlayerComponent;
import game.entity.component.TransformComponent;
import game.entity.component.TypeComponent;


//This system only update z axe of entities transform components
//Z axe is used to sort textures in the game, in order to render one before the other
//We set the Z axe = to the Y axe so more a texture is down in the screen ("more close")
//More it will be render after, to respect perspective
public class PerspectiveSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> tm;

    public PerspectiveSystem()
    {
        super(Family.all(TransformComponent.class, TypeComponent.class).get());
        tm = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        TransformComponent position = tm.get(entity);
        position.position.z = position.position.y;
    }
}
