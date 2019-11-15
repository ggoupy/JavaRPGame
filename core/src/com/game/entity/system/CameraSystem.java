package com.game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.game.entity.component.PlayerComponent;
import com.game.entity.component.TransformComponent;

import static com.game.entity.utils.Mappers.transformMapper;


public class CameraSystem extends IteratingSystem {

    private OrthographicCamera cameraBox2D;
    private OrthographicCamera cameraUI;

    public CameraSystem(OrthographicCamera cameraBox2D, OrthographicCamera cameraUI) {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());
        this.cameraBox2D = cameraBox2D; //to draw stuff that uses metres (in Box2D world)
        this.cameraUI = cameraUI; //to draw stuff that uses pixels
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent playerPosition = transformMapper.get(entity);
        cameraBox2D.position.x = playerPosition.position.x;
        cameraBox2D.position.y = playerPosition.position.y;
        cameraBox2D.update(); //to draw stuff using meters
        cameraUI.update(); //to draw stuff using pixels
    }
}
