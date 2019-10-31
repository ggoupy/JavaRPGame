package game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import game.entity.component.TransformComponent;
import game.entity.component.PlayerComponent;
import static game.entity.utils.Mappers.*;


public class CameraSystem extends IteratingSystem {

    private OrthographicCamera camera;

    public CameraSystem(OrthographicCamera camera) {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        TransformComponent position = transformMapper.get(entity);
        camera.position.x = position.position.x;
        camera.position.y = position.position.y;
    }
}
