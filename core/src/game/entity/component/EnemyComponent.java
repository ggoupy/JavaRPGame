package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.utils.Bar;

public class EnemyComponent implements Component {
    public boolean isDead = false;
    public Vector2 origin = null; //origin position
    public Bar life = new Bar(100);
    public float speed = 0.5f;
    public double movingTime = 2; // nb of seconds letting the enemy moving
    public double standingTime = 2; //nb of seconds letting the enemy standing
    public double currentTime = standingTime; //nb of seconds in the current action (moving or standing)
    public boolean collision = false; //handle collisions with scenery
    public Vector2 direction = new Vector2(0,0); //direction of the enemy
    // (to avoid collision with static body which set body velocity to 0 and so can't get the last velocity)
    public float moving_range = 4; //range the enemy can move around its spawn (in meters)
    public boolean aggressive = false; // if the enemy is aggressive
    public boolean aggro = false; // if the enemy has aggro player
    public boolean cant_aggro = false; //disable the aggro possibility
    public float aggro_range = 4; //aggressive enemy aggro the player if he is inside its range
    public float xpGain = 0.5f; //nb of xp that the player earn after killing it
}
