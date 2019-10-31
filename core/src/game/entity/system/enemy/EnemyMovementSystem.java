package game.entity.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import game.entity.component.*;

import static game.entity.utils.Mappers.*;
import java.util.Random;


public class EnemyMovementSystem extends IteratingSystem {

    private Random rand;
    private PooledEngine engine; //reference to game engine

    public EnemyMovementSystem(PooledEngine engine)
    {
        super(Family.all(EnemyComponent.class).get());
        this.rand = new Random();
        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent enemy = enemyMapper.get(entity); //get the enemy component of the entity
        BodyComponent body = bodyMapper.get(entity); //get the body component of the entity
        StateComponent state = stateMapper.get(entity); //get the state component of the entity
        TransformComponent enemyPos = transformMapper.get(entity); //get the transform component of the entity


        /* For aggressive enemy which are able to aggro */
        if (enemy.aggressive && !enemy.cant_aggro)
        {
            //array of players
            ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

            //for each player
            for(Entity player : players)
            {
                //position of the player
                TransformComponent playerPos = transformMapper.get(player);

                //distance between enemy and player
                double distance = Math.hypot(enemyPos.position.x-playerPos.position.x,
                                             enemyPos.position.y-playerPos.position.y);

                //if distance between player and enemy is < to its aggro range
                //we set the direction of the enemy to player
                if (distance <= enemy.aggro_range)
                {
                    goTo(enemy, body, playerPos.position.x, playerPos.position.y);
                    setMovingState(state, enemy.direction);
                    enemy.aggro = true; //enemy is "aggro-ing"
                }
                else enemy.aggro = false;

                if (enemy.aggro == true) break; //stop the loop when found a player to aggro
            }
        }

        /* Treat other movements if the enemy is not already aggro-ing */
        if (!enemy.aggro)
        {
            //new action (moving or standing)
            if (enemy.currentTime <= 0)
            {
                //we check the last action of the enemy (before currentTime reached 0)
                if (isMoving(state.get()))
                {
                    enemy.currentTime = enemy.standingTime;
                    //watch out to have it before the velocity setter to 0 to get the last velocity
                    setStandingState(state, enemy.direction);
                    enemy.direction.set(0,0);
                }
                else
                {
                    enemy.currentTime = enemy.movingTime;
                    Vector2 s = getRandSpeed(enemy.speed);
                    enemy.direction.set(s.x, s.y);
                    setMovingState(state, enemy.direction);
                }
            }

            //velocity according to direction
            body.body.setLinearVelocity(enemy.direction);

            //check if an enemy is too far from its spawn and apply a velocity
            if (isTooFarFromSpawn(body.body.getPosition(), enemy.origin, enemy.moving_range))
            {
                goTo(enemy, body, enemy.origin);
                setMovingState(state, enemy.direction);
            }

            enemy.currentTime -= deltaTime;
        }
        else //enemy is aggro-ing
        {
            //check if it is too far from its spawn
            if (isTooFarFromSpawn(body.body.getPosition(), enemy.origin, 10)) //range adjusted to 10
            {
                goTo(enemy, body, enemy.origin);
                setMovingState(state, enemy.direction);
                enemy.aggro = false;
                enemy.cant_aggro = true; //make it unable to aggro player
            }
            else enemy.cant_aggro = false;
        }

        /* Treat enemy collision with static body */
        if(enemy.collision)
        {
            enemy.direction.set(-enemy.direction.x, -enemy.direction.y);
            body.body.setLinearVelocity(enemy.direction);
            setMovingState(state, enemy.direction);
            enemy.collision = false;
            enemy.aggro = false;
        }
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
        //get current state in order to do not assign a state if already in it
        int currentState = state.get();

        //horizontal movement if the velocity in x is not null and if hte velocity in y is not significant
        if (v.x != 0 && Math.abs(v.y) < 0.4)
        {
            if (currentState != StateComponent.MOVING) state.set(StateComponent.MOVING);
            if (v.x < 0) state.horizontalFlip = true;
            else state.horizontalFlip = false;
            return;
        }

        //vertical up movement
        if (v.y > 0)
        {
            if (currentState != StateComponent.MOVING_UP) state.set(StateComponent.MOVING_UP);
            return;
        }

        //vertical down movement
        if (v.y < 0)
        {
            if (currentState != StateComponent.MOVING_DOWN) state.set(StateComponent.MOVING_DOWN);
            return;
        }

        //if x && y = 0 it keeps the previous state
    }

    //set the state according to last velocity
    private void setStandingState(StateComponent state, Vector2 v)
    {
        //0.4 because under this number, y velocity is not significant and it would be a MOVING movement
        if (v.y < -0.4) {state.set(StateComponent.STANDING_DOWN); return;}
        if (v.y > 0.4) {state.set(StateComponent.STANDING_UP); return;}
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

    //apply a velocity pointing the toGo point in parameter
    private void goTo(EnemyComponent enemy, BodyComponent body, Vector2 toGo) {goTo(enemy,body,toGo.x,toGo.y);}
    private void goTo(EnemyComponent enemy, BodyComponent body, float x, float y)
    {
        //get the normalization of the vector going to origin coordinates
        enemy.direction.x = x - body.body.getPosition().x;
        enemy.direction.y = y - body.body.getPosition().y;
        enemy.direction.nor();
        body.body.setLinearVelocity(enemy.direction);
    }
}
