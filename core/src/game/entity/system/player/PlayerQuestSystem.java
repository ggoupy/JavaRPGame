package game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.ObjectMap;
import game.UserInterface;
import game.entity.component.PlayerComponent;
import game.entity.component.QuestComponent;
import game.quest.KillingObjective;
import game.quest.Objective;
import game.quest.Quest;

import static game.entity.utils.Mappers.*;


public class PlayerQuestSystem extends IteratingSystem {

    private UserInterface ui;


    public PlayerQuestSystem(UserInterface ui)
    {
        super(Family.all(PlayerComponent.class, QuestComponent.class).get());

        this.ui = ui;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        PlayerComponent player = playerMapper.get(entity);
        QuestComponent questCom = questMapper.get(entity);

        if (questCom.quests != null && questCom.quests.size > 0)
        {
            Quest toShow = questCom.quests.get(0);
            if (toShow != null) ui.addQuestToMenuQuest(toShow);

            for (Quest quest : questCom.quests) {
                Objective obj = quest.getObjective();

                switch (obj.getType())
                {
                    case "kill":
                        KillingObjective killingObj = (KillingObjective) obj;
                        if (player.lastKilled!=null && !obj.isCompleted())
                        {
                            if (player.lastKilled.name.equals(killingObj.getEnemy()))
                            {
                                player.lastKilled = null;
                                obj.updateObjective();
                            }
                        }
                        break;

                    default: break;
                }
            }
        }


    }
}
