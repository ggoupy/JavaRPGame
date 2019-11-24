package com.game.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.game.entity.component.PlayerComponent;
import com.game.loader.AssetsManager;
import com.game.utils.Constants;


public class HUD extends Stage {

    private PlayerComponent player;
    private Label levelLb;
    private Image healthBar;
    private Image actionBar;
    private ProgressBar xpBar;
    private float hud_width;
    private float hud_height;

    public HUD(AssetsManager assetsManager, Batch batch, PlayerComponent p) {
        super(new FitViewport(Constants.G_WIDTH, Constants.G_HEIGHT), batch);

        //Player reference
        player = p;

        //Skins
        Skin menuSkin = assetsManager.getMenuSkin();
        Skin HUDSkin = assetsManager.getHUDSkin();

        //Table size
        hud_width = Constants.G_WIDTH / 6f;
        hud_height = Constants.G_HEIGHT / 9f;

        // Extract regions from the texture atlas.
        TextureAtlas atlas = assetsManager.getAtlas();
        AtlasRegion healthBarRegion = atlas.findRegion("health-bar");
        AtlasRegion actionBarRegion = atlas.findRegion("action-bar");

        // Get HUD background
        Texture background = assetsManager.manager.get(AssetsManager.background_hud);

        // Create a container storing our table
        Container<Table> hudContainer = new Container<>();
        hudContainer.setSize(hud_width * 1.2f, hud_height * 1.2f);
        hudContainer.setBackground(new TextureRegionDrawable(background));
        hudContainer.setPosition(50, Constants.G_HEIGHT - hudContainer.getHeight() * 1.2f);

        // Initialize health and action bars.
        healthBar = new Image(healthBarRegion);
        healthBar.setSize(hud_width, hud_width / 9);
        actionBar = new Image(actionBarRegion);
        actionBar.setSize(hud_width, hud_width / 9);

        // Initialize player name
        Label.LabelStyle nameLbStyle = new Label.LabelStyle(assetsManager.createTextFont(), Color.WHITE);
        Label nameLb = new Label(player.name, nameLbStyle);

        // Initialize the player level
        Label.LabelStyle levelLbStyle = new Label.LabelStyle(assetsManager.createTextFont(), Color.WHITE);
        levelLb = new Label("Level : " + player.level, levelLbStyle);


        // Create table for positioning
        Table barTable = new Table();
        barTable.add(nameLb).width(hud_width).padTop(10).left();
        barTable.row();
        barTable.add(levelLb).left();
        barTable.row().padTop(10);
        barTable.add(healthBar).left().width(hud_width).height(hud_height / 9);
        barTable.row().padTop(10);
        barTable.add(actionBar).left().width(hud_width).height(hud_height / 9).padBottom(20);
        barTable.row();

        hudContainer.setActor(barTable);
        addActor(hudContainer);

        //XP bar
        xpBar = new ProgressBar(0.0f, 1.0f, 0.01f, false, HUDSkin);
        xpBar.setValue(0.0f);
        xpBar.setAnimateDuration(0.35f);
        xpBar.setBounds(10, 0, Constants.G_WIDTH - 20, Constants.G_HEIGHT / 25f);
        addActor(xpBar);
    }

    public void update() {
        xpBar.setValue(player.xpBar.getCurrent() / player.xpBar.getMax());
        levelLb.setText("Level : " + player.level);
        float percentLife = (player.life.getCurrent() / player.life.getMax());
        float percentAction = (player.action.getCurrent() / player.action.getMax());
        healthBar.setWidth(hud_width * percentLife);
        actionBar.setWidth(hud_width * percentAction);
    }
}