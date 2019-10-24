package game.entity.component;

import com.badlogic.ashley.core.Component;
import game.utils.Bar;

public class PlayerComponent implements Component {

    public String name;
    public double level;
    public Bar life;
    public Bar action; //Call it differently
    public float speed;
    public int damage;
    public String spec;
}

