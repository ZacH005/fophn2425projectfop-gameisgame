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
import java.util.HashMap;
import java.util.Map;
//bananas comment
/**
 * The ScreenManager class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class ScreenManager extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;
    private PauseOverlay pauseScreen;
    private LevelSelectorScreen levelSelectorScreen;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // Character Animations
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

    // Music
    private Music backgroundMusic;
    private SoundManager soundManager;
    private Map<String,Integer> mainState = new HashMap<String,Integer>();
    private float passedVolumeSettingToPause = 0.1234f;

    public float getPassedVolumeSettingToPause() {
        return passedVolumeSettingToPause;
    }

    public void setPassedVolumeSettingToPause(float passedVolumeSettingToPause) {
        this.passedVolumeSettingToPause = passedVolumeSettingToPause;
    }

    // User and Game State
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

    public Map<String, Integer> getMainState() {
        return mainState;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        // Load existing user data, or use a default constructor to create a new one
        user = User.loadUserData("user_data.ser");
        if (user == null) {
            // Set default username if no data found
            user = new User("Player1");
        }
        ///sound layers
        soundManager.loadMusicLayer("bass","music/themes/Bass2.mp3");
        soundManager.loadMusicLayer("piano","music/themes/Piano2.mp3");
        soundManager.loadMusicLayer("drums","music/themes/Drums2.mp3");
        soundManager.loadMusicLayer("slowerDrums","music/themes/slowerdrums.mp3");
        soundManager.loadMusicLayer("pad","music/themes/Pad2.mp3");
        soundManager.loadMusicLayer("strings","music/themes/Strings2.mp3");
        soundManager.loadMusicLayer("wind","music/themes/Wind2.mp3");
        soundManager.loadMusicLayer("crackles","music/themes/Crackles2.mp3");

        ///sfxs
        soundManager.loadSound("losing sound","music/losing_sound.mp3");

        soundManager.loadSound("xplsv","music/sfxs/xplsv.mp3");

        soundManager.loadSound("mcCollectKey_sfx","music/sfxs/mcCollectKey2.mp3");
        soundManager.loadSound("mcCollectSpeedUp_sfx","music/sfxs/mcPickSpeedUp.mp3");
        soundManager.loadSound("mcCollectHeart_sfx","music/sfxs/mcPickHeart.mp3");
        soundManager.loadSound("mcPunch_sfx","music/sfxs/mcPunch.mp3");
        soundManager.loadSound("mcDeath_sfx","music/sfxs/mcDeath.mp3");
        soundManager.loadSound("mcHitWithAxe_sfx","music/sfxs/mcHitsEnemyWithAxe.mp3");
        soundManager.loadSound("mcDoor_sfx","music/sfxs/mcDoor.mp3");
        soundManager.loadSound("mcOpenBigDoor_sfx","music/sfxs/mcOpenBigDoor.mp3");
        soundManager.loadSound("mcOpenNormalDoor_sfx","music/sfxs/mcOpenNormalDoor.mp3");
        soundManager.loadSound("mcUsePowerUp_sfx","music/sfxs/mcUsePowerUp.mp3");
        soundManager.loadSound("footstep_sfx","music/footstep_sfx.mp3");
        soundManager.loadSound("mcHurt_sfx","music/sfxs/mcHurt.mp3");
        soundManager.loadSound("enemyDeath_sfx","music/sfxs/EnemyDeath.mp3");
        soundManager.loadSound("click","music/UI/menuSelect.mp3");
        soundManager.loadSound("enemyHurt", "music/hitHurt.wav");
        soundManager.loadSound("mineRock1", "music/sfx/mineRock1.mp3");
        soundManager.loadSound("mineRock2", "music/sfx/mineRock2.mp3");
        soundManager.loadSound("mineRock3", "music/sfx/mineRock3.mp3");
        soundManager.loadSound("playerHurt", "music/game_sfx/MC_sfx/hitHurt.wav");
        soundManager.loadSound("gemBreak", "music/sfxs/Gembr.mp3");

        /// key sound
        soundManager.loadKeySound("music/themes/Axe_nearby.mp3");
        /// state definition
        mainState.put("crackles",1);
        mainState.put("wind",1);
        mainState.put("piano",0);
        mainState.put("strings",0);
        mainState.put("pad",0);
        mainState.put("drums",0);
        mainState.put("bass",1);

        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        this.loadCharacterAnimation(); // Load character animation

//        //MUSIC
//        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/main ost.mp3"));
//        backgroundMusic.setLooping(true);
//        //muting it for now
//        backgroundMusic.setVolume(0);
//        backgroundMusic.play();

        soundManager.playAllLayers();
        goToMenu(); // Navigate to the menu screen
    }

    /*** Switches to the menu screen.*/
    public void goToMenu() {
        this.setScreen(new MenuScreen(this,soundManager)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    public void goToGame() {
        soundManager.onGameStateChange(mainState);

        // loading all maps
        FileHandle levelsDirectory = Gdx.files.local("assets/TiledMaps");
        Array<FileHandle> tmxFiles = new Array<>();
        for (FileHandle file : levelsDirectory.list()) {
            if (file.extension().equals("tmx")) {
                tmxFiles.add(file);
            }
        }

        //getting the first map that isn't completed by the user.
        for(FileHandle file : tmxFiles) {
            // if the level is completed don't open it
            if(tmxFiles.size == user.getCompletedLevels().size()){
                user.resetCompletedLevels();
                this.setScreen(new MenuScreen(this,soundManager));
                break;
            }
            else{
            if(user.getCompletedLevels().contains(file.name())){
                continue;
            }
            // if a level isn't played then this the one we go to intuitively
            else{
                System.out.println("loaded :"+file.name()+" map");
                this.setScreen(new GameScreen(this, ("TiledMaps/"+file.name()),soundManager));
            }}

        }
         // Set the current screen to GameScreen

        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
        if (pauseScreen != null)    {
            pauseScreen.dispose();
            pauseScreen = null;
        }
    }


    /** go to next level **/
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

        if(user.getCompletedLevels().size()==counter){
            user.saveUserData("user_data.ser");
            goToMenu();
        }

        else {
            goToGame();
        }
    }


//    /** restart game**/
//    /// helper method to clear temporary files.
//    public static void resetFile(String filePath) {
//        // added the channel lock to restrict access to this file while rewriting it
//        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
//             FileChannel channel = raf.getChannel();
//             FileLock lock = channel.lock()) {
//            // Truncate the file to zero length
//            raf.setLength(0);
//
//        } catch (IOException e) {
//            System.out.println("An error occurred while resetting the file: " + e.getMessage());
//        }
//    }
//
//    public void restartGame() {
//        /// dispose of the current game screen if necessary
//        if (getScreen() != null) {
//            getScreen().dispose();
//        }
//        /// reset objects-states in the map
//        resetFile("playerstate.txt");
//        System.out.println("wrote to file");
//        resetFile("enemystate.txt");
//        System.out.println("wrote to file");
//        // if the level isn't completed the goToGame will just go to the last unplayed level
//        goToGame();
//    }

    /// go to settings
    public void goToSettings()  {
        this.setScreen(new SettingsScreen(this));
        if (menuScreen != null) {
            menuScreen.dispose();
            menuScreen = null;
        }
    }

    /// go to Pause
//    public void goToPause() {
////        Vector2 positionOfThePlayerBeforePause = gameScreen.getPlayer().getPosition();
//
//        if (pauseScreen == null) {
//            pauseScreen = new PauseOverlay(this); // Reuse existing PauseOverlay
//        }
//        this.setScreen(pauseScreen);
//
//    }

    /// go to map selector
    public void goToLevelSelector() {
        if (levelSelectorScreen != null) {
            levelSelectorScreen.dispose(); // Create the level selector screen
        }
        this.setScreen(new LevelSelectorScreen(this)); // Set the current screen to LevelSelectorScreen
    }

    /** saves user data **/
    public void saveUserData() {
        if (user != null) {
            user.saveUserData("user_data.ser");
        }
    }

    /** sets user preferences **/
    public void setPreferences(Map<String, Object> preferences) {
        if (user != null) {
            user.setPreferences(preferences);
            saveUserData();  // Save preferences after setting them
        }
    }

    /// map loader
    public void loadLevel(String levelName) {
        // Load the level by loading the .tmx file
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load("assets/TiledMaps/" + levelName + ".tmx");


        this.setScreen(new GameScreen(this, "assets/TiledMaps/" + levelName + ".tmx",soundManager)); // Change to a GameScreen that uses the loaded map
    }

//    private void loadCharacterAnimation() {
//        Texture walkSheet = new Texture(Gdx.files.internal("TiledMaps/tilesets/newCaves/Hana Caraka - Base Character [sample]/walk.png"));
//
//        int frameWidth = 16, frameHeight = 16, walkAnimationFrames = 8, y = 32, idleAnimationFrames = 4;
//
//        for (int i = 0; i <= 2; i++)    {
//            // libGDX internal Array instead of ArrayList because of performance
//            Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
//
//            // Add all frames to the animation
//            int offset = 32;
//
//            for (int col = 0; col < walkAnimationFrames; col++) {
//                walkFrames.add(new TextureRegion(walkSheet, offset+col*(frameWidth+64), y, frameWidth, frameHeight));
//            }
//            switch (y)  {
//                case 112:
//                    characterDownAnimation = new Animation<>(0.05f, walkFrames);
//                    break;
//                case 32:
//                    characterRightAnimation = new Animation<>(0.05f, walkFrames);
//                    Array<TextureRegion> leftWalkFrames = new Array<>(TextureRegion.class);
//                    for (TextureRegion t : walkFrames)  {
//                        TextureRegion flippedFrame = new TextureRegion(t);
//                        flippedFrame.flip(true, false);
//                        leftWalkFrames.add(flippedFrame);
//                    }
//                    characterLeftAnimation = new Animation<>(0.05f, leftWalkFrames);
//                    break;
//                case 192:
//
//                    characterUpAnimation = new Animation<>(0.05f, walkFrames);
//                    break;
//            }
//            y += frameHeight + 64;
//        }
//
//        y=32;
//        Texture idleSheet = new Texture(Gdx.files.internal("TiledMaps/tilesets/newCaves/Hana Caraka - Base Character [sample]/idle.png"));
//
//        for (int i = 0; i <= 2; i++)    {
//            Array<TextureRegion> idleFrames = new Array<>(TextureRegion.class);
//
//            int offset = 32;
//
//            for (int col = 0; col < idleAnimationFrames; col++) {
//                idleFrames.add(new TextureRegion(idleSheet, offset+col*(frameWidth+64), y, frameWidth, frameHeight));
//            }
//            switch (y)  {
//                case 32:
//                    characterIdleAnimation = new Animation<>(0.1f, idleFrames);
//                    break;
//            }
//            y += frameHeight + 64;
//        }
//    }


    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("animations/player/FinalAnimAdjusted2.png"));

        int frameWidth = 64, frameHeight = 64, walkAnimationFrames = 9, idleAnimationFrames = 2, y = 0;

        for (int i = 0; i <= 50; i++)    {
            // libGDX internal Array instead of ArrayList because of performance
            Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> idleFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> attackLeftFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> runningframes = new Array<>(TextureRegion.class);


            for (int col = 0; col < walkAnimationFrames; col++) {
                walkFrames.add(new TextureRegion(walkSheet, col*(frameWidth), y, frameWidth, frameHeight));
            }
            for (int col = 0; col < idleAnimationFrames; col++) {
                idleFrames.add(new TextureRegion(walkSheet, col*(frameWidth), y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 6; col++) {
                attackLeftFrames.add(new TextureRegion(walkSheet, col*(frameWidth), y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 8; col++) {
                runningframes.add(new TextureRegion(walkSheet, col*(frameWidth), y, frameWidth, frameHeight));
            }
            switch (y)  {
                case 640:
                    characterDownAnimation = new Animation<>(0.05f, walkFrames);
                    break;
                case 704:
                    characterRightAnimation = new Animation<>(0.05f, walkFrames);
//                    Array<TextureRegion> leftWalkFrames = new Array<>(TextureRegion.class);
//                    for (TextureRegion t : walkFrames)  {
//                        TextureRegion flippedFrame = new TextureRegion(t);
//                        flippedFrame.flip(true, false);
//                        leftWalkFrames.add(flippedFrame);
//                    }
//                    characterLeftAnimation = new Animation<>(0.05f, leftWalkFrames);
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

    public Animation<TextureRegion> getRunUpAnimation() {
        return runUpAnimation;
    }

    public Animation<TextureRegion> getRunDownAnimation() {
        return runDownAnimation;
    }

    public Animation<TextureRegion> getRunLeftAnimation() {
        return runLeftAnimation;
    }

    public Animation<TextureRegion> getRunRightAnimation() {
        return runRightAnimation;
    }

    public Animation<TextureRegion> getcharacterUpAttackAnimation() {
        return characterUpAttackAnimation;
    }

    public Animation<TextureRegion> getcharacterDownAttackAnimation() {
        return characterDownAttackAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAttackAnimation() {
        return characterRightAttackAnimation;
    }

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

            getScreen().hide(); // Hide the current screen
            getScreen().dispose(); // Dispose the current screen
            spriteBatch.dispose(); // Dispose the spriteBatch
            skin.dispose(); // Dispose the skin
            soundManager.dispose();
    }

    // Getters methods

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterUpAttackAnimation() {
        return characterUpAttackAnimation;
    }

    public Animation<TextureRegion> getCharacterDownAttackAnimation() {
        return characterDownAttackAnimation;
    }

    public Animation<TextureRegion> getCharacterRightIdleAnimation() {
        return characterRightIdleAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftIdleAnimation() {
        return characterLeftIdleAnimation;
    }

    public Animation<TextureRegion> getCharacterDownIdleAnimation() {
        return characterDownIdleAnimation;
    }

    public Animation<TextureRegion> getCharacterUpIdleAnimation() {
        return characterUpIdleAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public void setBackgroundMusic(Music backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    public User getUser() {
        return user;
    }
}
