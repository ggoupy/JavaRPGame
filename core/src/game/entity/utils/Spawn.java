package game.entity.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;

public class Spawn {
    public Rectangle spawnPos;
    public Entity user;
    public boolean taken;
    public Spawn(Rectangle r, boolean t)
    {
        this.spawnPos = r;
        this.taken = t;
        user = null;
    }
}
