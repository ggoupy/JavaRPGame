package game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import game.entity.component.BodyComponent;
import game.entity.component.EnemyComponent;
import game.entity.component.StateComponent;

import java.util.Random;

public class EnemyMovementSystem extends IteratingSystem {

    private ComponentMapper<EnemyComponent>  em;
    private ComponentMapper<BodyComponent> bm;
    private ComponentMapper<StateComponent> sm;

    private Random rand;

    public EnemyMovementSystem() {
        super(Family.all(EnemyComponent.class).get());

        em = ComponentMapper.getFor(EnemyComponent.class);
        bm = ComponentMapper.getFor(BodyComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);

        rand = new Random();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent enemy = em.get(entity); //get the enemy component of the entity
        BodyComponent body = bm.get(entity); //get the body component of the entity
        StateComponent state = sm.get(entity); //get the state component of the entity

        boolean moving = false;

        //new action (moving or standing)
        if (enemy.currentTime <= 0)
        {
            //we check the last action of the enemy (before currentTime reached 0)
            if (isMoving(state.get()))
            {
                moving = false; //then will not move
                enemy.currentTime = enemy.standingTime;
                //watch out to have it before the velocity setter to 0 to get the last velocity
                setStandingState(state, enemy.direction);
                enemy.direction.set(0,0);
                body.body.setLinearVelocity(enemy.direction);
            }
            else
            {
                moving = true; //then will move
                enemy.currentTime = enemy.movingTime;
                Vector2 s = getRandSpeed(enemy.speed);
                enemy.direction.set(s.x, s.y);
                setMovingState(state, enemy.direction);
            }
        }

        if (moving)
        {
            body.body.setLinearVelocity(enemy.direction);
        }

        //check if an enemy is too far from its spawn and apply a velocity
        if (isTooFarFromSpawn(body.body.getPosition(), enemy.origin, enemy.moving_range))
        {
            //get the normalization of the vector going to spawn coordinates
            enemy.direction.x = enemy.origin.x - body.body.getPosition().x;
            enemy.direction.y = enemy.origin.y - body.body.getPosition().y;
            enemy.direction.nor();
            body.body.setLinearVelocity(enemy.direction);
            setMovingState(state, enemy.direction);
        }

        if(enemy.collision)
        {
            enemy.direction.set(-enemy.direction.x, -enemy.direction.y);
            body.body.setLinearVelocity(enemy.direction);
            setMovingState(state, enemy.direction);
            enemy.collision = false;
        }

        enemy.currentTime -= deltaTime;
    }


    //true if the enemy is currently moving
    private boolean isMoving(int state)
    {
        return (state == StateComponent.MOVING
                || state == StateComponent.MOVING_UP
                || state == StateComponent.MOVING_DOWN);
    }

    //return a random speed vector
    private Vector2 getRandSpeed(float speed)
    {
        float x = 0;
        switch (rand.nextInt(3)) //nb between 0 and 2
        {
            case 0: x = 0; break;
            case 1: x = speed; break;
            case 2: x = -speed; break;
        }

        float y = 0;
        switch (rand.nextInt(3)) //nb between 0 and 2
        {
            case 0: y = 0; break;
            case 1: y = speed; break;
            case 2: y = -speed; break;
        }

        return new Vector2(x,y);
    }

    //set the state according to velocity
    private void setMovingState(StateComponent state, Vector2 v)
    {
        if (v.y > 0) {state.set(StateComponent.MOVING_UP); return;}
        if (v.y < 0) {state.set(StateComponent.MOVING_DOWN); return;}
        if (v.x != 0)
        {
            state.set(StateComponent.MOVING);
            if (v.x < 0) state.horizontalFlip = true;
            else state.horizontalFlip = false;
            return;
        }
        //if x && y = 0 it keeps the previous state
    }

    //set the state according to last velocity
    private void setStandingState(StateComponent state, Vector2 v)
    {
        if (v.y < 0) {state.set(StateComponent.STANDING_DOWN); return;}
        if (v.y > 0) {state.set(StateComponent.STANDING_UP); return;}
        else {state.set(StateComponent.STANDING); return;}
    }

    //check if coordinates are too far from another according to a range (in world measure : meters)
    private boolean isTooFarFromSpawn(Vector2 position, Vector2 spawn, float range)
    {
        if ((position.x > spawn.x + range)
            || (position.x < spawn.x - range)
            || (position.y > spawn.y + range)
            || (position.y < spawn.y - range))
            return true;

        return false;
    }
}
