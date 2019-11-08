package game.entity.system.player;

import com.badlogic.ashley.core.EntitySystem;
import game.GDXGame;
import game.controller.InputsController;

public class PlayerUISystem extends EntitySystem {

    private GDXGame game;
    private InputsController controller;

    public PlayerUISystem(GDXGame game, InputsController controller)
    {
        this.game = game;
        this.controller = controller;
    }

    @Override
    public void update(float deltaTime)
    {
        if (controller.map_key)
        {
            game.changeScreen(GDXGame.MAP_SCREEN);
        }
    }
}
