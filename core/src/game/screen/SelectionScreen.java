package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.GDXGame;
import game.loader.AssetsManager;

import java.util.ArrayList;


public class SelectionScreen implements Screen {

    private GDXGame game;
    private Stage stage;
    private Label classLb;
    private Label nameLb;
    private TextField nameTf;
    private TextButton [] classBtns;
    private TextButton createBtn;

    public SelectionScreen(GDXGame g) {game = g;}

    @Override
    public void show()
    {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        final Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //Background image
        Texture background = game.assetsManager.manager.get(AssetsManager.background);
        table.background(new TextureRegionDrawable(background));

        final Skin skin = game.assetsManager.getMenuSkin();
        classLb = new Label("SELECT YOUR CLASS", skin);
        classLb.setColor(Color.DARK_GRAY);
        classLb.setFontScale(1.2F);
        table.add(classLb).colspan(10).center(); //colspan 10 = trick to set the col taking all the row
        table.row().padTop(20);

        String [] classes = getClassesFromJson();
        classBtns = new TextButton [classes.length];
        for (int i=0; i<classes.length; ++i)
        {
            classBtns[i] = new TextButton(classes[i], skin);
            classBtns[i].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.playerSpecialization = actor.toString().split(" ")[1];
                }
            });
            table.add(classBtns[i]).fill().uniform();
        }
        table.row().padTop(100);


        nameLb = new Label("ENTER YOUR HERO NAME : ", skin);
        nameLb.setColor(Color.DARK_GRAY);
        nameLb.setFontScale(1.1F);
        table.add(nameLb).fill();

        nameTf = new TextField("myHero", skin);
        table.add(nameTf).fill().padLeft(10);
        table.row().padTop(200);

        createBtn = new TextButton(" CREATE YOUR HERO ", skin);
        createBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!game.playerSpecialization.equals("") && !nameTf.getText().equals(""))
                {
                    game.playerName = nameTf.getText();
                    //fade out the screen and then run the changeScreen method
                    stage.addAction(Actions.sequence(
                        Actions.fadeOut(1),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {game.changeScreen(GDXGame.GAME_SCREEN);}
                        })
                    ));
                }
                else
                {
                    Dialog a = new Dialog("Error", skin);
                    String err = "";
                    if (game.playerSpecialization.equals("")) err+="You need to select a class\n";
                    if (nameTf.getText().equals("")) err+="You need to enter your hero name\n";
                    a.text(err).pad(60);
                    a.button("Got it!").right();
                    a.show(stage);
                }
            }
        });
        table.add(createBtn).colspan(10).right(); //colspan 10 = trick to set the col taking all the row
        table.row().padTop(10);
    }

    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public String [] getClassesFromJson()
    {
        Json json = new Json();
        ArrayList<String> list = json.fromJson(ArrayList.class, Gdx.files.internal(game.assetsManager.heroes));

        String [] classes = list.toArray(new String[list.size()]);
        return classes;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {stage.dispose();}
}
