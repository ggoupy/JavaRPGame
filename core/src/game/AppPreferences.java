package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class AppPreferences {

    private static final String MOVING_UP_KEY = "moving.up.key";
    private static final String MOVING_DOWN_KEY = "moving.down.key";
    private static final String MOVING_LEFT_KEY = "moving.left.key";
    private static final String MOVING_RIGHT_KEY = "moving.right.key";
    private static final String PREFERENCES_NAME = "user";

    private Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    public String getMovingUpKey() {return getPreferences().getString(MOVING_UP_KEY, "Z");}
    public String getMovingDownKey() {return getPreferences().getString(MOVING_DOWN_KEY, "Q");}
    public String getMovingLeftKey() {return getPreferences().getString(MOVING_LEFT_KEY, "S");}
    public String getMovingRightKey() {return getPreferences().getString(MOVING_RIGHT_KEY, "D");}

    public void setMovingUpKey(String k) {getPreferences().putString(MOVING_UP_KEY, k); getPreferences().flush();}
    public void setMovingDownKey(String k) {getPreferences().putString(MOVING_DOWN_KEY, k); getPreferences().flush();}
    public void setMovingLeftKey(String k) {getPreferences().putString(MOVING_LEFT_KEY, k); getPreferences().flush();}
    public void setMovingRightKey(String k) {getPreferences().putString(MOVING_RIGHT_KEY, k); getPreferences().flush();}
}