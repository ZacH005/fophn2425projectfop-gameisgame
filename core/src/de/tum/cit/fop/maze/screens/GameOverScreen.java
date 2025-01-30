package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.SoundManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The GameOverScreen class displays the game over screen with buttons for retrying or going to the menu.
 */
public class GameOverScreen implements Screen {
    private Stage stage;
    private Stage stage2;
    private Table table;
    private Table table2;
    private Texture bg;
    private Texture overlayTexture;
    private ScreenManager game;
    private SoundManager soundManager;
    private Map<String, Integer> mainState;
    private Map<String, Integer> gameOverState = new HashMap<String, Integer>();
    private ParticleEffect particleEffect;

    /**
     * Constructs the GameOverScreen with the given game instance.
     *
     * @param game The game instance to interact with the screen and sound manager.
     */
    public GameOverScreen(ScreenManager game) {
        this.game = game;
        soundManager = game.getSoundManager();
        mainState = game.getMainState();

        gameOverState.put("crackles", 1);
        gameOverState.put("wind", 1);
        gameOverState.put("piano", 0);
        gameOverState.put("strings", 0);
        gameOverState.put("pad", 0);
        gameOverState.put("drums", 0);
        gameOverState.put("bass", 0);
        gameOverState.put("slowerDrums", 1);
        soundManager.onGameStateChange(gameOverState);
    }

    /**
     * Returns the current state of the game over screen.
     *
     * @return A map representing the current game over state.
     */
    public Map<String, Integer> getGameOverState() {
        return gameOverState;
    }

    /**
     * Initializes the screen, sets up the layout, and creates the buttons.
     */
    @Override
    public void show() {
        bg = new Texture(Gdx.files.internal("GameOverScreen.png"), true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"), true);
        var camera = new OrthographicCamera();
        camera.zoom = 1f;
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());
        stage2 = new Stage(viewport, game.getSpriteBatch());

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table2 = new Table();
        table2.setFillParent(true);
        table.setFillParent(true);
        stage.addActor(table);
        stage2.addActor(table2);
        soundManager.onGameStateChange(gameOverState);
        table2.add(new Label("GAME OVER!", game.getSkin(), "title")).padBottom((float) viewport.getScreenHeight() / 3);

        TextButton retryButton = new TextButton("Retry", game.getSkin());
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                soundManager.onGameStateChange(mainState);
                game.goToGame();
            }
        });
        table.add(retryButton).width(300).row();

        TextButton menuButton = new TextButton("Go to Menu", game.getSkin());
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                soundManager.setMusicVolume(0.5f);
                game.goToMenu();
            }
        });
        table.add(menuButton).width(300).row();
    }

    /**
     * Renders the screen and updates the elements on it.
     *
     * @param delta The time elapsed since the last render.
     */
    @Override
    public void render(float delta) {
        soundManager.setKeySoundVolume(0);
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
     * Resizes the viewport when the screen is resized.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the resources used by the screen when it's no longer needed.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Pauses the screen, typically when the game is not active.
     */
    @Override
    public void pause() {
    }

    /**
     * Resumes the screen, typically after being paused.
     */
    @Override
    public void resume() {
    }

    /**
     * Hides the screen, typically when transitioning to a different screen.
     */
    @Override
    public void hide() {
    }
}
