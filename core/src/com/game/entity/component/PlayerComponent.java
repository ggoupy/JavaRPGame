package com.game.entity.component;

import com.badlogic.ashley.core.Component;
import com.game.utils.Bar;
import com.game.utils.Timer;

public class PlayerComponent implements Component {

    public String name = "";
    public String spec = "";
    public int level = 1;
    public Bar xpBar = null;
    public Bar life = null;
    public Bar action = null; //Call it differently
    public float speed = 1;
    public float damage = 0;
    public float attackPoints = 15; //action points that consume 1 attack
    public Timer attackDuration = null;
    public boolean isAttacking = false;
    public float lifeRegeneration = 1;
    public float actionRegeneration = 1;
    public float lastDamageDuration = 0; //timer
    public EnemyComponent lastKilled = null;
}

