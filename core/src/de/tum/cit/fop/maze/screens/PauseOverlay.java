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

/**
 * The PauseOverlay class represents the overlay shown when the game is paused.
 * It includes UI elements for resuming, restarting, adjusting sound and camera zoom settings,
 * and navigating to the main menu.
 */
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
    private Map<String, Integer> pauseState = new HashMap<String, Integer>();
    private Slider zoomSlider;

    /**
     * Constructs a PauseOverlay object that displays a pause menu with options
     * such as Resume, Restart, Go to Menu, and sound/zoom adjustments.
     *
     * @param gameScreen The GameScreen that this overlay is associated with.
     * @param game The main game manager used to navigate between screens.
     */
    public PauseOverlay(GameScreen gameScreen, ScreenManager game) {
        var camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));
        this.soundManager = gameScreen.getSoundManager();

        pauseState.put("crackles", 1);
        pauseState.put("wind", 1);
        pauseState.put("piano", 0);
        pauseState.put("strings", 0);
        pauseState.put("pad", 0);
        pauseState.put("drums", 0);
        pauseState.put("bass", 0);
        pauseState.put("key_sound", 0);

        overlay = new Image(new Texture(Gdx.files.internal("PauseMenuOverlay.png")));
        overlay.setFillParent(true);
        overlay.setColor(1, 0, 250, 0.02f);
        stage.addActor(overlay);
        soundManager.onGameStateChange(pauseState);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label label = new Label("Game Paused", skin);
        table.add(label).padBottom(80).row();

        if (musicSlider == null) {
            musicVolume = game.getPassedVolumeSettingToPause();
            soundManager.setSfxVolume(musicVolume);
        } else {
            musicVolume = musicSlider.getValue();
        }

        if (zoomSlider == null) {
            gameScreen.setCameraZoom(1f);
        } else {
            gameScreen.setCameraZoom(zoomSlider.getValue());
        }

        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundManager.playSound("click");
                setVisible(false);
                gameScreen.setPaused(false);
                soundManager.onGameStateChange(gameScreen.getMainState());
            }
        });
        table.add(resumeButton).width(300).row();

        TextButton restartButton = new TextButton("Restart", skin);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundManager.playSound("click");
                gameScreen.dispose();
                String mappath = gameScreen.getMapPath();
                game.setScreen(new GameScreen(game, mappath, soundManager));
            }
        });
        table.add(restartButton).width(300).row();

        TextButton goToMenuButton = new TextButton("Go To Menu", skin);
        goToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundManager.playSound("click");
                game.goToMenu();
            }
        });
        table.add(goToMenuButton).width(300).row();

        Label musicLabel = new Label("Sound", skin);
        table.add(musicLabel).padBottom(20).row();

        musicSlider = new Slider(0, 1, 0.1f, false, skin);
        musicSlider.setValue(musicVolume);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                soundManager.setSfxVolume(musicSlider.getValue());
                soundManager.setMusicVolume(musicSlider.getValue());
                musicVolume = musicSlider.getValue();
            }
        });
        table.add(musicSlider).width(300).row();

        Label zoomLabel = new Label("Camera Zoom", skin);
        table.add(zoomLabel).padBottom(20).row();

        zoomSlider = new Slider(0.2f, 1, 0.1f, false, skin);
        zoomSlider.setValue(gameScreen.getCameraZoom());
        zoomSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                gameScreen.setCameraZoom(zoomSlider.getValue());
            }
        });
        table.add(zoomSlider).width(300).row();

        lastWidth = Gdx.graphics.getWidth();
        lastHeight = Gdx.graphics.getHeight();
    }

    /**
     * Returns the current music volume setting.
     *
     * @return The current music volume as a float between 0 and 1.
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Renders the pause overlay, including handling any resizing.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void render(float delta) {
        int currentWidth = Gdx.graphics.getWidth();
        int currentHeight = Gdx.graphics.getHeight();

        if (currentWidth != lastWidth || currentHeight != lastHeight) {
            handleResize(currentWidth, currentHeight);
        }

        stage.act(delta);
        stage.draw();
    }

    /**
     * Handles resizing of the overlay when the window size changes.
     *
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    private void handleResize(int width, int height) {
        lastWidth = width;
        lastHeight = height;
        stage.getViewport().update(width, height, true);
        table.invalidateHierarchy();
    }

    /**
     * Disposes of the resources used by the overlay.
     */
    public void dispose() {
        stage.dispose();
    }

    /**
     * Sets the visibility of the overlay.
     *
     * @param visible True to show the overlay, false to hide it.
     */
    public void setVisible(boolean visible) {
        overlay.setVisible(visible);
        table.setVisible(visible);
        if (visible) {
            Gdx.input.setInputProcessor(stage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    public Map<String, Integer> getPauseState() {
        return pauseState;
    }
}
