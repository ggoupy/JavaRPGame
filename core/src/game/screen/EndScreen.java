package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sun.org.apache.bcel.internal.Const;
import game.GDXGame;
import game.loader.AssetsManager;
import game.utils.Constants;

public class EndScreen implements Screen {

    private GDXGame game;
    private Stage stage;

    public EndScreen(GDXGame g) {this.game = g;}

    @Override
    public void show()
    {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        final Skin skin = game.assetsManager.getMenuSkin();

        final Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.center();

        //Background image
        Texture background = game.assetsManager.manager.get(AssetsManager.background);
        table.background(new TextureRegionDrawable(background));

        Label diedLabel = new Label("You died !", skin);
        diedLabel.setFontScale(3f);
        table.add(diedLabel);

        table.row().padTop(Constants.G_HEIGHT/15f);

        TextButton mainMenuBtn = new TextButton("Main menu", skin);
        mainMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor)
            {
                game.changeScreen(GDXGame.MENU_SCREEN);
            }
        });
        table.add(mainMenuBtn);
        table.row().padTop(50);

        TextButton exitBtn = new TextButton("Exit", skin);
        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.add(exitBtn).padBottom(200);
    }

    @Override
    public void render(float delta)
    {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
