package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.IntMap;

public class AnimationComponent implements Component {
    //storing animations <Int,Animation>
    //IntMap performs very fast get, containsKey and remove method
    public IntMap<Animation> animations = new IntMap<Animation>();
}
