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

public class EnemySystem extends IteratingSystem {

    private ComponentMapper<EnemyComponent>  em;
    private ComponentMapper<BodyComponent> bm;
    private ComponentMapper<StateComponent> sm;

    private Random rand;

    public EnemySystem() {
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
        float x=0,y = 0;


        //new action (moving or standing)
        if (enemy.currentTime <= 0)
        {
            //we check the last action of the enemy (before currentTime reached 0)
            if (isMoving(state.get()))
            {
                moving = false; //then will not move
                enemy.currentTime = enemy.standingTime;
                //watch out to have it before the velocity setter to 0 to get the last velocity
                setStandingState(state, body.body.getLinearVelocity().x, body.body.getLinearVelocity().y);
                body.body.setLinearVelocity( 0,  0);
            }
            else
            {
                moving = true; //then will move
                enemy.currentTime = enemy.movingTime;
                Vector2 s = getRandSpeed(enemy.speed);
                x = s.x + body.body.getLinearVelocity().x;
                y = s.y + body.body.getLinearVelocity().y;
                setMovingState(state,x,y);
            }
        }

        if (moving)
        {
            body.body.setLinearVelocity( x,  y);
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
    private void setMovingState(StateComponent state, float x, float y)
    {
        if (y > 0) {state.set(StateComponent.MOVING_UP); return;}
        if (y < 0) {state.set(StateComponent.MOVING_DOWN); return;}
        if (x != 0)
        {
            state.set(StateComponent.MOVING);
            if (x < 0) state.horizontalFlip = true;
            else state.horizontalFlip = false;
            return;
        }
        //if x && y = 0 it keeps the previous state
    }

    //set the state according to last velocity
    private void setStandingState(StateComponent state, float x, float y)
    {
        if (y > 0) {state.set(StateComponent.STANDING_UP); return;}
        if (y < 0) {state.set(StateComponent.STANDING_DOWN); return;}
        if (x != 0) {state.set(StateComponent.STANDING); return;}
        //if x && y = 0 it keeps the previous state
    }
}
