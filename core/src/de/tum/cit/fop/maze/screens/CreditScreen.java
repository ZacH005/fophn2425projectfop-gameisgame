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
 * The CreditScreen class displays the credit screen with buttons and background.
 */
public class CreditScreen implements Screen {
    private Stage stage;
    private Stage stage2;
    private Table table;
    private Table table2;
    private Texture bg;
    private Texture overlayTexture;
    private ScreenManager game;
    private SoundManager soundManager;
    private Map<String, Integer> mainState;
    private Map<String, Integer> CreditState = new HashMap<String, Integer>();
    private ParticleEffect particleEffect;

    /**
     * Constructs the CreditScreen with the given game instance.
     *
     * @param game The game instance to interact with the screen and sound manager.
     */
    public CreditScreen(ScreenManager game) {
        this.game = game;
        soundManager = game.getSoundManager();
        mainState = game.getMainState();

        CreditState.put("crackles", 1);
        CreditState.put("wind", 1);
        CreditState.put("piano", 1);
        CreditState.put("strings", 1);
        CreditState.put("pad", 1);
        CreditState.put("drums", 1);
        CreditState.put("bass", 1);
        CreditState.put("slowerDrums", 0);
        soundManager.onGameStateChange(CreditState);
    }

    /**
     * Returns the current state of the credit screen.
     *
     * @return A map representing the current credit state.
     */
    public Map<String, Integer> getGameOverState() {
        return CreditState;
    }

    /**
     * Initializes the screen, sets up the layout, and creates the buttons.
     */
    @Override
    public void show() {
        bg = new Texture(Gdx.files.internal("CreditScreen.png"), true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"), true);
        var camera = new OrthographicCamera();
        camera.zoom = 1f;
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());
        stage2 = new Stage(viewport, game.getSpriteBatch());

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        soundManager.onGameStateChange(CreditState);

        TextButton retryButton = new TextButton("Menu", game.getSkin());
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                soundManager.onGameStateChange(mainState);
                game.goToMenu();
            }
        });
        table.add(retryButton).width(300).padBottom(200);
        table.bottom();
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
