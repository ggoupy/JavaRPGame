package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.AppPreferences;
import game.GDXGame;
import game.loader.AssetsManager;


public class PreferenceScreen implements Screen {

    private GDXGame game;
    private AppPreferences gamePreferences;
    private Stage stage;
    private TextField movingLeftTf;
    private TextField movingRightTf;
    private TextField movingUpTf;
    private TextField movingDownTf;


    public PreferenceScreen(GDXGame g)
    {
        game = g;
        gamePreferences = game.getPreferences();
    }

    @Override
    public void show()
    {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        final Skin skin = game.assetsManager.getMenuSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //Background image
        Texture background = game.assetsManager.manager.get(AssetsManager.background);
        table.background(new TextureRegionDrawable(background));

        //Keys selection
        Label movingUpLb = new Label("moving up", skin);
        Label movingLeftLb = new Label("moving left", skin);
        Label movingDownLb = new Label("moving down", skin);
        Label movingRightLb = new Label("moving right", skin);
        movingUpTf = new TextField(gamePreferences.getMovingUpKey(), skin);
        movingUpTf.setMaxLength(1); movingUpTf.setAlignment(Align.center);
        movingLeftTf = new TextField(gamePreferences.getMovingLeftKey(), skin);
        movingLeftTf.setMaxLength(1); movingLeftTf.setAlignment(Align.center);
        movingDownTf = new TextField(gamePreferences.getMovingDownKey(), skin);
        movingDownTf.setMaxLength(1); movingDownTf.setAlignment(Align.center);
        movingRightTf = new TextField(gamePreferences.getMovingRightKey(), skin);
        movingRightTf.setMaxLength(1); movingRightTf.setAlignment(Align.center);

        table.add(movingUpLb).right().padRight(30);
        table.add(movingUpTf).center();
        table.row();

        table.add(movingLeftLb).right().padRight(30);
        table.add(movingLeftTf).center();
        table.row();

        table.add(movingDownLb).right().padRight(30);
        table.add(movingDownTf).center();
        table.row();

        table.add(movingRightLb).right().padRight(30);
        table.add(movingRightTf).center();
        table.row();

        //Save button
        TextButton saveBtn = new TextButton("Save", skin);
        saveBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String saveResponse = saveChanges();
                if (saveResponse.equals("OK")) //all OK
                {
                    game.changeScreen(GDXGame.MENU_SCREEN);
                }
                else
                {
                    Dialog errAlert = new Dialog("Error", skin);
                    errAlert.text(saveResponse).pad(60).row();
                    errAlert.button("Got it!").right();
                    errAlert.show(stage);
                }
            }
        });
        table.add(saveBtn).padTop(100).colspan(10).right(); //colspan 10 = trick to set the col taking all the row
    }

    @Override
    public void render(float delta)
    {
        //Clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }


    private String saveChanges()
    {
        //Key Up
        char keyUp = movingUpTf.getText().charAt(0);
        if (!Character.isLetter(keyUp)) return "Could not assign this key for moving up";

        //Key Down
        char keyDown = movingDownTf.getText().charAt(0);
        if (!Character.isLetter(keyDown)) return "Could not assign this key for moving down";

        //Key Left
        char keyLeft = movingLeftTf.getText().charAt(0);
        if (!Character.isLetter(keyLeft)) return "Could not assign this key for moving left";

        //Key Right
        char keyRight = movingRightTf.getText().charAt(0);
        if (!Character.isLetter(keyRight)) return "Could not assign this key for moving right";

        gamePreferences.setMovingUpKey(Character.toString(keyUp).toUpperCase());
        gamePreferences.setMovingDownKey(Character.toString(keyDown).toUpperCase());
        gamePreferences.setMovingLeftKey(Character.toString(keyLeft).toUpperCase());
        gamePreferences.setMovingRightKey(Character.toString(keyRight).toUpperCase());

        return "OK";
    }


    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {stage.dispose();}
}
