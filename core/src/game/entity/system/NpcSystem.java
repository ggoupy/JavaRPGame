package game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.entity.component.*;
import game.screen.ui.UserInterface;
import game.controller.InputsControllerGame;
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
        //when the contact end to set the show quest panel not visible in the UI
        if (collision.hasCollidedEntity(player))
        {
            //We get the first completed quest
            Quest quest = questCom.getCompletedQuest();

            //If there is no completed quests
            if (quest == null)
            {
                //we get the first not accepted quest
                quest = questCom.getNewQuest();
            }


            //To show the quest in the UI, if we found a completed or not accepted quest
            if (quest != null)
            {
                ui.setQuest(quest); //Quest to show in UI

                //Interact quest key
                if (controller.acceptQuest)
                {
                    controller.acceptQuest = false; //manually set to false the key

                    QuestComponent playerQuests = questMapper.get(player); //quest component of the player

                    //Case of new quest -> Player accepts it
                    if (!quest.isCompleted())
                    {
                        playerQuests.quests.add(quest); //add the quest to the player quest component
                        quest.setAccepted(); //set the quest to accepted by the player
                    }

                    //Case of completed case -> Player returns it
                    else
                    {
                        PlayerComponent playerCom = playerMapper.get(player); //Player component

                        //We add the XP of the quest to player
                        playerCom.xpBar.updateCurrent(quest.getXP());

                        //We remove the quest from the NPC and the player
                        questCom.quests.removeValue(quest, true);
                        playerQuests.quests.removeValue(quest, true);
                    }
                }
            }
        }


        //Quest icon
        Entity iconEntity = attachedMapper.get(entity).attachedEntity; //Icon entity attached to NPC
        TextureComponent iconTexture = textureMapper.get(iconEntity);
        TransformComponent iconPos = transformMapper.get(iconEntity);

        iconPos.isHidden = true; //Firstly hidden

        //We set the icon according to current quests
        //If the NPC has a quest completed by player
        if(questCom.getCompletedQuest() != null)
        {
            iconTexture.region = iconTexture.region3;
            iconPos.isHidden = false;
        }
        //If the NPC has a quest not accepted by player
        else if (questCom.getNewQuest() != null)
        {
            iconTexture.region = iconTexture.region2;
            iconPos.isHidden = false;
        }
    }
}
