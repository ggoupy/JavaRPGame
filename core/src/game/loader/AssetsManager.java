package game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class AssetsManager {

    //another way to declare member variable
    //these attributes are constants but accessible by other classes
    public final AssetManager manager = new AssetManager();

    // Textures path
    public static final String spritesheet = "images/hero/hero_atlas.txt";

    //Tiled map: file storing info of all tiles in a map to manage it easily
    //layers are containers of tiles and we will use it to render specific layer of a map
    public static final String tiledMap = "map/map.tmx"; //path to the tiled map
    public static final String tmBackground = "background"; //tiles of the bg
    public static final String tmGround = "ground"; //tiles drawing before player
    public static final String tmForeground = "foreground"; //tiles drawing after player

    //Atlas
    //This is a spritesheet storing info of all sprites, better performance to load all in one time
    private TextureAtlas atlas;

    public void queueAddAssets()
    {
        atlas = new TextureAtlas(spritesheet);

        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load(tiledMap, TiledMap.class);
    }

    public final TextureAtlas getAtlas()
    {
        return atlas;
    }
}
