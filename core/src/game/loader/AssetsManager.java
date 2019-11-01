package game.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class AssetsManager {

    //Another way to declare member variable
    //These attributes are constants but accessible by other classes
    public final AssetManager manager = new AssetManager();

    //Config
    public Json json;
    public static final String heroes_config = "config/heroes.json";
    public static final String heroCfg_path = "config/heroes/";

    //Background path
    public static final String background = "background/background.png";
    public static final String background_hud = "background/background-hud.png";

    //Skin for menu
    private Skin menuSkin;
    public static final String menuSkin_path = "skin/craftacular/skin/craftacular-ui.json";

    //Skin for HUD
    private Skin HUDSkin;
    public static final String HUDSkin_path = "skin/pixthulhu/skin/pixthulhu-ui.json";

    //Textures path
    public static final String spritesheet = "images/atlas.txt";

    //Tiled map: file storing info of all tiles in a map to manage it easily
    //layers are containers of tiles and we will use it to render specific layer of a map
    public static final String tiledMap = "map/map.tmx"; //path to the tiled map
    public static final String tmBackground = "background"; //tiles of the bg
    public static final String tmGround = "ground"; //tiles drawing before player
    public static final String tmForeground = "foreground"; //tiles drawing after player

    //Atlas: spritesheet storing info of all sprites, better performance to load all in one time
    private TextureAtlas atlas;

    public AssetsManager()
    {
        json = new Json();
        menuSkin = new Skin(Gdx.files.internal(menuSkin_path));
        HUDSkin = new Skin(Gdx.files.internal(HUDSkin_path));
        atlas = new TextureAtlas(spritesheet);
    }

    public void queueAddAssets()
    {
        manager.load(background, Texture.class);
        manager.load(background_hud, Texture.class);

        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load(tiledMap, TiledMap.class);
    }

    public final TextureAtlas getAtlas()
    {
        return atlas;
    }

    public final Skin getMenuSkin() { return menuSkin; }
    public final Skin getHUDSkin() { return HUDSkin; }
}