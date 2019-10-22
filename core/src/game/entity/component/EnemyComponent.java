package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class EnemyComponent implements Component {
    public boolean isDead = false;
    public Vector2 origin = null; //origin position
    public int life = 100;
    public float speed = 0.5f;
    public boolean aggressive = false; // if the enemy is aggressive
    public boolean aggro = false; // if the enemy has aggro player
    public double movingTime = 2; // nb of seconds letting the enemy moving
    public double standingTime = 2; //nb of seconds letting the enemy standing
    public double currentTime = standingTime; //nb of seconds in the current action (moving or standing)
    public boolean collision = false; //handle collisions with scenery
    public Vector2 direction = new Vector2(0,0); //direction of the enemy
    // (to avoid collision with static body which set body velocity to 0 and so can't get the last velocity)
}
