package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import game.quest.Quest;

public class QuestComponent implements Component {
    public Array<Quest> quests = null;
}
