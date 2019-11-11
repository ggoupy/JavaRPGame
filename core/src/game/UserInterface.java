package game;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.controller.InputsControllerGame;
import game.loader.AssetsManager;
import game.quest.Quest;
import game.screen.GameScreen;
import game.screen.hud.HUD;
import game.utils.Constants;

import static game.entity.utils.Mappers.playerMapper;

public class UserInterface {

    private GDXGame game;
    private GameScreen gameScreen;
    private InputsControllerGame controller;
    private AssetsManager assetsManager;
    private PooledEngine engine;
    private Batch spriteBatch;
    private Stage stage;
    private Table stageTable;

    /* HUD */
    private HUD hud;

    /* QUEST UI */
    private Table questTable;
    private Label questTitle;
    private Label questDescription;
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


    public UserInterface(GDXGame g, GameScreen gameScreen)
    {
        this.game = g;
        this.gameScreen = gameScreen;
        this.controller = gameScreen.getController();
        this.assetsManager = gameScreen.getAssetsManager();
        this.engine = gameScreen.getEngine();
        this.spriteBatch = gameScreen.getSpriteBatch();

        this.hud = new HUD(assetsManager, spriteBatch, playerMapper.get(gameScreen.getPlayerEntity()));

        stage = new Stage(new ScreenViewport());
        stageTable = new Table();
        stageTable.setFillParent(true);
        stage.addActor(stageTable);

        stageTable.debugAll();
        stage.setDebugAll(true);

        Skin skin = assetsManager.getMenuSkin();

        /* QUEST MENU UI */
        createQuestMenuUI(skin);

        /* QUEST UI */
        createQuestUI(skin);

        /* QUIT DIALOG UI */
        createQuitDialogUI(skin);
    }

    public void updateUI()
    {
        setUIComponents();
        drawUI();
    }

    private void drawUI()
    {
        drawHUD();
        if (showQuest) stage.draw();
        if (showQuestMenu) stage.draw();
        if (showQuitDialog) stage.draw();
    }

    private void drawHUD()
    {
        //Set the projection matrix to screen view
        spriteBatch.setProjectionMatrix(hud.getCamera().combined);

        // Update HUD actors
        hud.update();
        hud.act();

        // Draw the HUD stage
        hud.draw();
    }

    private void setUIComponents()
    {
        showQuest = false;
        questTable.setVisible(false);

        showQuestMenu = false;
        questMenuDialog.setVisible(false);

        showQuitDialog = false;
        quitDialog.setVisible(false);

        if (controller.map)
        {
            controller.resetKeys(); //we force keys to be false (keyUp) to avoid problems in next screen
            game.changeScreen(GDXGame.MAP_SCREEN);
        }

        if (controller.quitGame)
        {
            showQuitDialog = true;
            quitDialog.setVisible(true);
            Gdx.input.setInputProcessor(stage);
        }

        if (hasQuest())
        {
            showQuest = true;
            questTable.setVisible(true);
        }

        if (controller.showQuests)
        {
            showQuestMenu = true;
            questMenuDialog.setVisible(true);
        }
    }


    public void setQuest(Quest quest)
    {
        questTitle.setText(quest.getTitle());
        questDescription.setText(quest.getObjective().getDescription());
    }

    public void removeQuest()
    {
        questTitle.setText("");
        questDescription.setText("");
    }

    private boolean hasQuest()
    {
        return questTitle.getText().length != 0;
    }


    public void addQuestToMenuQuest(Quest quest)
    {
        questMenuTitle.setText(quest.getTitle());
        questMenuDescription.setText(quest.getObjective().getDescription());
        questMenuObjective.setText(quest.objectiveToString());
    }

    public void removeQuestToMenuQuest()
    {
        questMenuTitle.setText("You don't have any quests");
        questMenuDescription.setText("Accept a quest to have a mission");
        questMenuObjective.setText("");
    }


    private void createQuestUI(Skin skin)
    {
        // Get Quest background
        Texture questBg = assetsManager.manager.get(AssetsManager.background_hud);

        questTable = new Table();
        questTable.setBackground(new TextureRegionDrawable(questBg));

        questTitle = new Label("", skin);
        questDescription = new Label("", skin);
        questDescription.setWrap(true);
        Label questAccept = new Label("Press ENTER to accept", skin);
        questTable.add(questTitle).pad(60).row();
        questTable.add(questDescription).width(Constants.G_WIDTH/3f).pad(50).row();
        questTable.add(questAccept).pad(60).row();

        //Padding
        float questPadY = Constants.G_HEIGHT/6f;
        stageTable.pad(questPadY,0,questPadY,60).right().top();

        stageTable.add(questTable).growY();
    }


    private void createQuestMenuUI(Skin skin)
    {
        // Get Quest background
        Texture questBg = assetsManager.manager.get(AssetsManager.background_hud);

        questMenuDialog = new Dialog("", skin);
        questMenuDialog.setBackground(new TextureRegionDrawable(questBg));

        Table questMenuTable = new Table();

        questMenuTitle = new Label("You don't have any quests", skin);
        questMenuDescription = new Label("Accept a quest to have a mission", skin);
        questMenuDescription.setWrap(true);
        questMenuObjective = new Label("", skin);

        questMenuTable.add(questMenuTitle).pad(80).row();
        questMenuTable.add(questMenuDescription).width(Constants.G_WIDTH/4f).pad(60).row();
        questMenuTable.add(questMenuObjective).pad(80).row();

        questMenuDialog.add(questMenuTable);
        questMenuDialog.show(stage);
    }


    private void createQuitDialogUI(Skin skin)
    {
        quitDialog = new Dialog("", skin);
        quitDialog.row();

        Label text = new Label("You really want to go back to the main menu?", skin);
        quitDialog.add(text).pad(30).colspan(3); //colspan(3) is a trick to make the label taking all space (bug to center it) (3>nb of cells)

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
}
