package game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.GDXGame;
import game.entity.component.PlayerComponent;
import game.entity.factory.EntityFactory;
import game.screen.GameScreen;

import static game.entity.utils.Mappers.*;


public class PlayerHealthSystem extends IteratingSystem {

    private GDXGame game;
    private static EntityFactory entityFactory;

    public PlayerHealthSystem(GDXGame g, EntityFactory entityFactory)
    {
        super(Family.all(PlayerComponent.class).get());

        this.game = g;
        this.entityFactory = entityFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        PlayerComponent player = playerMapper.get(entity);

        if (player.life.getCurrent() <= 0)
        {
            entityFactory.destroyEntity(entity);
            game.changeScreen(GDXGame.ENDSCREEN);
            return;
        }

        if (player.lastDamageDuration > 3)
        {
            if (player.life.getCurrent()+player.lifeRegeneration <= player.life.getMax())
            {
                player.life.updateCurrent(player.lifeRegeneration);
            }
        }
        else
        {
            player.lastDamageDuration += deltaTime;
        }
    }
}