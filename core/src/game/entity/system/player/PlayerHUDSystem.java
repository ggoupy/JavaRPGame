package game.entity.system.player;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.entity.component.PlayerComponent;
import game.loader.AssetsManager;
import game.screen.hud.HUD;

public class PlayerHUDSystem extends EntitySystem {

    private final SpriteBatch spriteBatch;
    private final AssetsManager assetsManager;
    private HUD hud;

    public PlayerHUDSystem(SpriteBatch sb, AssetsManager a, PlayerComponent p)
    {
        this.spriteBatch = sb;
        this.assetsManager = a;
        createHUD(p);
    }

    @Override
    public void update(float delta) {
        spriteBatch.setProjectionMatrix(hud.getCamera().combined);
        hud.update(); // Update HUD actors
        hud.act(); // For the xp progress bar
        hud.draw(); // Draw the HUD stage
    }

    public void createHUD(PlayerComponent p)
    {
        hud = new HUD(assetsManager, spriteBatch, p);
    }

    public void dispose()
    {
        hud.dispose();
    }
}
