package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.AppPreferences;
import game.GDXGame;
import game.loader.AssetsManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PreferenceScreen implements Screen {

    private GDXGame game;
    private AppPreferences gamePreferences;
    private Stage stage;
    private TextField movingLeftTf;
    private TextField movingRightTf;
    private TextField movingUpTf;
    private TextField movingDownTf;
    private TextField attackTf;
    private TextField mapTf;
    private TextField questTf;


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
        table.background(new TextureRegionDrawable(background).tint(Color.GRAY));

        //Keys selection
        Label keyPreferencesLb = new Label ("Key preferences", skin);

        Label movingUpLb = new Label("moving up", skin);
        Label movingLeftLb = new Label("moving left", skin);
        Label movingDownLb = new Label("moving down", skin);
        Label movingRightLb = new Label("moving right", skin);
        Label attackLb = new Label("attack", skin);
        Label mapLb = new Label("open/close map", skin);
        Label questLb = new Label("open/close quests", skin);
        Label escapeLb = new Label("quit game", skin);

        movingUpTf = new TextField(gamePreferences.getMovingUpKey(), skin);
        movingUpTf.setMaxLength(1); movingUpTf.setAlignment(Align.center);
        movingLeftTf = new TextField(gamePreferences.getMovingLeftKey(), skin);
        movingLeftTf.setMaxLength(1); movingLeftTf.setAlignment(Align.center);
        movingDownTf = new TextField(gamePreferences.getMovingDownKey(), skin);
        movingDownTf.setMaxLength(1); movingDownTf.setAlignment(Align.center);
        movingRightTf = new TextField(gamePreferences.getMovingRightKey(), skin);
        movingRightTf.setMaxLength(1); movingRightTf.setAlignment(Align.center);
        attackTf = new TextField(gamePreferences.getAttack1Key(), skin);
        attackTf.setMaxLength(1); attackTf.setAlignment(Align.center);
        mapTf = new TextField(gamePreferences.getMapKey(), skin);
        mapTf.setMaxLength(1); mapTf.setAlignment(Align.center);
        questTf = new TextField(gamePreferences.getQuestKey(), skin);
        questTf.setMaxLength(1); questTf.setAlignment(Align.center);
        TextField escapeTf = new TextField("ESC", skin);
        escapeTf.setDisabled(true); escapeTf.setAlignment(Align.center);

        table.add(keyPreferencesLb).left().padBottom(50).row();

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

        table.add(attackLb).right().padRight(30);
        table.add(attackTf).center();
        table.row();

        table.add(mapLb).right().padRight(30);
        table.add(mapTf).center();
        table.row();

        table.add(questLb).right().padRight(30);
        table.add(questTf).center();
        table.row();

        table.add(escapeLb).right().padRight(30);
        table.add(escapeTf).center();
        table.row().padTop(40);


        Label mapInfo = new Label("To move the camera in the map screen:", skin);
        Label mapInfo1 = new Label("use player movement keys", skin);
        Label mapInfo2 = new Label("E to zoom and R to dezoom", skin);
        table.add(mapInfo).right().colspan(10).row(); //colspan(10) is a trick to make the label taking all cells
        table.add(mapInfo1).right().colspan(10).row();
        table.add(mapInfo2).right().colspan(10).row();

        table.row().padTop(100);

        //Quit button
        TextButton quitBtn = new TextButton("Quit", skin);
        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(GDXGame.MENU_SCREEN);
            }
        });
        table.add(quitBtn).left().padRight(50);

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
        table.add(saveBtn).right();
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


    //Try to save user changes in game preferences class
    //return "OK" if changes are saved, the error message if not
    //FACTORISE THIS FUNCTION LATER AND MAKE IT MORE EFFICIENT
    private String saveChanges()
    {
        //text field are storing string of one character, so we get it through charAt(0)

        //Key Up
        String movingUp = movingUpTf.getText();
        if (movingUp.equals("")) return "moving up does not have assigned key";
        char keyUp = movingUp.charAt(0);
        if (!Character.isLetter(keyUp)) return "Could not assign this key for moving up";

        //Key Down
        String movingDown = movingDownTf.getText();
        if (movingDown.equals("")) return "moving down does not have assigned key";
        char keyDown = movingDown.charAt(0);
        if (!Character.isLetter(keyDown)) return "Could not assign this key for moving down";

        //Key Left
        String movingLeft = movingLeftTf.getText();
        if (movingLeft.equals("")) return "moving left does not have assigned key";
        char keyLeft = movingLeft.charAt(0);
        if (!Character.isLetter(keyLeft)) return "Could not assign this key for moving left";

        //Key Right
        String movingRight = movingRightTf.getText();
        if (movingRight.equals("")) return "moving right does not have assigned key";
        char keyRight = movingRight.charAt(0);
        if (!Character.isLetter(keyRight)) return "Could not assign this key for moving right";

        //Attack 1
        String attack1 = attackTf.getText();
        if (attack1.equals("")) return "attack does not have assigned key";
        char keyAttack1 = attack1.charAt(0);
        if (!Character.isLetter(keyAttack1)) return "Could not assign this key for attack";

        //Map
        String map = mapTf.getText();
        if (map.equals("")) return "map does not have assigned key";
        char keyMap = map.charAt(0);
        if (!Character.isLetter(keyMap)) return "Could not assign this key for map";

        //Quests
        String quest = questTf.getText();
        if (quest.equals("")) return "quest does not have assigned key";
        char keyQuest = quest.charAt(0);
        if (!Character.isLetter(keyQuest)) return "Could not assign this key for quest";


        Set<String> keys = new HashSet<>(); //Contains unique values
        int nbKeys = 0;
        keys.add(movingUp.toUpperCase()); nbKeys++;
        keys.add(movingDown.toUpperCase()); nbKeys++;
        keys.add(movingLeft.toUpperCase()); nbKeys++;
        keys.add(movingRight.toUpperCase()); nbKeys++;
        keys.add(attack1.toUpperCase()); nbKeys++;
        keys.add(map.toUpperCase()); nbKeys++;
        keys.add(quest.toUpperCase()); nbKeys++;

        if (keys.size() < nbKeys) return "Two keys have the same value";

        gamePreferences.setMovingUpKey(Character.toString(keyUp).toUpperCase());
        gamePreferences.setMovingDownKey(Character.toString(keyDown).toUpperCase());
        gamePreferences.setMovingLeftKey(Character.toString(keyLeft).toUpperCase());
        gamePreferences.setMovingRightKey(Character.toString(keyRight).toUpperCase());
        gamePreferences.setAttack1Key(Character.toString(keyAttack1).toUpperCase());
        gamePreferences.setMapKey(Character.toString(keyMap).toUpperCase());
        gamePreferences.setQuestKey(Character.toString(keyQuest).toUpperCase());

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
