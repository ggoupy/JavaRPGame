package game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.UserInterface;
import game.controller.InputsControllerGame;
import game.entity.component.CollisionComponent;
import game.entity.component.NpcComponent;
import game.entity.component.QuestComponent;
import game.quest.Quest;

import static game.entity.utils.Mappers.*;


public class NpcSystem extends IteratingSystem {

    private InputsControllerGame controller;
    private UserInterface ui;
    private Entity player;

    public NpcSystem(UserInterface ui, InputsControllerGame controller, Entity player)
    {
        super(Family.all(NpcComponent.class, QuestComponent.class).get());
        this.controller = controller;
        this.player = player;
        this.ui = ui;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        QuestComponent questCom = questMapper.get(entity);
        CollisionComponent collision = collisionMapper.get(entity);

        //We treat the collision here because we need to handle the moment
        //when the contact end to set showQuests to false
        if (collision.hasCollidedEntity(player))
        {
            Quest quest = null;

            for(int i=0; i < questCom.quests.size; ++i)
            {
                //we stop the loop when we find the first not terminated and accepted quest of the NPC
                if (!questCom.quests.get(i).isTerminated() && !questCom.quests.get(i).isAccepted())
                {
                    quest = questCom.quests.get(i);
                    break;
                }
            }

            if (quest != null)
            {
                ui.setQuest(quest); //Quest to show in UI

                if (controller.acceptQuest)
                {
                    controller.acceptQuest = false; //manually set to false the accept quest key
                    QuestComponent playerQuests = questMapper.get(player); //quest component of the player
                    playerQuests.quests.add(new Quest(quest.getTitle(), quest.getObjective())); //add the shown quest to the player quest component
                    quest.setAccepted(); //set the quest to accepted (to do not keep showing it)
                }
            }
            else ui.removeQuest(); //remove the last quest shown if no one encountered
        }
        else ui.removeQuest(); //No quest to show in UI
    }
}
