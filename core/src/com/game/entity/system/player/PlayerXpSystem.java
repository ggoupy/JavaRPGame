package com.game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.game.entity.component.PlayerComponent;
import com.game.entity.factory.EntityFactory;
import com.game.loader.SoundsManager;

import static com.game.entity.utils.Mappers.playerMapper;


public class PlayerXpSystem extends IteratingSystem {

    private EntityFactory entityFactory;
    private SoundsManager soundsManager;

    public PlayerXpSystem(EntityFactory entityFactory, SoundsManager soundsManager) {
        super(Family.all(PlayerComponent.class).get());

        this.entityFactory = entityFactory;
        this.soundsManager = soundsManager;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity); //get player component for the entity
        if (player.xpBar.isFull()) //if player xpBar is completed
        {
            soundsManager.playEffect("level-up");
            playerUP(player);
        }
    }

    private void playerUP(PlayerComponent player) {
        player.level++; //update player level
        player.life.setMax(player.life.getMax() * 1.1f, true);
        player.action.setMax(player.action.getMax() * 1.03f, true);
        player.damage = player.damage * 1.1f;

        float xpSurplus = player.xpBar.getCurrent() - player.xpBar.getMax(); //xp points over the max
        xpSurplus = xpSurplus > 0 ? xpSurplus : 0; //assert that is > 0
        player.xpBar.setCurrent(xpSurplus); //init xp bar for next level
        player.xpBar.setMax(entityFactory.getHeroXpForLevel(player.level)); //update xp needed for next level
    }
}
