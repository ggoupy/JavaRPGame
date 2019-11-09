package game.entity.system.player;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.GDXGame;
import game.controller.InputsControllerGame;
import game.loader.AssetsManager;
import game.utils.Constants;

public class PlayerUISystem extends EntitySystem {

    private GDXGame game;
    private InputsControllerGame controller;
    private Stage stage;

    public PlayerUISystem(GDXGame game, InputsControllerGame controller)
    {
        this.game = game;
        this.controller = controller;

        createStage();
    }

    public void createStage()
    {
        this.stage = new Stage(new ScreenViewport());

        Skin skin = game.assetsManager.getMenuSkin();


        Dialog dialog = new Dialog("", game.assetsManager.getMenuSkin());
        dialog.row();

        Label text = new Label("You really want to go back to the main menu?", skin);
        dialog.add(text).pad(30).colspan(3); //colspan(3) is a trick to make the label taking all space (bug to center it) (3>nb of cells)

        TextButton noButton = new TextButton("No", skin);
        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setToCurrentController();
                controller.quitGame = false;
            }
        });
        dialog.row();
        dialog.add(noButton).left();

        TextButton yesButton = new TextButton("Yes", skin);
        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.quitGame = false;
                game.changeScreen(GDXGame.MENU_SCREEN);
            }
        });
        dialog.add(yesButton).right();

        dialog.show(stage);
    }

    @Override
    public void update(float deltaTime)
    {
        if (controller.map)
        {
            controller.resetKeys(); //we force keys to be false (keyUp) to avoid problems in next screen
            game.changeScreen(GDXGame.MAP_SCREEN);
        }

        if (controller.quitGame)
        {
            Gdx.input.setInputProcessor(stage);
            stage.act(Math.min(deltaTime, 1 / 30f));
            stage.draw();
        }
    }
}
