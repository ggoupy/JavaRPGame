package game.screen.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import game.entity.component.PlayerComponent;
import game.loader.AssetsManager;
import game.utils.Constants;


public class HUD extends Stage {

    private PlayerComponent player;
    private Image healthBar;
    private Image actionBar;
    float hud_width;
    float hud_height;

    public HUD(AssetsManager assetsManager, Batch batch, PlayerComponent p)
    {
        super(new FitViewport(Constants.G_WIDTH, Constants.G_HEIGHT), batch);

        player = p;

        //Table size
        hud_width = Constants.G_WIDTH / 6;
        hud_height = Constants.G_HEIGHT /  9;

        // Extract regions from the texture atlas.
        TextureAtlas atlas = assetsManager.getAtlas();
        AtlasRegion healthBarRegion = atlas.findRegion("health-bar");
        AtlasRegion actionBarRegion = atlas.findRegion("action-bar");

        // Get HUD background
        Texture background = assetsManager.manager.get(assetsManager.background_hud);

        // Create a container storing our table
        Container<Table> hudContainer = new Container<Table>();
        hudContainer.setSize(hud_width*1.2f, hud_height*1.2f);
        hudContainer.setBackground(new TextureRegionDrawable(background));
        hudContainer.setPosition(50, Constants.G_HEIGHT - hudContainer.getHeight() * 1.2f);

        // Initialize health and action bars.
        healthBar = new Image(healthBarRegion);
        healthBar.setSize(hud_width, hud_width/9);
        actionBar = new Image(actionBarRegion);
        actionBar.setSize(hud_width, hud_width/9);

        // Initialize player name
        Label nameLb = new Label(player.name, assetsManager.getSkin());

        // Create table for positioning
        Table barTable = new Table();
        barTable.add(nameLb).width(hud_width);
        barTable.row().padTop(10);
        barTable.add(healthBar).left().width(hud_width).height(hud_height/9);
        barTable.row().padTop(10);
        barTable.add(actionBar).left().width(hud_width).height(hud_height/9);
        barTable.row();

        hudContainer.setActor(barTable);
        addActor(hudContainer);
    }

    public void update()
    {
        float percentLife = (player.life.getCurrent()/player.life.getMax());
        float percentAction = (player.action.getCurrent()/player.action.getMax());
        healthBar.setWidth(hud_width*percentLife);
        actionBar.setWidth(hud_width*percentAction);
    }
}