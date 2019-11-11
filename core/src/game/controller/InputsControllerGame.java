package game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import game.AppPreferences;
import game.GDXGame;

public class InputsControllerGame implements InputProcessor {

    private GDXGame game;

    //Keys from preferences
    private int leftKey;
    private int rightKey;
    private int upKey;
    private int downKey;
    private int attack1Key;
    private int mapKey;

    //Player control inputs
    public boolean left;
    public boolean right;
    public boolean up;
    public boolean down;
    public boolean att1;
    public boolean att2;

    //UI Inputs
    public boolean acceptQuest;
    public boolean showQuests;
    public boolean map;
    public boolean quitGame;


    public InputsControllerGame(GDXGame g) {game=g;}


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
        if (attack1Key == keycode) {
            att1 = true;
            keyProcessed = true;
        }
        if (Input.Keys.R == keycode) {
            att2 = true;
            keyProcessed = true;
        }

        //Theses keys have to be detect only one time when pressing them
        //Later we need to set it manually to false when manage the action
        if (Input.Keys.ENTER == keycode) {
            acceptQuest = !acceptQuest;
            keyProcessed = true;
        }
        if (Input.Keys.K == keycode) {
            showQuests = !showQuests;
            keyProcessed = true;
        }
        if (mapKey == keycode) {
            map = !map;
            keyProcessed = true;
        }
        if (Input.Keys.ESCAPE == keycode) {
            quitGame = !quitGame;
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
        if (attack1Key == keycode) {
            att1 = false;
            keyProcessed = true;
        }
        if (Input.Keys.R == keycode) {
            att2 = false;
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
        att1 = false;
        att2 = false;
        acceptQuest = false;
        showQuests = false;
        map = false;
        quitGame = false;
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
        attack1Key = Input.Keys.valueOf(preferences.getAttack1Key());
        mapKey = Input.Keys.valueOf(preferences.getMapKey());
    }


    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean scrolled(int amount) {return false;}
}