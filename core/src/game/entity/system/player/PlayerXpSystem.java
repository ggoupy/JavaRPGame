package game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.PlayerComponent;
import static game.entity.utils.Mappers.*;


public class PlayerXpSystem extends IteratingSystem {

    public PlayerXpSystem()
    {
        super(Family.all(PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        PlayerComponent player = playerMapper.get(entity); //get player component for the entity
        if (player.xpBar.isFull()) //if player xpBar is completed
        {
            playerUP(player);

            float xpSurplus = player.xpBar.getCurrent() - player.xpBar.getMax(); //xp points over the max
            xpSurplus = xpSurplus > 0 ? xpSurplus : 0; //assert that is > 0
            player.xpBar.setCurrent(xpSurplus); //init xp bar for next level
            player.xpBar.setMax(player.xpBar.getMax()*1.5f); //update xp needed for next level
        }
    }

    private void playerUP(PlayerComponent player)
    {
        player.level++; //update player level
        player.life.setMax(player.life.getMax()*1.1f,true);
        player.action.setMax(player.action.getMax()*1.1f,true);
        player.damage = player.damage * 1.1f;
    }
}
