package com.game.screen.ui;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.game.GDXGame;
import com.game.controller.InputsControllerGame;
import com.game.loader.AssetsManager;
import com.game.quest.Quest;
import com.game.screen.GameScreen;
import com.game.utils.Constants;

import static com.game.entity.utils.Mappers.playerMapper;

public class UserInterface {

    private GDXGame game;
    private GameScreen gameScreen;
    private InputsControllerGame controller;
    private AssetsManager assetsManager;
    private PooledEngine engine;
    private Batch spriteBatch;
    private Stage stage;
    private Table stageTable;

    private Skin skin;
    private Label.LabelStyle textStyle;
    private Label.LabelStyle titleStyle;

    /* HUD */
    private HUD hud;

    /* QUEST UI */
    private Table questTable;
    private Label questTitle;
    private Label questDescription;
    private Label questAccept;
    private boolean showQuest = false;

    /* QUEST MENU UI */
    private Dialog questMenuDialog;
    private Label questMenuTitle;
    private Label questMenuDescription;
    private Label questMenuObjective;
    private boolean showQuestMenu = false;

    /* QUIT DIALOG UI */
    private Dialog quitDialog;
    private boolean showQuitDialog = false;


    public UserInterface(GDXGame g, GameScreen gameScreen) {
        this.game = g;
        this.gameScreen = gameScreen;
        this.controller = gameScreen.getController();
        this.assetsManager = gameScreen.getAssetsManager();
        this.engine = gameScreen.getEngine();
        this.spriteBatch = gameScreen.getSpriteBatch();

        this.hud = new HUD(assetsManager, spriteBatch, playerMapper.get(gameScreen.getPlayerEntity()));

        stage = new Stage(gameScreen.getUIViewport());
        stageTable = new Table();
        stageTable.setFillParent(true);
        stage.addActor(stageTable);

        skin = assetsManager.getMenuSkin();
        titleStyle = new Label.LabelStyle(assetsManager.createTitleFont(), Color.WHITE);
        textStyle = new Label.LabelStyle(assetsManager.createTextFont(), Color.WHITE);

        /* QUEST MENU UI */
        createQuestMenuUI();

        /* QUEST UI */
        createQuestUI();

        /* QUIT DIALOG UI */
        createQuitDialogUI();
    }

    public void updateUI() {
        setUIComponents();
        drawUI();
    }

    private void drawUI() {
        //We always draw the player HUD
        drawHUD();

        //We draw the UI only if at least one component needs to be draw
        if (showQuest || showQuestMenu || showQuitDialog) {
            stage.act();
            stage.draw();
        }
    }

    private void drawHUD() {
        //Set the projection matrix to screen view
        spriteBatch.setProjectionMatrix(hud.getCamera().combined);

        // Update HUD actors
        hud.update();
        hud.act();

        // Draw the HUD stage
        hud.draw();
    }

    private void setUIComponents() {
        showQuest = false;
        questTable.setVisible(false);

        showQuestMenu = false;
        questMenuDialog.setVisible(false);

        showQuitDialog = false;
        quitDialog.setVisible(false);

        if (controller.map) {
            controller.resetKeys(); //we force keys to be false (keyUp) to avoid problems in next screen
            game.changeScreen(GDXGame.MAP_SCREEN);
        }

        if (controller.quitGame) {
            showQuitDialog = true;
            quitDialog.setVisible(true);
            //Set a input processor using stage processor + function declared below
            //To change in the future but for the moment only need to look at escape Key
            Gdx.input.setInputProcessor(new InputMultiplexer(stage) {
                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.ESCAPE) {
                        controller.setToCurrentController(); //Game Controller
                        controller.quitGame = false;
                        quitDialog.setVisible(false);
                    }
                    return false;
                }
            });
        }

        if (hasQuest()) {
            showQuest = true;
            questTable.setVisible(true);
        }

        if (controller.showQuests) {
            showQuestMenu = true;
            questMenuDialog.setVisible(true);
        }

        //Resize update
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }


    public void setQuest(Quest quest) {
        questTitle.setText(quest.getTitle());
        questDescription.setText(quest.getObjective().getDescription());
        if (quest.isCompleted()) questAccept.setText("Press ENTER to return the quest");
        else questAccept.setText("Press ENTER to accept the quest");
    }

    private void removeQuest() {
        questTitle.setText("");
        questDescription.setText("");
    }

    private boolean hasQuest() {
        return questTitle.getText().length > 0;
    }


    public void addQuestToMenuQuest(Quest quest) {
        questMenuTitle.setText(quest.getTitle());
        questMenuDescription.setText(quest.getObjective().getDescription());
        questMenuObjective.setText(quest.objectiveToString());
    }

    public void removeQuestToMenuQuest() {
        questMenuTitle.setText("You don't have any quests");
        questMenuDescription.setText("Accept a quest to have a mission");
        questMenuObjective.setText("");
    }


    private void createQuestUI() {
        // Get Quest background
        Texture questBg = assetsManager.manager.get(AssetsManager.background_quest);

        questTable = new Table();
        questTable.setBackground(new TextureRegionDrawable(questBg));

        questTitle = new Label("", titleStyle);
        questDescription = new Label("", textStyle);
        questDescription.setWrap(true);
        questAccept = new Label("Press ENTER to accept the quest", textStyle);
        questTable.add(questTitle).pad(100, 50, 0, 50).row();
        questTable.add(questDescription).width(Constants.G_WIDTH / 3f).height(Constants.G_HEIGHT / 4f).pad(70).top().row();
        questTable.add(questAccept).padTop(10);
        questTable.top();

        //Padding
        float questPadY = Constants.G_HEIGHT / 6f;
        stageTable.pad(questPadY, 0, questPadY, 60).right().top();

        stageTable.add(questTable).growY();
    }


    private void createQuestMenuUI() {
        // Get Quest background
        Texture questBg = assetsManager.manager.get(AssetsManager.background_quest);

        questMenuDialog = new Dialog("", skin);
        questMenuDialog.setBackground(new TextureRegionDrawable(questBg));

        questMenuTitle = new Label("You don't have any quests", titleStyle);
        questMenuDescription = new Label("Accept a quest to have a mission", textStyle);
        questMenuDescription.setWrap(true);
        questMenuObjective = new Label("", textStyle);

        questMenuDialog.row();
        questMenuDialog.add(questMenuTitle).pad(80, 50, 0, 50).center().row();
        questMenuDialog.add(questMenuDescription).width(Constants.G_WIDTH / 4f).height(Constants.G_HEIGHT / 5f).pad(50).center().row();
        questMenuDialog.add(questMenuObjective).pad(0, 50, 80, 50).center().row();

        questMenuDialog.show(stage);
    }


    private void createQuitDialogUI() {
        quitDialog = new Dialog("", skin);
        quitDialog.row();


        Label text = new Label("You really want to go back to the main menu?", skin);
        quitDialog.add(text).pad(30).colspan(2);

        TextButton noButton = new TextButton("No", skin);
        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setToCurrentController(); //Game Controller
                controller.quitGame = false;
                quitDialog.setVisible(false);
            }
        });
        quitDialog.row();
        quitDialog.add(noButton).left();

        TextButton yesButton = new TextButton("Yes", skin);
        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.quitGame = false;
                game.changeScreen(GDXGame.MENU_SCREEN);
            }
        });
        quitDialog.add(yesButton).right();
        quitDialog.show(stage);
    }


    public void clearUIComponents() {
        removeQuest();
    }
}
