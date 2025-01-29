package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

public class SoundManager implements Disposable {
    private HashMap<String, Sound> soundEffects;
    private HashMap<String, Music> musicLayers; // Music layers
    private HashMap<String, Boolean> layerMuteStates; // Mute state of each layer
    private float musicVolume = 0.5f;
    // for the key sound
    private Music keySound;
    private float sfxVolume = 1.0f;

    public SoundManager() {
        soundEffects = new HashMap<>();
        musicLayers = new HashMap<>();
        layerMuteStates = new HashMap<>();
    }

    // load key sound
    public void loadKeySound(String filePath) {
        keySound = Gdx.audio.newMusic(Gdx.files.internal(filePath));
    }
    // set the key sound volume on its own.
    public void setKeySoundVolume(float volume) {
        keySound.setVolume(volume);
    }
    public void playKeySound() {
        keySound.play();
        keySound.setLooping(true);
    }
    public float getKeySoundVolume() {
        return keySound.getVolume();
    }

    // Load a sound effect
    public void loadSound(String name, String filePath) {
        soundEffects.put(name, Gdx.audio.newSound(Gdx.files.internal(filePath)));
    }

    // Play a sound effect
    public void playSound(String name) {
        Sound sound = soundEffects.get(name);
        System.out.println(name);
        if (sound != null) {
            sound.play(sfxVolume);
        }
    }

    public Sound getSound(String name)  {
        return soundEffects.get(name);
    }

    // Load music layers
    public void loadMusicLayer(String name, String filePath) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        musicLayers.put(name, music);
        layerMuteStates.put(name, false); // Default state: not muted
    }

    // Play all layers simultaneously
    public void playAllLayers() {
        for (String layerName : musicLayers.keySet()) {
            Music music = musicLayers.get(layerName);
            if (music != null) {
                music.setLooping(true);
                if (!layerMuteStates.get(layerName)) {
                    music.setVolume(musicVolume); // Only set volume for unmuted layers
                } else {
                    music.setVolume(0); // Ensure muted layers stay silent
                }
                music.play();
            }
        }
    }

    // Mute or unmute a music layer
    public void setLayerMuteState(String layerName, boolean mute) {
        Music music = musicLayers.get(layerName);
        if (music != null) {
            layerMuteStates.put(layerName, mute);
            music.setVolume(mute ? 0.0f : musicVolume); // Adjust volume based on mute state
        }
    }

    // Adjust mute states based on game state changes
    public void onGameStateChange(Map<String, Integer> layerStates) {
        for (Map.Entry<String, Integer> entry : layerStates.entrySet()) {
            String layerName = entry.getKey();
            Integer state = entry.getValue();
            boolean mute = state == 0; // 0 means muted, 1 means not muted
            setLayerMuteState(layerName, mute);
        }
    }

    // Set music volume without unmuting muted layers
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        setSfxVolume(musicVolume);
        for (String layerName : musicLayers.keySet()) {
            Music music = musicLayers.get(layerName);
            if (music != null && !layerMuteStates.get(layerName)) {
                music.setVolume(musicVolume); // Only update volume for unmuted layers
            }
        }
    }

    // Set volume for a specific music layer
    public void setLayerVolume(String layerName, float volume) {
        Music music = musicLayers.get(layerName);
        if (music != null) {
            if (!layerMuteStates.get(layerName)) { // Ensure the layer is not muted
                music.setVolume(volume);
            }
        }
    }
    public float getLayerVolume(String layerName) {
        Music music = musicLayers.get(layerName);
        float vol = music.getVolume();
        return vol;
    }

    // Set SFX volume
    public void setSfxVolume(float volume) {
        sfxVolume = volume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    // Dispose all loaded sounds and music
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

