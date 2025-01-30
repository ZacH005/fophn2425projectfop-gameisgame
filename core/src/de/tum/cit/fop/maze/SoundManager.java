package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages sound effects and music layers for the game.
 * This class handles loading, playing, and controlling the volume of sounds and music.
 */
public class SoundManager implements Disposable {
    private HashMap<String, Sound> soundEffects;
    private HashMap<String, Music> musicLayers;
    private HashMap<String, Boolean> layerMuteStates;
    private float musicVolume = 0.5f;
    private Music keySound;
    private float sfxVolume = 1.0f;

    /**
     * Constructs a new SoundManager and initializes the data structures.
     */
    public SoundManager() {
        soundEffects = new HashMap<>();
        musicLayers = new HashMap<>();
        layerMuteStates = new HashMap<>();
    }

    /**
     * Loads the key sound from the specified file path.
     *
     * @param filePath the path to the key sound file.
     */
    public void loadKeySound(String filePath) {
        keySound = Gdx.audio.newMusic(Gdx.files.internal(filePath));
    }

    /**
     * Sets the volume of the key sound.
     *
     * @param volume the volume level to set (0.0 to 1.0).
     */
    public void setKeySoundVolume(float volume) {
        keySound.setVolume(volume);
    }

    /**
     * Plays the key sound and sets it to loop continuously.
     */
    public void playKeySound() {
        keySound.play();
        keySound.setLooping(true);
    }

    /**
     * Returns the current volume of the key sound.
     *
     * @return the volume of the key sound.
     */
    public float getKeySoundVolume() {
        return keySound.getVolume();
    }

    /**
     * Loads a sound effect and associates it with a name.
     *
     * @param name     the name to associate with the sound effect.
     * @param filePath the path to the sound effect file.
     */
    public void loadSound(String name, String filePath) {
        soundEffects.put(name, Gdx.audio.newSound(Gdx.files.internal(filePath)));
    }

    /**
     * Plays the sound effect associated with the specified name.
     *
     * @param name the name of the sound effect to play.
     */
    public void playSound(String name) {
        Sound sound = soundEffects.get(name);
        if (sound != null) {
            sound.play(sfxVolume);
        }
    }

    /**
     * Returns the sound effect associated with the specified name.
     *
     * @param name the name of the sound effect.
     * @return the Sound object, or null if not found.
     */
    public Sound getSound(String name) {
        return soundEffects.get(name);
    }

    /**
     * Loads a music layer and associates it with a name.
     *
     * @param name     the name to associate with the music layer.
     * @param filePath the path to the music file.
     */
    public void loadMusicLayer(String name, String filePath) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        musicLayers.put(name, music);
        layerMuteStates.put(name, false);
    }

    /**
     * Plays all loaded music layers simultaneously.
     */
    public void playAllLayers() {
        for (String layerName : musicLayers.keySet()) {
            Music music = musicLayers.get(layerName);
            if (music != null) {
                music.setLooping(true);
                if (!layerMuteStates.get(layerName)) {
                    music.setVolume(musicVolume);
                } else {
                    music.setVolume(0);
                }
                music.play();
            }
        }
    }

    /**
     * Sets the mute state for a specific music layer.
     *
     * @param layerName the name of the music layer.
     * @param mute      true to mute the layer, false to unmute.
     */
    public void setLayerMuteState(String layerName, boolean mute) {
        Music music = musicLayers.get(layerName);
        if (music != null) {
            layerMuteStates.put(layerName, mute);
            music.setVolume(mute ? 0.0f : musicVolume);
        }
    }

    /**
     * Adjusts the mute states of music layers based on the game state.
     *
     * @param layerStates a map containing the mute states for each layer.
     */
    public void onGameStateChange(Map<String, Integer> layerStates) {
        for (Map.Entry<String, Integer> entry : layerStates.entrySet()) {
            String layerName = entry.getKey();
            Integer state = entry.getValue();
            boolean mute = state == 0;
            setLayerMuteState(layerName, mute);
        }
    }

    /**
     * Sets the volume for all unmuted music layers.
     *
     * @param volume the volume level to set (0.0 to 1.0).
     */
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        setSfxVolume(musicVolume);
        for (String layerName : musicLayers.keySet()) {
            Music music = musicLayers.get(layerName);
            if (music != null && !layerMuteStates.get(layerName)) {
                music.setVolume(musicVolume);
            }
        }
    }

    /**
     * Sets the volume for a specific music layer.
     *
     * @param layerName the name of the music layer.
     * @param volume    the volume level to set (0.0 to 1.0).
     */
    public void setLayerVolume(String layerName, float volume) {
        Music music = musicLayers.get(layerName);
        if (music != null && !layerMuteStates.get(layerName)) {
            music.setVolume(volume);
        }
    }

    /**
     * Returns the current volume of a specific music layer.
     *
     * @param layerName the name of the music layer.
     * @return the volume of the music layer.
     */
    public float getLayerVolume(String layerName) {
        Music music = musicLayers.get(layerName);
        return music.getVolume();
    }

    /**
     * Sets the volume for sound effects.
     *
     * @param volume the volume level to set (0.0 to 1.0).
     */
    public void setSfxVolume(float volume) {
        sfxVolume = volume;
    }

    /**
     * Returns the current music volume.
     *
     * @return the music volume.
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Returns the current sound effects volume.
     *
     * @return the sound effects volume.
     */
    public float getSfxVolume() {
        return sfxVolume;
    }

    /**
     * Disposes of all loaded sounds and music to free up resources.
     */
    @Override
    public void dispose() {
        for (Sound sound : soundEffects.values()) {
            sound.dispose();
        }
        for (Music music : musicLayers.values()) {
            music.dispose();
        }
    }
}