package com.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.game.AppPreferences;

import java.util.Random;


public class SoundsManager {

    private static final String path = "sounds/";
    private static final String bgMusic = path+"background-music.mp3";

    private static final String soundEffects_path = path+"effects/";
    private ArrayMap<String, Sound> soundEffects;

    private static final String npcSounds_path = path+"npcs/";
    private ArrayMap<String, Music> npcSounds;

    private AssetManager manager;
    private float soundVolume = 1f;


    SoundsManager(AssetManager assetManager)
    {
        this.manager = assetManager;
        npcSounds = new ArrayMap<>();
        soundEffects = new ArrayMap<>();
    }

    void queueAddSounds()
    {
        manager.load(bgMusic, Music.class);

        //Read all files in effect sounds directory and load them
        FileHandle effectDir = new FileHandle(soundEffects_path);
        for (FileHandle f : effectDir.list())
        {
            manager.load(soundEffects_path+f.name(), Sound.class);
        }

        //Read all files in npc sounds directory and load them
        FileHandle npcDir = new FileHandle(npcSounds_path);
        for (FileHandle f : npcDir.list())
        {
            manager.load(npcSounds_path+f.name(), Music.class);
        }
    }


    //Read all files in sound folders and store the path and the music in arrays
    void storeSounds()
    {
        //Effects
        FileHandle effectDir = new FileHandle(soundEffects_path);
        for (FileHandle f : effectDir.list())
        {
            soundEffects.put(soundEffects_path+f.name(), manager.get(soundEffects_path+f.name(), Sound.class));
        }

        //NPCs
        FileHandle npcDir = new FileHandle(npcSounds_path);
        for (FileHandle f : npcDir.list())
        {
            npcSounds.put(npcSounds_path+f.name(),manager.get(npcSounds_path+f.name(), Music.class));
        }
    }


    public void playBgMusic()
    {
        getBgMusic().setLooping(true);
        getBgMusic().play();
    }


    //we cant handle Sound class instances volume here
    //Because we set the volume when playing (so we need to save the new volume to apply it later)
    public void setSoundsVolume(float volume)
    {
        soundVolume = volume;

        getBgMusic().setVolume(volume);
        for (Music npcSound : npcSounds.values()) npcSound.setVolume(volume);
    }


    private Music getBgMusic() {return manager.get(bgMusic, Music.class);}


    //Play the sound effect given in parameter
    public void playEffect(String name)
    {
        for(ObjectMap.Entry<String, Sound> effect : soundEffects)
        {
            if (effect.key.contains(name))
            {
                effect.value.play(soundVolume);
                return;
            }
        }
    }


    //play a random npc greeting sound for a given type of npc
    public void playNpcGreeting(String type)
    {
        String researchedSound = type+"-greeting"; //sound path pattern researched

        //We loop the array map looking for greeting sounds for a type
        //And we add sounds found in a array
        Array<Music> greetings = new Array<>();
        for(ObjectMap.Entry<String, Music> npcSound : npcSounds)
        {
            if (npcSound.key.contains(researchedSound)) greetings.add(npcSound.value);
        }

        //We return a random sound from the array
        greetings.get(new Random().nextInt(greetings.size)).play();
    }
}
