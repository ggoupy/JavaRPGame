package game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.entity.component.PlayerComponent;
import game.loader.AssetsManager;
import game.screen.hud.HUD;

public class PlayerHUDSystem extends EntitySystem {

    private final SpriteBatch spriteBatch;
    private final HUD hud;

    public PlayerHUDSystem(SpriteBatch sb, AssetsManager a, PlayerComponent p)
    {
        this.spriteBatch = sb;
        this.hud = new HUD(a, spriteBatch, p);
    }

    @Override
    public void update(float delta) {
        spriteBatch.setProjectionMatrix(hud.getCamera().combined);
        hud.update();
        hud.draw();
    }
}
