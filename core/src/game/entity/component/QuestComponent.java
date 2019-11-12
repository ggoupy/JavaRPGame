package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import game.quest.Quest;

public class QuestComponent implements Component {
    public Array<Quest> quests = new Array<>();

    //Return the first quest completed in the array or null
    public Quest getCompletedQuest()
    {
        for (Quest quest : quests)
        {
            if (quest.isCompleted()) return quest;
        }

        return null;
    }

    //Return the first not accepted quest in the array or null
    public Quest getNewQuest()
    {
        for (Quest quest : quests)
        {
            if (!quest.isAccepted()) return quest;
        }

        return null;
    }
}