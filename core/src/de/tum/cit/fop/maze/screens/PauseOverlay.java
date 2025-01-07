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
import de.tum.cit.fop.maze.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class PauseOverlay {
    private Stage stage;
    private Image overlay;
    private Skin skin;
    private Table table;

    private int lastWidth;
    private int lastHeight;
    private SoundManager soundManager;
    private Slider musicSlider;
    private float musicVolume;
    private Map<String,Integer> pauseState = new HashMap<String,Integer>();

    public Map<String, Integer> getPauseState() {
        return pauseState;
    }

    public PauseOverlay(GameScreen gameScreen, ScreenManager game) {
        // Initialize stage and skin
        var camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));
        this.soundManager = gameScreen.getSoundManager();

        pauseState.put("crackles",1);
        pauseState.put("wind",1);
        pauseState.put("piano",0);
        pauseState.put("strings",0);
        pauseState.put("pad",0);
        pauseState.put("drums",0);
        pauseState.put("bass",0);


        // Create the overlay
        overlay = new Image(new Texture(Gdx.files.internal("PauseMenuOverlay.png")));
        overlay.setFillParent(true);
        overlay.setColor(1, 0, 250, 0.02f); // Set transparency
        stage.addActor(overlay);
        soundManager.onGameStateChange(pauseState);
        // Create and configure the table for UI elements
        table = new Table();
        table.setFillParent(true); // Center the table
        stage.addActor(table);

        // Add label
        Label label = new Label("Game Paused", skin);
        table.add(label).padBottom(80).row();

        if(musicSlider==null){
            if(game.getPassedVolumeSettingToPause()!=0.1234f){
                musicVolume = game.getPassedVolumeSettingToPause();
            }
            else {
                musicVolume = 0.5f;
            }
        }
        else {
            musicVolume = musicSlider.getValue();
        }

        // Add Resume button
        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Resume clicked");
                soundManager.playSound("click");
                setVisible(false);
                gameScreen.setPaused(false);
                soundManager.onGameStateChange(gameScreen.getMainState());
            }
        });
        table.add(resumeButton).width(300).row();


        //Menu button
        TextButton restartButton = new TextButton("Restart", skin);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Restart clicked");
                soundManager.playSound("click");
                gameScreen.dispose();
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
                soundManager.playSound("click");
                gameScreen.dispose();
                game.goToMenu();
            }
        });
        table.add(goToMenuButton).width(300).row();


        Label musicLabel = new Label("Sound", skin);
        table.add(musicLabel).padBottom(20).row();

        musicSlider = new Slider(0, 1, 0.1f, false, skin);
        musicSlider.setValue(musicVolume); // Default value (50%)
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Update the volume when the slider value changes
                     soundManager.playSound("click");
                     soundManager.setSfxVolume(musicSlider.getValue());
                     soundManager.setMusicVolume(musicSlider.getValue());
                     musicVolume = musicSlider.getValue();

            }
        });
        table.add(musicSlider).width(300).row();



    // Initialize dimensions
    lastWidth =Gdx.graphics.getWidth();
    lastHeight =Gdx.graphics.getHeight();

    }
    public float getMusicVolume() {
        return musicVolume;
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
