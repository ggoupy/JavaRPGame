package com.game.entity.utils;

import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

import static com.game.entity.utils.Mappers.transformMapper;


public class ZComparator implements Comparator<Entity> {

    @Override
    public int compare(Entity entityA, Entity entityB) {
        float az = transformMapper.get(entityA).position.z;
        float bz = transformMapper.get(entityB).position.z;
        int res = 0;

        if (az > bz) res = -1;
        if (az < bz) res = 1;

        return res;
    }
}