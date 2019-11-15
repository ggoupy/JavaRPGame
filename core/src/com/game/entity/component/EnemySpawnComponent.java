package com.game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.game.entity.utils.Spawn;
import com.game.utils.Timer;


public class EnemySpawnComponent implements Component {
    public String name = null;
    public Array<Spawn> spawns = new Array<>();
    public Timer RespawnTimer = null;
    public String enemyType = null; //type of enemies spawning there
    public int enemyLevel = 1; //level of enemies spawning there

    public void addSpawn(Rectangle r, boolean t) {
        spawns.add(new Spawn(r, t));
    }

    public int nbSpawns() {
        return spawns.size;
    }

    public int nbEnemies() {
        int count = 0;
        for (Spawn spawn : spawns) {
            if (spawn.taken) count++;
        }
        return count;
    }
}