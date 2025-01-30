package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Stage stage2;
    private Table table;
    private Table table2;
    private Texture bg;
    private Texture overlayTexture;
    private SoundManager soundManager;
    private Slider music;
    private ParticleEffect particleEffect;

    /**
     * Constructor for SettingsScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public SettingsScreen(ScreenManager game) {
        var camera = new OrthographicCamera();
        camera.zoom = 1f;

        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());
        stage2 = new Stage(viewport, game.getSpriteBatch());
        table = new Table();
        table2 = new Table();
        table2.setFillParent(true);
        table.setFillParent(true);
        stage.addActor(table);
        stage2.addActor(table2);

        soundManager = game.getSoundManager();

        table2.add(new Label("Settings", game.getSkin(), "title")).padBottom((float) viewport.getScreenHeight() / 2);

        music = new Slider(0, 1, 0.1f, false, game.getSkin());
        music.setValue(game.getSoundManager().getMusicVolume());
        table.add(music).width(300);
        music.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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
                game.goToMenu();
            }
        });
    }

    /**
     * Sets the music slider.
     *
     * @param music The slider for adjusting music volume.
     */
    public void setMusic(Slider music) {
        this.music = music;
    }

    /**
     * Returns the music slider.
     *
     * @return The slider for adjusting music volume.
     */
    public Slider getMusic() {
        return music;
    }

    /**
     * Renders the Settings screen, drawing the background, particles, and UI elements.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float scaleX = Gdx.graphics.getWidth() / (float) bg.getWidth();
        float scaleY = Gdx.graphics.getHeight() / (float) bg.getHeight();
        float scale = Math.min(scaleX, scaleY);
        float bgWidth = bg.getWidth() * scale;
        float bgHeight = bg.getHeight() * scale;
        float bgX = (Gdx.graphics.getWidth() - bgWidth) / 2f;
        float bgY = (Gdx.graphics.getHeight() - bgHeight) / 2f;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();

        stage.getBatch().draw(bg, bgX, bgY, bgWidth, bgHeight);

        particleEffect.start();
        particleEffect.draw(stage.getBatch(), delta);
        particleEffect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - 100);

        stage.getBatch().end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        stage.getBatch().begin();
        stage.getBatch().setColor(1f, 1f, 1f, 0.5f);
        stage.getBatch().draw(overlayTexture, mouseX - 3840 / 2f, mouseY - 2160 / 2f);
        stage.getBatch().setColor(1f, 1f, 1f, 1f);

        stage.getBatch().end();

        stage2.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage2.draw();
    }

    /**
     * Resizes the stage to fit the new screen dimensions.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of resources when the screen is no longer needed.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Initializes assets and input processor when the screen is shown.
     */
    @Override
    public void show() {
        soundManager.setKeySoundVolume(0f);
        bg = new Texture(Gdx.files.internal("Menu UI Design.png"), true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"), true);
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/effects/Particle Park Flame.p"), Gdx.files.internal("particles/images"));
        particleEffect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - 100);
        particleEffect.scaleEffect(5f);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Pauses the screen (not implemented).
     */
    @Override
    public void pause() {
    }

    /**
     * Resumes the screen (not implemented).
     */
    @Override
    public void resume() {
    }

    /**
     * Hides the screen (not implemented).
     */
    @Override
    public void hide() {
    }
}
