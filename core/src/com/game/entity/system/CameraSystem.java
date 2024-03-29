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



        //TO DO LATER : FOR SMOOTH CAMERA MOVEMENT -> LERP
        //THIS CODE IS SHAKING, TRY WITH NEW POSITION OF PLAYER INSTEAD OF LAST? CHANGE ORDER BETWEEN CAMERA AND PLAYER MOVEMENT SYSTEM?
        //ADD A IF STATEMENT TO DONT LERP IF PLAYER NOT MOVING? USE INTERPOLATED POS OF PLAYER?...
        //Vector3 v = new Vector3(playerPosition.position.x, playerPosition.position.y, 0);
        //Vector3 v2 = new Vector3(cameraBox2D.position.x, cameraBox2D.position.y, 0);
        //Vector3 v3 = v2.lerp(v, deltaTime);
        //cameraBox2D.position.set(v3);
    }
}
