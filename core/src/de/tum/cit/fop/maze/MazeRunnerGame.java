package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.arbitrarymap.RenderMap;
import de.tum.cit.fop.maze.entity.Entity;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.entity.User;
import de.tum.cit.fop.maze.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import jdk.jfr.StackTrace;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
//bananas comment
/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;
    private PauseScreen pauseScreen;
    private LevelSelectorScreen levelSelectorScreen;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private Animation<TextureRegion> characterIdleAnimation;

    // Music
    private Music backgroundMusic;
    //A User
    private User user;
    private int indexOfTheMapBeingPlayed;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        user = User.loadUserData("user_data.ser"); // Load existing user data, or use a default constructor to create a new one

        if (user == null) {
            user = new User("Player1");  // Set default username if no data found
        }

        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation

        // Play some background music
        // Background sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/main ost.mp3"));
        backgroundMusic.setLooping(true);

        //Paused music cause it was annoying
        backgroundMusic.setVolume(0);
        backgroundMusic.play();

        goToMenu(); // Navigate to the menu screen
    }
    /** restarts game **/

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
    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    public void goToGame() {
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

            if(user.getCompletedLevels().contains(file.name())){
                continue;
            }
            else if(user.getCompletedLevels().size()==tmxFiles.size){
                user.getCompletedLevels().clear();
                user.saveUserData("user_data.ser");
                this.setScreen(new MenuScreen(this));
            }
            else{
                indexOfTheMapBeingPlayed = tmxFiles.indexOf(file,false);
                this.setScreen(new GameScreen(this, ("TiledMaps/"+file.name()) ));
            }
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
        //get the files
        FileHandle levelsDirectory = Gdx.files.local("assets/TiledMaps");
        Array<FileHandle> tmxFiles = new Array<>();
        for (FileHandle file : levelsDirectory.list()) {
            if (file.extension().equals("tmx")) {
                tmxFiles.add(file);
            }
        }
        // get the one we played, add it to the user data, load the next one in the array and update the pointer to the current level being played
        try{
            user.getCompletedLevels().add(tmxFiles.get(indexOfTheMapBeingPlayed).name());
            user.saveUserData("user_data.ser");
            indexOfTheMapBeingPlayed++;
            System.out.println("finished"+ tmxFiles.get(indexOfTheMapBeingPlayed).name());
            this.setScreen(new GameScreen(this, ("TiledMaps/"+tmxFiles.get(indexOfTheMapBeingPlayed).name())));
            System.out.println("started" + tmxFiles.get(indexOfTheMapBeingPlayed+1).name());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /** restart game**/
    /// helper method to clear temporary files.
    public static void resetFile(String filePath) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("playerstate.txt", false));
            // Open the file in non-append mode and write nothing to it
            writer.write("");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while resetting the file: " + e.getMessage());
        }

    }

    public void restartGame() {
        /// dispose of the current game screen if necessary
        if (getScreen() != null) {
            getScreen().dispose();
        }
/// reset objects-states in the map
        resetFile("playerstate.txt");
        resetFile("enemystate.txt");

        goToGame();
    }


    public void goToSettings()  {
        this.setScreen(new SettingsScreen(this));
        if (menuScreen != null) {
            menuScreen.dispose();
            menuScreen = null;
        }
    }

    public void goToPause() {
//        Vector2 positionOfThePlayerBeforePause = gameScreen.getPlayer().getPosition();
        if (pauseScreen == null) {
            pauseScreen = new PauseScreen(this); // Reuse existing PauseScreen
        }
        this.setScreen(pauseScreen);
    }
/// go to map selector
    public void goToLevelSelector() {
        if (levelSelectorScreen == null) {
            levelSelectorScreen = new LevelSelectorScreen(this); // Create the level selector screen
        }
        this.setScreen(levelSelectorScreen); // Set the current screen to LevelSelectorScreen
    }
    /// map loader
    public void loadLevel(String levelName) {
        // Load the level by loading the .tmx file
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load("assets/TiledMaps/" + levelName + ".tmx");


        this.setScreen(new GameScreen(this, "assets/TiledMaps/" + levelName + ".tmx")); // Change to a GameScreen that uses the loaded map
    }

    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("TiledMaps/tilesets/newCaves/Hana Caraka - Base Character [sample]/walk.png"));

        int frameWidth = 16, frameHeight = 16, walkAnimationFrames = 8, y = 32, idleAnimationFrames = 4;

        for (int i = 0; i <= 2; i++)    {
            // libGDX internal Array instead of ArrayList because of performance
            Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

            // Add all frames to the animation
            int offset = 32;

            for (int col = 0; col < walkAnimationFrames; col++) {
                walkFrames.add(new TextureRegion(walkSheet, offset+col*(frameWidth+64), y, frameWidth, frameHeight));
            }
            switch (y)  {
                case 112:
                    characterDownAnimation = new Animation<>(0.05f, walkFrames);
                    break;
                case 32:
                    characterRightAnimation = new Animation<>(0.05f, walkFrames);
                    Array<TextureRegion> leftWalkFrames = new Array<>(TextureRegion.class);
                    for (TextureRegion t : walkFrames)  {
                        TextureRegion flippedFrame = new TextureRegion(t);
                        flippedFrame.flip(true, false);
                        leftWalkFrames.add(flippedFrame);
                    }
                    characterLeftAnimation = new Animation<>(0.05f, leftWalkFrames);
                    break;
                case 192:

                    characterUpAnimation = new Animation<>(0.05f, walkFrames);
                    break;
            }
            y += frameHeight + 64;
        }

        y=32;
        Texture idleSheet = new Texture(Gdx.files.internal("TiledMaps/tilesets/newCaves/Hana Caraka - Base Character [sample]/idle.png"));

        for (int i = 0; i <= 2; i++)    {
            Array<TextureRegion> idleFrames = new Array<>(TextureRegion.class);

            int offset = 32;

            for (int col = 0; col < idleAnimationFrames; col++) {
                idleFrames.add(new TextureRegion(idleSheet, offset+col*(frameWidth+64), y, frameWidth, frameHeight));
            }
            switch (y)  {
                case 32:
                    characterIdleAnimation = new Animation<>(0.1f, idleFrames);
                    break;
            }
            y += frameHeight + 64;
        }
    }
    // completing a certain level
    public void markLevelAsCompleted(String levelFileName) {
        if (user != null) {
            user.addCompletedLevel(levelFileName);
            user.saveUserData("user_data.ser");  // Save after adding a completed level
        }
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

    }

    // Getter methods
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

    public Animation<TextureRegion> getCharacterIdleAnimation() {
        return characterIdleAnimation;
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
