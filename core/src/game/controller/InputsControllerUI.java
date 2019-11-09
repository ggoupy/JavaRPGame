package game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import game.AppPreferences;
import game.GDXGame;

public class InputsControllerUI implements InputProcessor {

    private GDXGame game;

    private int leftKey;
    private int rightKey;
    private int upKey;
    private int downKey;

    public boolean left;
    public boolean right;
    public boolean up;
    public boolean down;
    public boolean e;
    public boolean r;
    public boolean exitMap;


    public InputsControllerUI(GDXGame g) {game=g;}


    @Override
    public boolean keyDown(int keycode)
    {
        boolean keyProcessed = false;

        if (leftKey == keycode) {
            left = true;
            keyProcessed = true;
        }
        if (rightKey == keycode) {
            right = true;
            keyProcessed = true;
        }
        if (upKey == keycode) {
            up = true;
            keyProcessed = true;
        }
        if (downKey == keycode) {
            down = true;
            keyProcessed = true;
        }
        if (Input.Keys.E == keycode) {
            e = true;
            keyProcessed = true;
        }
        if (Input.Keys.R == keycode) {
            r = true;
            keyProcessed = true;
        }
        if (Input.Keys.M == keycode) {
            exitMap = true;
            keyProcessed = true;
        }


        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        boolean keyProcessed = false;

        if (leftKey == keycode) {
            left = false;
            keyProcessed = true;
        }
        if (rightKey == keycode) {
            right = false;
            keyProcessed = true;
        }
        if (upKey == keycode) {
            up = false;
            keyProcessed = true;
        }
        if (downKey == keycode) {
            down = false;
            keyProcessed = true;
        }
        if (Input.Keys.E == keycode) {
            e = false;
            keyProcessed = true;
        }
        if (Input.Keys.R == keycode) {
            r = false;
            keyProcessed = true;
        }
        if (Input.Keys.M == keycode) {
            exitMap = false;
            keyProcessed = true;
        }

        return keyProcessed;
    }


    //we reset all keys (like if they were up)
    //used when we change the screen and so, input controller
    public void resetKeys()
    {
        left = false;
        right = false;
        up = false;
        down = false;
        e = false;
        r = false;
        exitMap = false;
    }


    //We update GDX with this controller
    //We reset keys (if used before changing) and load keys set in preferences
    public void setToCurrentController()
    {
        resetKeys();
        loadUserKeys();
        Gdx.input.setInputProcessor(this);
    }


    //We load user preferences keys (according to his keyboard)
    private void loadUserKeys()
    {
        AppPreferences preferences = game.getPreferences();
        leftKey = Input.Keys.valueOf(preferences.getMovingLeftKey());
        rightKey = Input.Keys.valueOf(preferences.getMovingRightKey());
        upKey = Input.Keys.valueOf(preferences.getMovingUpKey());
        downKey = Input.Keys.valueOf(preferences.getMovingDownKey());
    }


    @Override
    public boolean keyTyped(char character) {return false;}
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
    @Override
    public boolean mouseMoved(int screenX, int screenY) {return false;}
    @Override
    public boolean scrolled(int amount) {return false;}
}
