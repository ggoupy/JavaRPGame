package com.game.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;


public class AssetsManager {

    public static final String heroCfg_path = "config/heroes/";
    public static final String heroes = heroCfg_path + "heroes.json";
    public static final String heroXpCfg = heroCfg_path + "xpForLevel.json";
    public static final String enemyCfg_path = "config/enemies/";
    public static final String npcCfg_path = "config/npcs/";
    //Background path
    public static final String background = "background/background.png";
    public static final String menuSkin_path = "skin/craftacular/skin/craftacular-ui.json";
    public static final String HUDSkin_path = "skin/pixthulhu/skin/pixthulhu-ui.json";
    //UI
    public static final String background_hud = "images/ui/hud/hud-background.png";
    public static final String background_quest = "images/ui/quest-background.png";
    //Textures path
    public static final String spritesheet = "images/atlas.txt";
    //Tiled map: file storing info of all tiles in a map to manage it easily
    //layers are containers of tiles and we will use it to render specific layer of a map
    public static final String tiledMap = "map/map.tmx"; //path to the tiled map
    public static final String tmBackground = "background"; //tiles of the bg
    public static final String tmGround = "ground"; //tiles drawing before player
    public static final String tmForeground = "foreground"; //tiles drawing after player
    //Font
    public static final String font_path = "fonts/DroidSans.ttf";
    //Another way to declare member variable
    //These attributes are constants but accessible by other classes
    public final AssetManager manager = new AssetManager();
    //Config
    public Json json;
    //Skin for menu
    private Skin menuSkin;
    //Skin for HUD
    private Skin HUDSkin;
    //Atlas: spritesheet storing info of all sprites, better performance to load all in one time
    private TextureAtlas atlas;
    private BitmapFont font;


    public AssetsManager() {
        json = new Json();
        menuSkin = new Skin(Gdx.files.internal(menuSkin_path));
        HUDSkin = new Skin(Gdx.files.internal(HUDSkin_path));
        atlas = new TextureAtlas(spritesheet);

        //Font loading
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(font_path));
        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = 25;
        font = fontGenerator.generateFont(fontParameter);
        font.getCache().setUseIntegerPositions(false);
        fontGenerator.dispose();
    }

    public void queueAddAssets() {
        manager.load(background, Texture.class);
        manager.load(background_hud, Texture.class);
        manager.load(background_quest, Texture.class);

        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load(tiledMap, TiledMap.class);
    }

    public final TextureAtlas getAtlas() {
        return atlas;
    }

    public final Skin getMenuSkin() {
        return menuSkin;
    }

    public final Skin getHUDSkin() {
        return HUDSkin;
    }

    public final BitmapFont getFont() {
        return font;
    }

    public void dispose() {
        atlas.dispose();
        menuSkin.dispose();
        HUDSkin.dispose();
        font.dispose();
        manager.dispose();
    }
}