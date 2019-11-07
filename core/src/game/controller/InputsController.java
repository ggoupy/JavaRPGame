package game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class InputsController implements InputProcessor {

    //Player control inputs
    public boolean left;
    public boolean right;
    public boolean up;
    public boolean down;
    public boolean e_key;
    public boolean r_key;

    //Map inputs
    public boolean map_key;
    public boolean exitMap_key;


    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode)
        {
            case Keys.Q:
                left = true;
                keyProcessed = true;
                break;
            case Keys.D:
                right = true;
                keyProcessed = true;
                break;
            case Keys.Z:
                up = true;
                keyProcessed = true;
                break;
            case Keys.S:
                down = true;
                keyProcessed = true;
                break;
            case Keys.E:
                e_key = true;
                keyProcessed = true;
                break;
            case Keys.R:
                r_key = true;
                keyProcessed = true;
                break;
            case Keys.M:
                map_key = true;
                keyProcessed = true;
                break;
            case Keys.ESCAPE:
                exitMap_key = true;
                keyProcessed = true;
                break;
        }

        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode)
        {
            case Keys.Q:
                left = false;
                keyProcessed = true;
                break;
            case Keys.D:
                right = false;
                keyProcessed = true;
                break;
            case Keys.Z:
                up = false;
                keyProcessed = true;
                break;
            case Keys.S:
                down = false;
                keyProcessed = true;
                break;
            case Keys.E:
                e_key = false;
                keyProcessed = true;
                break;
            case Keys.R:
                r_key = false;
                keyProcessed = true;
                break;
            case Keys.M:
                map_key = false;
                keyProcessed = true;
                break;
            case Keys.ESCAPE:
                exitMap_key = false;
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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