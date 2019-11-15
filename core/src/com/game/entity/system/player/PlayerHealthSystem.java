package com.game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.game.GDXGame;
import com.game.entity.component.PlayerComponent;
import com.game.entity.factory.EntityFactory;

import static com.game.entity.utils.Mappers.playerMapper;


public class PlayerHealthSystem extends IteratingSystem {

    private GDXGame game;
    private EntityFactory entityFactory;

    public PlayerHealthSystem(GDXGame g, EntityFactory entityFactory) {
        super(Family.all(PlayerComponent.class).get());

        this.game = g;
        this.entityFactory = entityFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);

        if (player.life.isEmpty()) {
            entityFactory.killPlayer(entity);
        }

        if (player.lastDamageDuration > 3) {
            player.life.updateCurrent(player.lifeRegeneration, true); //current do not > max
        } else {
            player.lastDamageDuration += deltaTime;
        }

        //Here for the moment (would change later)
        //Update the action bar of the player
        player.action.updateCurrent(player.actionRegeneration, true); //current do not > max
    }
}
