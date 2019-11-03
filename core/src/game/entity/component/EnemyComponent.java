package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.utils.Bar;

public class EnemyComponent implements Component {

    /* Common attributes */
    public Vector2 origin = null; //origin position
    public Vector2 direction = null; //direction of the enemy // (to avoid collision with static body which set body velocity to 0 and so can't get the last velocity)
    public boolean collision = false; //handle collisions with scenery
    public boolean aggro = false; // if the enemy has aggro player
    public boolean cant_aggro = false; //disable the aggro possibility

    /* Specific attributes */
    public Bar life = null;
    public int level = 1;
    public float speed = 0.5f;
    public float damage = 0.1f;
    public float xpGain = 50; //nb of xp that the player earn after killing it
    public boolean aggressive = false; // if the enemy is aggressive
    public float aggroRange = 3; //aggressive enemy aggro the player if he is inside its range
    public float movingRange = 4; //range the enemy can move around its spawn (in meters)
    public float movingTime = 2; // nb of seconds letting the enemy moving
    public float standingTime = 2; //nb of seconds letting the enemy standing
    public float currentTime = standingTime; //nb of seconds in the current action (moving or standing)
}
