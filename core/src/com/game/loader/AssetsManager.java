package com.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.game.utils.Constants;

import java.util.Random;


public class AssetsManager {

    //Game config
    public static final String heroCfg_path = "config/heroes/";
    public static final String heroes = heroCfg_path + "heroes.json";
    public static final String heroXpCfg = heroCfg_path + "xpForLevel.json";
    public static final String enemyCfg_path = "config/enemies/";
    public static final String npcCfg_path = "config/npcs/";

    //Background
    public static final String background = "background/background.png";

    //UI
    public static final String background_hud = "images/ui/hud/hud-background.png";
    public static final String background_quest = "images/ui/quest-background.png";

    //Textures
    public static final String spriteSheet = "images/atlas.txt";

    //Tiled map: file storing info of all tiles in a map to manage it easily
    //layers are containers of tiles and we will use it to render specific layer of a map
    public static final String tiledMap = "map/map.tmx"; //path to the tiled map
    public static final String tmBackground = "background"; //tiles of the bg
    public static final String tmGround = "ground"; //tiles drawing before player
    public static final String tmForeground = "foreground"; //tiles drawing after player

    //Font
    public static final String font = "fonts/DroidSans-Bold.ttf";

    //Skins
    public static final String menuSkin = "skin/craftacular/skin/craftacular-ui.json";
    public static final String hudSkin = "skin/pixthulhu/skin/pixthulhu-ui.json";

    //Sounds
    private SoundsManager soundsManager;

    //Json loader
    public Json json;


    //Asset manager
    //Another way to declare member variable
    //These attributes are constants but accessible by other classes
    public final AssetManager manager = new AssetManager();


    public AssetsManager() {
        json = new Json();
        soundsManager = new SoundsManager(manager);
    }


    public void queueAddAssets() {
        //Load backgrounds
        manager.load(background, Texture.class);
        manager.load(background_hud, Texture.class);
        manager.load(background_quest, Texture.class);

        //Load skins
        manager.load(hudSkin, Skin.class);
        manager.load(menuSkin, Skin.class);

        //Load Sounds
        soundsManager.queueAddSounds();

        //Load the tiled map
        manager.setLoader(TiledMap.class, new TmxMapLoader()); //There is no loader for tiled map
        manager.load(tiledMap, TiledMap.class);

        //Load textures
        manager.load(spriteSheet, TextureAtlas.class);

        //Load the font
        FreeTypeFontLoaderParameter fontParameter = new FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = font;
        fontParameter.fontParameters.size = Constants.G_HEIGHT/90; //10% of the screen height
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, new FreetypeFontLoader(resolver));
        manager.load(font, BitmapFont.class, fontParameter);
    }

    public void finishLoading()
    {
        manager.finishLoading();
        soundsManager.storeSounds();
    }


    public final TextureAtlas getAtlas() {
        return manager.get(spriteSheet, TextureAtlas.class);
    }

    public final Skin getMenuSkin() {
        return manager.get(menuSkin, Skin.class);
    }

    public final Skin getHUDSkin() {return manager.get(hudSkin, Skin.class);}

    public final BitmapFont getFont() {
        return manager.get(font, BitmapFont.class);
    }

    public final SoundsManager getSoundsManager() {return soundsManager;}


    public void dispose()
    {
        manager.dispose();
    }
}