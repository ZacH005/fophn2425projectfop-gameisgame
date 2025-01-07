package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import de.tum.cit.fop.maze.SoundManager;

public class SettingsScreen implements Screen {
    private final Stage stage;
    private SoundManager soundManager;
    private Slider music;
    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public SettingsScreen(ScreenManager game) {
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        soundManager = game.getSoundManager();


        // Add a label as a title
        table.add(new Label("Settings", game.getSkin(), "title")).padBottom(80).row();

        music = new Slider(0, 1, 0.3f, false, game.getSkin());

        music.setValue(game.getSoundManager().getMusicVolume());
        table.add(music).width(300);
        music.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                game.getBackgroundMusic().setVolume(music.getValue());
                soundManager.playSound("click");
                soundManager.setSfxVolume(music.getValue());
                soundManager.setMusicVolume(music.getValue());
            }
        });

        TextButton muteMusicButton = new TextButton("Mute Music", game.getSkin());
        table.add(muteMusicButton).width(300).row();
        muteMusicButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                    soundManager.setSfxVolume(0.0f);
                    soundManager.setMusicVolume(0.0f);
                    music.setValue(0.0f);


            }
        });

        TextButton goToMenuButton = new TextButton("Back To Menu", game.getSkin());
        table.add(goToMenuButton).width(300).row();
        goToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                soundManager.setMusicVolume(music.getValue());
                soundManager.setSfxVolume(music.getValue());
                game.setPassedVolumeSettingToPause(music.getValue());
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });
    }

    public void setMusic(Slider music) {
        this.music = music;
    }

    public Slider getMusic() {
        return music;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
