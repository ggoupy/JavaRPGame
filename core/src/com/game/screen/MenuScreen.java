package com.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.game.GDXGame;
import com.game.loader.AssetsManager;

public class MenuScreen implements Screen {

    private GDXGame game;
    private Stage stage;

    public MenuScreen(GDXGame g) {
        game = g;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Skin skin = game.assetsManager.getMenuSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //Background image
        Texture background = game.assetsManager.manager.get(AssetsManager.background);
        table.background(new TextureRegionDrawable(background));

        //NEW GAME
        TextButton newGame = new TextButton(game.gameLaunched ? "Resume Game" : "New Game", skin);
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.gameLaunched) game.changeScreen(GDXGame.GAME_SCREEN);
                else game.changeScreen(GDXGame.SELECTION_SCREEN);
            }
        });
        table.add(newGame).fill().uniform().padBottom(50);
        table.row();

        //PREFERENCES
        TextButton preferences = new TextButton("Preferences", skin);
        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(GDXGame.PREFERENCE_SCREEN);
            }
        });
        table.add(preferences).fill().uniform().padBottom(50);
        ;
        table.row();

        //EXIT
        TextButton exit = new TextButton("Exit", skin);
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.add(exit).fill().uniform();
    }

    @Override
    public void render(float delta) {
        //Clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
