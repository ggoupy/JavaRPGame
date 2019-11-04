package game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class FontComponent implements Component {
    //To display a font, we need to convert its world position into screen position
    public Vector3 worldPos = null;
    public Vector3 screenPos = null;
    public String text = null;
}
