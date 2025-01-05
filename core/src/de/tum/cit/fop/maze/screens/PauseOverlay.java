package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;

public class PauseOverlay {
    private Stage stage;
    private Image overlay;
    private Skin skin;
    private Table table;

    private int lastWidth;
    private int lastHeight;

    public PauseOverlay(GameScreen gameScreen, ScreenManager game) {
        // Initialize stage and skin
        var camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));

        // Create the overlay
        overlay = new Image(new Texture(Gdx.files.internal("PauseMenuOverlay.png")));
        overlay.setFillParent(true);
        overlay.setColor(1, 0, 250, 0.02f); // Set transparency
        stage.addActor(overlay);

        // Create and configure the table for UI elements
        table = new Table();
        table.setFillParent(true); // Center the table
        stage.addActor(table);

        // Add label
        Label label = new Label("Game Paused", skin);
        table.add(label).padBottom(80).row();

        // Add Resume button
        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Resume clicked");
                setVisible(false);
                gameScreen.setPaused(false);
            }
        });
        table.add(resumeButton).width(300).row();


        //Menu button
        TextButton restartButton = new TextButton("Restart", skin);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Restart clicked");
                if (gameScreen != null) {
                    gameScreen.dispose();
                }
                game.goToGame();
            }
        });
        table.add(restartButton).width(300).row();


        //Menu button
        TextButton goToMenuButton = new TextButton("Go To Menu", skin);
        goToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Go To Menu clicked");
                if (gameScreen != null) {
                    gameScreen.dispose();
                }
                game.goToMenu();
            }
        });
        table.add(goToMenuButton).width(300).row();


        Label musicLabel = new Label("Sound", skin);
        table.add(musicLabel).padBottom(20).row();

        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(0.5f); // Default value (50%)
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Update the volume when the slider value changes
                float volume = musicSlider.getValue();
                setMusicVolume(volume,game);
            }
        });
        table.add(musicSlider).width(300).row();



    // Initialize dimensions
    lastWidth =Gdx.graphics.getWidth();
    lastHeight =Gdx.graphics.getHeight();

    }
    private void setMusicVolume(float volume,ScreenManager game) {
        // Here, use a music player or sound manager to adjust the music volume
        // For example, if you use libGDX's SoundManager:
        // Gdx.audio.newMusic("your_music_file.mp3").setVolume(volume);
        // or use a sound manager class to control the volume.
//            game.getBackgroundMusic().setVolume(volume);
    }

    public void render(float delta) {
        // Check for resize
        int currentWidth = Gdx.graphics.getWidth();
        int currentHeight = Gdx.graphics.getHeight();

        if (currentWidth != lastWidth || currentHeight != lastHeight) {
            handleResize(currentWidth, currentHeight);
        }

        // Draw the stage (includes overlay and UI elements)
        stage.act(delta);
        stage.draw();
    }
    // this to force the table to recalculate its dimensions
    // and move the buttons in the overlay correctly
    // we call it for each frame since render is called each frame.

    private void handleResize(int width, int height) {
        // Update the last known dimensions
        lastWidth = width;
        lastHeight = height;

        // Update stage viewport and layout
        stage.getViewport().update(width, height, true);
        table.invalidateHierarchy(); // Force the table to recalculate its layout
    }

    public void dispose() {
        stage.dispose();
    }

    public void setVisible(boolean visible) {
        overlay.setVisible(visible);
        table.setVisible(visible); // Show/hide the table with buttons
        if (visible) {
            Gdx.input.setInputProcessor(stage); // Enable input for the overlay
        } else {
            Gdx.input.setInputProcessor(null); // Reset input processor for the game
        }
    }
}
