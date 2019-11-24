package com.game.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.game.utils.Constants;


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
    public static final String font = "fonts/font.ttf";//"fonts/DroidSans-Bold.ttf";
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontParameter fontParameter;

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

        //Load the basic font
        FreeTypeFontLoaderParameter parameter = new FreeTypeFontLoaderParameter();
        parameter.fontFileName = font;
        parameter.fontParameters.size = Constants.FONT_SIZE;
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, new FreetypeFontLoader(resolver));
        manager.load(font, BitmapFont.class, parameter);

        //Create the font generator
        this.fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(font));
        this.fontParameter = new FreeTypeFontParameter();
    }


    public void finishLoading()
    {
        manager.finishLoading();
        soundsManager.storeSounds();

        getFont().getData().setScale(0.9f, 0.9f);
    }


    public BitmapFont createTitleFont() {return createFont(Constants.FONT_SIZE*2);}
    public BitmapFont createSubTitleFont() {return createFont((int) (Constants.FONT_SIZE*1.5));}
    public BitmapFont createTextFont() {return createFont((int) (Constants.FONT_SIZE*1.2));}
    public BitmapFont createLittleTextFont() {return createFont((int) (Constants.FONT_SIZE*0.9));}
    public BitmapFont createFont() {return createFont(Constants.FONT_SIZE);}
    private BitmapFont createFont(int dp)
    {
        fontParameter.size = dp;
        BitmapFont f = fontGenerator.generateFont(fontParameter);
        f.getData().setScale(0.9f,0.9f);
        return f;
    }


    public final TextureAtlas getAtlas() {
        return manager.get(spriteSheet, TextureAtlas.class);
    }

    public final Skin getMenuSkin() {
        return manager.get(menuSkin, Skin.class);
    }

    public final Skin getHUDSkin() {return manager.get(hudSkin, Skin.class);}

    public final BitmapFont getFont() {return manager.get(font, BitmapFont.class);}

    public final SoundsManager getSoundsManager() {return soundsManager;}


    public void dispose()
    {
        fontGenerator.dispose();
        manager.dispose();
    }
}