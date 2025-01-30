package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.entity.User;
import de.tum.cit.fop.maze.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * The ScreenManager class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class ScreenManager extends Game {
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;
    private PauseOverlay pauseScreen;
    private LevelSelectorScreen levelSelectorScreen;

    private SpriteBatch spriteBatch;
    private Skin skin;

    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private Animation<TextureRegion> characterUpIdleAnimation;
    private Animation<TextureRegion> characterDownIdleAnimation;
    private Animation<TextureRegion> characterLeftIdleAnimation;
    private Animation<TextureRegion> characterRightIdleAnimation;
    private Animation<TextureRegion> characterLeftAttackAnimation;
    private Animation<TextureRegion> characterRightAttackAnimation;
    private Animation<TextureRegion> characterDownAttackAnimation;
    private Animation<TextureRegion> characterUpAttackAnimation;
    private Animation<TextureRegion> runUpAnimation;
    private Animation<TextureRegion> runDownAnimation;
    private Animation<TextureRegion> runLeftAnimation;
    private Animation<TextureRegion> runRightAnimation;

    private Music backgroundMusic;
    private SoundManager soundManager;
    private Map<String, Integer> mainState = new HashMap<>();
    private float passedVolumeSettingToPause = 0.1234f;

    private User user;

    /**
     * Constructor for ScreenManager.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public ScreenManager(NativeFileChooser fileChooser) {
        super();
        this.soundManager = new SoundManager();
    }

    /**
     * Returns the main state of the game.
     *
     * @return A map containing the main state of the game.
     */
    public Map<String, Integer> getMainState() {
        return mainState;
    }

    /**
     * Returns the current game screen.
     *
     * @return The current GameScreen instance.
     */
    public GameScreen getGameScreen() {
        return gameScreen;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch, Skin, and loads necessary resources.
     */
    @Override
    public void create() {
        user = User.loadUserData("user_data.ser");
        if (user == null) {
            user = new User("Player1");
        }

        soundManager.loadMusicLayer("bass", "music/themes/Bass2.mp3");
        soundManager.loadMusicLayer("piano", "music/themes/Piano2.mp3");
        soundManager.loadMusicLayer("drums", "music/themes/Drums2.mp3");
        soundManager.loadMusicLayer("slowerDrums", "music/themes/slowerdrums.mp3");
        soundManager.loadMusicLayer("pad", "music/themes/Pad2.mp3");
        soundManager.loadMusicLayer("strings", "music/themes/Strings2.mp3");
        soundManager.loadMusicLayer("wind", "music/themes/Wind2.mp3");
        soundManager.loadMusicLayer("crackles", "music/themes/Crackles2.mp3");

        soundManager.loadSound("losing sound", "music/losing_sound.mp3");
        soundManager.loadSound("xplsv", "music/sfxs/xplsv.mp3");
        soundManager.loadSound("mcCollectKey_sfx", "music/sfxs/mcCollectKey2.mp3");
        soundManager.loadSound("mcCollectSpeedUp_sfx", "music/sfxs/mcPickSpeedUp.mp3");
        soundManager.loadSound("mcCollectHeart_sfx", "music/sfxs/mcPickHeart.mp3");
        soundManager.loadSound("mcPunch_sfx", "music/sfxs/mcPunch.mp3");
        soundManager.loadSound("mcDeath_sfx", "music/sfxs/mcDeath.mp3");
        soundManager.loadSound("mcHitWithAxe_sfx", "music/sfxs/mcHitsEnemyWithAxe.mp3");
        soundManager.loadSound("mcDoor_sfx", "music/sfxs/mcDoor.mp3");
        soundManager.loadSound("mcOpenBigDoor_sfx", "music/sfxs/mcOpenBigDoor.mp3");
        soundManager.loadSound("mcOpenNormalDoor_sfx", "music/sfxs/mcOpenNormalDoor.mp3");
        soundManager.loadSound("mcUsePowerUp_sfx", "music/sfxs/mcUsePowerUp.mp3");
        soundManager.loadSound("footstep_sfx", "music/footstep_sfx.mp3");
        soundManager.loadSound("mcHurt_sfx", "music/sfxs/mcHurt.mp3");
        soundManager.loadSound("enemyDeath_sfx", "music/sfxs/EnemyDeath.mp3");
        soundManager.loadSound("click", "music/UI/menuSelect.mp3");
        soundManager.loadSound("enemyHurt", "music/hitHurt.wav");
        soundManager.loadSound("mineRock1", "music/sfx/mineRock1.mp3");
        soundManager.loadSound("mineRock2", "music/sfx/mineRock2.mp3");
        soundManager.loadSound("mineRock3", "music/sfx/mineRock3.mp3");
        soundManager.loadSound("playerHurt", "music/game_sfx/MC_sfx/hitHurt.wav");
        soundManager.loadSound("gemBreak", "music/sfxs/Gembr.mp3");

        soundManager.loadKeySound("music/themes/Axe_nearby.mp3");

        mainState.put("crackles", 1);
        mainState.put("wind", 1);
        mainState.put("piano", 0);
        mainState.put("strings", 0);
        mainState.put("pad", 0);
        mainState.put("drums", 0);
        mainState.put("bass", 1);

        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));
        spriteBatch = new SpriteBatch();
        this.loadCharacterAnimation();

        soundManager.playAllLayers();
        goToMenu();
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this, soundManager));
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
    }

    /**
     * Switches to the game screen and loads the appropriate level.
     */
    public void goToGame() {
        soundManager.onGameStateChange(mainState);

        FileHandle levelsDirectory = Gdx.files.local("assets/TiledMaps");
        Array<FileHandle> tmxFiles = new Array<>();
        for (FileHandle file : levelsDirectory.list()) {
            if (file.extension().equals("tmx")) {
                tmxFiles.add(file);
            }
        }

        tmxFiles.sort(Comparator.comparing(file -> ((FileHandle) file).nameWithoutExtension()).reversed());
        user.getCompletedLevels().sort(Comparator.reverseOrder());

        for (FileHandle file : tmxFiles) {
            if (tmxFiles.size == user.getCompletedLevels().size()) {
                user.resetCompletedLevels();
                goToCredits();
                break;
            } else {
                if (user.getCompletedLevels().contains(file.name())) {
                    continue;
                } else {
                    GameScreen newGameScreen = new GameScreen(this, ("TiledMaps/" + file.name()), soundManager);
                    newGameScreen.getPlayer().loadState("playerstate.txt");
                    this.setScreen(newGameScreen);
                    System.out.println("loaded :" + file.name() + " map");
                }
            }
        }

        if (menuScreen != null) {
            menuScreen.dispose();
            menuScreen = null;
        }
        if (pauseScreen != null) {
            pauseScreen.dispose();
            pauseScreen = null;
        }
    }

    /**
     * Passes volme settings between all screens and when reloading/loading int oa level.
     * @param passedVolumeSettingToPause
     */

    public void setPassedVolumeSettingToPause(float passedVolumeSettingToPause) {
        this.passedVolumeSettingToPause = passedVolumeSettingToPause;
    }

    /**
     * Getter for the passed volumestting variable.
     * @return
     */

    public float getPassedVolumeSettingToPause() {
        return passedVolumeSettingToPause;
    }

    /**
     * Switches to the next level or credits screen if all levels are completed.
     */
    public void goToNextLevel() {
        FileHandle levelsDirectory = Gdx.files.local("assets/TiledMaps");
        System.out.println(levelsDirectory);
        int counter = 0;
        for (FileHandle file : levelsDirectory.list()) {
            if (file.extension().equals("tmx")) {
                counter++;
            }
        }

        System.out.println(user.getCompletedLevels());
        System.out.println(levelsDirectory);
        System.out.println(counter);

        if (user.getCompletedLevels().size() == counter) {
            user.saveUserData("user_data.ser");
            goToCredits();
        } else {
            goToGame();
        }
    }

    /**
     * Switches to the settings screen.
     */
    public void goToSettings() {
        this.setScreen(new SettingsScreen(this));
        if (menuScreen != null) {
            menuScreen.dispose();
            menuScreen = null;
        }
    }

    /**
     * Switches to the credits screen.
     */
    public void goToCredits() {
        this.setScreen(new CreditScreen(this));
        if (menuScreen != null) {
            menuScreen.dispose();
            menuScreen = null;
        }
    }

    /**
     * Switches to the level selector screen.
     */
    public void goToLevelSelector() {
        if (levelSelectorScreen != null) {
            levelSelectorScreen.dispose();
        }
        this.setScreen(new LevelSelectorScreen(this));
    }

    /**
     * Saves the user data to a file.
     */
    public void saveUserData() {
        if (user != null) {
            user.saveUserData("user_data.ser");
        }
    }

    /**
     * Sets user preferences and saves them.
     *
     * @param preferences A map containing user preferences.
     */
    public void setPreferences(Map<String, Object> preferences) {
        if (user != null) {
            user.setPreferences(preferences);
            saveUserData();
        }
    }

    /**
     * Loads a specific level and switches to the game screen.
     *
     * @param levelName The name of the level to load.
     */
    public void loadLevel(String levelName) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load("assets/TiledMaps/" + levelName + ".tmx");

        this.setScreen(new GameScreen(this, "assets/TiledMaps/" + levelName + ".tmx", soundManager));
    }

    /**
     * Loads character animations from a sprite sheet.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("animations/player/FinalAnimAdjusted2.png"));

        int frameWidth = 64, frameHeight = 64, walkAnimationFrames = 9, idleAnimationFrames = 2, y = 0;

        for (int i = 0; i <= 50; i++) {
            Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> idleFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> attackLeftFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> runningframes = new Array<>(TextureRegion.class);

            for (int col = 0; col < walkAnimationFrames; col++) {
                walkFrames.add(new TextureRegion(walkSheet, col * (frameWidth), y, frameWidth, frameHeight));
            }
            for (int col = 0; col < idleAnimationFrames; col++) {
                idleFrames.add(new TextureRegion(walkSheet, col * (frameWidth), y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 6; col++) {
                attackLeftFrames.add(new TextureRegion(walkSheet, col * (frameWidth), y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 8; col++) {
                runningframes.add(new TextureRegion(walkSheet, col * (frameWidth), y, frameWidth, frameHeight));
            }
            switch (y) {
                case 640:
                    characterDownAnimation = new Animation<>(0.05f, walkFrames);
                    break;
                case 704:
                    characterRightAnimation = new Animation<>(0.05f, walkFrames);
                    break;
                case 576:
                    characterLeftAnimation = new Animation<>(0.05f, walkFrames);
                    break;
                case 512:
                    characterUpAnimation = new Animation<>(0.05f, walkFrames);
                    break;
                case 0:
                    characterDownIdleAnimation = new Animation<>(0.25f, idleFrames);
                    break;
                case 256:
                    characterUpIdleAnimation = new Animation<>(0.25f, idleFrames);
                    break;
                case 64:
                    characterLeftIdleAnimation = new Animation<>(0.25f, idleFrames);
                    break;
                case 192:
                    characterRightIdleAnimation = new Animation<>(0.25f, idleFrames);
                    break;
                case 3008:
                    characterRightAttackAnimation = new Animation<>(0.05f, attackLeftFrames, Animation.PlayMode.NORMAL);
                    break;
                case 3072:
                    characterDownAttackAnimation = new Animation<>(0.07f, attackLeftFrames);
                    break;
                case 3136:
                    characterLeftAttackAnimation = new Animation<>(0.05f, attackLeftFrames);
                    break;
                case 3200:
                    characterUpAttackAnimation = new Animation<>(0.07f, attackLeftFrames);
                    break;
                case 2176:
                    runUpAnimation = new Animation<>(0.05f, runningframes);
                    break;
                case 2240:
                    runLeftAnimation = new Animation<>(0.05f, runningframes);
                    break;
                case 2304:
                    runDownAnimation = new Animation<>(0.05f, runningframes);
                    break;
                case 2368:
                    runRightAnimation = new Animation<>(0.05f, runningframes);
                    break;
            }
            y += frameHeight;
        }
    }

    /**
     * Returns the run-up animation.
     *
     * @return The run-up animation.
     */
    public Animation<TextureRegion> getRunUpAnimation() {
        return runUpAnimation;
    }

    /**
     * Returns the run-down animation.
     *
     * @return The run-down animation.
     */
    public Animation<TextureRegion> getRunDownAnimation() {
        return runDownAnimation;
    }

    /**
     * Returns the run-left animation.
     *
     * @return The run-left animation.
     */
    public Animation<TextureRegion> getRunLeftAnimation() {
        return runLeftAnimation;
    }

    /**
     * Returns the run-right animation.
     *
     * @return The run-right animation.
     */
    public Animation<TextureRegion> getRunRightAnimation() {
        return runRightAnimation;
    }

    /**
     * Returns the character's up attack animation.
     *
     * @return The character's up attack animation.
     */
    public Animation<TextureRegion> getcharacterUpAttackAnimation() {
        return characterUpAttackAnimation;
    }

    /**
     * Returns the character's down attack animation.
     *
     * @return The character's down attack animation.
     */
    public Animation<TextureRegion> getcharacterDownAttackAnimation() {
        return characterDownAttackAnimation;
    }

    /**
     * Returns the character's right attack animation.
     *
     * @return The character's right attack animation.
     */
    public Animation<TextureRegion> getCharacterRightAttackAnimation() {
        return characterRightAttackAnimation;
    }

    /**
     * Returns the character's left attack animation.
     *
     * @return The character's left attack animation.
     */
    public Animation<TextureRegion> getCharacterLeftAttackAnimation() {
        return characterLeftAttackAnimation;
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        if (user != null) {
            user.saveUserData("user_data.ser");
        }

        getScreen().hide();
        getScreen().dispose();
        spriteBatch.dispose();
        skin.dispose();
        soundManager.dispose();
    }

    /**
     * Returns the sound manager.
     *
     * @return The sound manager.
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * Returns the UI skin.
     *
     * @return The UI skin.
     */
    public Skin getSkin() {
        return skin;
    }

    /**
     * Returns the character's down animation.
     *
     * @return The character's down animation.
     */
    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    /**
     * Returns the character's up animation.
     *
     * @return The character's up animation.
     */
    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    /**
     * Returns the character's right animation.
     *
     * @return The character's right animation.
     */
    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    /**
     * Returns the character's left animation.
     *
     * @return The character's left animation.
     */
    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    /**
     * Returns the character's up attack animation.
     *
     * @return The character's up attack animation.
     */
    public Animation<TextureRegion> getCharacterUpAttackAnimation() {
        return characterUpAttackAnimation;
    }

    /**
     * Returns the character's down attack animation.
     *
     * @return The character's down attack animation.
     */
    public Animation<TextureRegion> getCharacterDownAttackAnimation() {
        return characterDownAttackAnimation;
    }

    /**
     * Returns the character's right idle animation.
     *
     * @return The character's right idle animation.
     */
    public Animation<TextureRegion> getCharacterRightIdleAnimation() {
        return characterRightIdleAnimation;
    }

    /**
     * Returns the character's left idle animation.
     *
     * @return The character's left idle animation.
     */
    public Animation<TextureRegion> getCharacterLeftIdleAnimation() {
        return characterLeftIdleAnimation;
    }

    /**
     * Returns the character's down idle animation.
     *
     * @return The character's down idle animation.
     */
    public Animation<TextureRegion> getCharacterDownIdleAnimation() {
        return characterDownIdleAnimation;
    }

    /**
     * Returns the character's up idle animation.
     *
     * @return The character's up idle animation.
     */
    public Animation<TextureRegion> getCharacterUpIdleAnimation() {
        return characterUpIdleAnimation;
    }

    /**
     * Returns the sprite batch used for drawing textures.
     *
     * @return The sprite batch.
     */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    /**
     * Returns the background music currently playing.
     *
     * @return The background music.
     */
    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    /**
     * Sets the background music to the specified music.
     *
     * @param backgroundMusic The music to set as background music.
     */
    public void setBackgroundMusic(Music backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    /**
     * Returns the user associated with the game.
     *
     * @return The user.
     */
    public User getUser() {
        return user;
    }
}