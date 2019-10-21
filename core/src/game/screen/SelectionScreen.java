package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.GDXGame;

import java.util.ArrayList;
import java.util.List;

public class SelectionScreen implements Screen {

    private GDXGame game;
    private Stage stage;
    private Label classLb;
    private TextButton [] classBtns;

    public SelectionScreen(GDXGame g)
    {
        game = g;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("Menu/craftacular/skin/craftacular-ui.json"));
        classLb = new Label("Select your class", skin);
        table.add(classLb);
        table.row().pad(100,0,100,0);

        String [] classes = getClassesFromJson();
        classBtns = new TextButton [classes.length];
        for (int i=0; i<classes.length; ++i)
        {
            classBtns[i] = new TextButton(classes[i], skin);
            classBtns[i].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.playerSpecialization = actor.toString().split(" ")[1];
                    game.changeScreen(GDXGame.APPLICATION);
                }
            });
            table.add(classBtns[i]);
        }
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
        ArrayList<String> list = json.fromJson(ArrayList.class, Gdx.files.internal("config/hero_cfg.json"));

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
    public void dispose() {}
}
