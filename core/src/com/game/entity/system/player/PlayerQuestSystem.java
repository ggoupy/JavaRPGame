package com.game.entity.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.game.entity.component.EnemyComponent;
import com.game.entity.component.PlayerComponent;
import com.game.entity.component.QuestComponent;
import com.game.quest.KillingObjective;
import com.game.quest.Objective;
import com.game.quest.Quest;
import com.game.screen.ui.UserInterface;

import static com.game.entity.utils.Mappers.playerMapper;
import static com.game.entity.utils.Mappers.questMapper;


public class PlayerQuestSystem extends IteratingSystem {

    private UserInterface ui;


    public PlayerQuestSystem(UserInterface ui) {
        super(Family.all(PlayerComponent.class, QuestComponent.class).get());

        this.ui = ui;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);
        QuestComponent questCom = questMapper.get(entity);

        if (questCom.quests != null && questCom.quests.size > 0) {
            Quest toShow = questCom.quests.get(0);
            if (toShow != null) ui.addQuestToMenuQuest(toShow);

            for (Quest quest : questCom.quests) {
                Objective obj = quest.getObjective();

                switch (obj.getType()) {
                    case "kill":
                        KillingObjective killingObj = (KillingObjective) obj;
                        if (player.lastKilled != null && !obj.isCompleted()) {
                            EnemyComponent lastKilled = player.lastKilled;

                            //If the player kill an enemy with the same type at the same area of the quest
                            if (lastKilled.name.equals(killingObj.getEnemy())
                                    && obj.getAreas().contains(player.lastKilled.spawnName, false)) {
                                player.lastKilled = null;
                                obj.updateObjective();
                                if (obj.isCompleted()) quest.setCompleted();
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        } else ui.removeQuestToMenuQuest();


    }
}
