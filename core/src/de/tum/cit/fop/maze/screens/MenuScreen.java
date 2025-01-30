package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.SoundManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private final Stage stage2;
    private SoundManager soundManager;
    private Map<String, Integer> menuState = new HashMap<String, Integer>();
    private SpriteBatch batch;
    private Texture overlayTexture;
    private Texture bg;
    private Table table;
    private Table table2;
    private Viewport viewport;
    private ScreenManager game;
    private ParticleEffect particleEffect;
    private float scale;
    private float bgWidth;
    private float bgHeight;
    private float bgX;
    private float bgY;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     * @param soundManager The sound manager used for playing sounds in the game.
     */
    public MenuScreen(ScreenManager game, SoundManager soundManager) {
        this.game = game;
        var camera = new OrthographicCamera();
        camera.zoom = 1f;

        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());
        stage2 = new Stage(viewport, game.getSpriteBatch());

        table = new Table();
        table2 = new Table();
        table2.setFillParent(true);
        table.setFillParent(true);

        stage.addActor(table);
        stage2.addActor(table2);

        this.soundManager = soundManager;
        soundManager.loadSound("click", "music/UI/menu_select.ogg");

        menuState.put("crackles", 1);
        menuState.put("wind", 1);
        menuState.put("piano", 1);
        menuState.put("strings", 0);
        menuState.put("pad", 0);
        menuState.put("drums", 0);
        menuState.put("bass", 0);
        menuState.put("slowerDrums", 1);

        table2.add(new Label("Salt Mine Dungeons!", game.getSkin(), "title")).padBottom((float) viewport.getScreenHeight() / 2);

        if (game.getUser().getCompletedLevels().isEmpty()) {
            TextButton startNewGameButton = new TextButton("Start New Game", game.getSkin());
            table.add(startNewGameButton).width(300).row();
            startNewGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundManager.playSound("click");
                    game.goToGame();
                }
            });
        } else {
            TextButton ContinueButton = new TextButton("Continue", game.getSkin());
            table.add(ContinueButton).width(300).row();
            ContinueButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundManager.playSound("click");
                    game.goToGame();
                }
            });
        }

        TextButton goToLevelSelectorButton = new TextButton("Levels", game.getSkin());
        table.add(goToLevelSelectorButton).width(300).row();
        goToLevelSelectorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                game.goToLevelSelector();
            }
        });
        soundManager.onGameStateChange(menuState);

        TextButton goToSettingsButton = new TextButton("Go To Settings", game.getSkin());
        table.add(goToSettingsButton).width(300).row();
        goToSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                game.goToSettings();
            }
        });

        TextButton goToCreditsButton = new TextButton("Credits", game.getSkin());
        table.add(goToCreditsButton).width(300).row();
        goToCreditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                game.goToCredits();
            }
        });

        TextButton exitGameButton = new TextButton("Exit Game", game.getSkin());
        table.add(exitGameButton).width(300).row();
        exitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                Gdx.app.exit();
            }
        });

        bg = new Texture(Gdx.files.internal("Menu UI Design.png"), true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"), true);
        table.padTop(100);
    }

    /**
     * Renders the menu screen.
     *
     * @param delta The time in seconds since the last render call.
     */
    @Override
    public void render(float delta) {
        soundManager.setKeySoundVolume(0);

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float scaleX = Gdx.graphics.getWidth() / (float) bg.getWidth();
        float scaleY = Gdx.graphics.getHeight() / (float) bg.getHeight();
        scale = Math.min(scaleX, scaleY);
        bgWidth = bg.getWidth() * scale;
        bgHeight = bg.getHeight() * scale;
        bgX = (Gdx.graphics.getWidth() - bgWidth) / 2f;
        bgY = (Gdx.graphics.getHeight() - bgHeight) / 2f;

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
     * Called when the screen is resized.
     *
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of resources when the screen is disposed.
     */
    @Override
    public void dispose() {
        overlayTexture.dispose();
        bg.dispose();
        stage.dispose();
    }

    /**
     * Called when the screen is shown.
     */
    @Override
    public void show() {
        soundManager.setKeySoundVolume(0f);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/effects/Particle Park Flame.p"), Gdx.files.internal("particles/images"));
        particleEffect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - 100);
        particleEffect.scaleEffect(5f);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Called when the screen is paused.
     */
    @Override
    public void pause() {
    }

    /**
     * Called when the screen is resumed.
     */
    @Override
    public void resume() {
    }

    /**
     * Called when the screen is hidden.
     */
    @Override
    public void hide() {
    }
}
