package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import game.entity.component.TransformComponent;
import game.entity.component.player.PlayerComponent;

public class CameraSystem extends IteratingSystem {

    private ComponentMapper<PlayerComponent> pm;
    private ComponentMapper<TransformComponent> tm;
    private OrthographicCamera camera;

    public CameraSystem(OrthographicCamera camera) {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        camera.position.x = entity.getComponent(TransformComponent.class).position.x;
        camera.position.y = entity.getComponent(TransformComponent.class).position.y;
    }
}
