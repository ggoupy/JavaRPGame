package com.game.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class FontComponent implements Component {
    //To display a font, we need to convert its world position into screen position
    public Vector3 worldPos = new Vector3();
    public Vector3 screenPos = new Vector3();
    public String text = null;
}
