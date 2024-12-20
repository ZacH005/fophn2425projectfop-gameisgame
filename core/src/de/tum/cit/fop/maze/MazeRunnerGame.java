package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.arbitrarymap.RenderMap;
import de.tum.cit.fop.maze.screens.GameScreen;
import de.tum.cit.fop.maze.screens.MenuScreen;
import de.tum.cit.fop.maze.screens.PauseScreen;
import de.tum.cit.fop.maze.screens.SettingsScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
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
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation

        // Play some background music
        // Background sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/main ost.mp3"));
        backgroundMusic.setLooping(true);

        //Paused music cause it was annoying
        backgroundMusic.play();

        goToMenu(); // Navigate to the menu screen
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

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        this.setScreen(new GameScreen(this)); // Set the current screen to GameScreen
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
        if (pauseScreen != null)    {
            pauseScreen.dispose();
            pauseScreen = null;
        }
    }

    public void goToSettings()  {
        this.setScreen(new SettingsScreen(this));
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    public void goToPause() {
        this.setScreen(new PauseScreen(this));
    }

    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("templateAssets/character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;
        int y = 0;

        for (int i = 0; i <= 3; i++)    {
            // libGDX internal Array instead of ArrayList because of performance
            Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

            // Add all frames to the animation
            for (int col = 0; col < animationFrames; col++) {
                walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            switch (y)  {
                case 0:
                    characterDownAnimation = new Animation<>(0.1f, walkFrames);
                    characterIdleAnimation = new Animation<>(0.0f, new TextureRegion(walkSheet, 0, y, frameWidth, frameHeight));
                    break;
                case 32:
                    characterRightAnimation = new Animation<>(0.1f, walkFrames);
                    break;
                case 64:
                    characterUpAnimation = new Animation<>(0.1f, walkFrames);
                    break;
                case 96:
                    characterLeftAnimation = new Animation<>(0.1f, walkFrames);
                    break;
            }
            y += 32;
        }
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
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
}
