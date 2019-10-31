package game.entity.component;

import com.badlogic.ashley.core.Component;
import game.utils.Bar;
import game.utils.Timer;

public class PlayerComponent implements Component {

    public String name = "";
    public String spec = "";
    public double level = 1;
    public Bar life = null;
    public Bar action = null; //Call it differently
    public float speed = 1;
    public int damage = 0;
    public Timer attackDuration = null;
    public boolean isAttacking = false;
    public float lifeRegeneration = 1;
    public float lastDamageDuration = 0;
}

