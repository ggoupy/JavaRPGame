package game.entity.component;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {

    //int values representing different statements
    public static final int STANDING = 0; //manage standing to left and right
    public static final int STANDING_UP = 1;
    public static final int STANDING_DOWN = 2;
    public static final int MOVING = 3; //manage moving to left and right
    public static final int MOVING_UP = 4;
    public static final int MOVING_DOWN = 5;
    public static final int ATTACKING = 6;
    public static final int ATTACKING_UP = 7;
    public static final int ATTACKING_DOWN = 9;

    private int state = 0; //current statement (=STANDING)
    public float time = 0.0f; //time in this statement
    public boolean isLooping = false; //for animations
    public boolean horizontalFlip = false; //Flip the sprite for horizontal animation (left/right)

    public void set(int newState){
        state = newState;
        time = 0.0f;
    }

    public int get(){
        return state;
    }
}
