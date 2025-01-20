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

public class GameOverScreen implements Screen {
    private Stage stage;
    private Stage stage2;
    private Table table;
    private Table table2;
    private Texture bg;
    private Texture overlayTexture;
    private ScreenManager game; // Reference to game class
    private SoundManager soundManager;
    private Map<String,Integer> mainState;
    private Map<String,Integer> gameOverState = new HashMap<String,Integer>();
    private ParticleEffect particleEffect;

    // Pass the game instance in the constructor
    public GameOverScreen(ScreenManager game) {

        this.game = game; // Store the reference to the game
        soundManager = game.getSoundManager();
        mainState = game.getMainState();

        gameOverState.put("crackles",1);
        gameOverState.put("wind",1);
        gameOverState.put("piano",0);
        gameOverState.put("strings",0);
        gameOverState.put("pad",0);
        gameOverState.put("drums",0);
        gameOverState.put("bass",0);

        soundManager.onGameStateChange(gameOverState);

    }

    public Map<String, Integer> getGameOverState() {
        return gameOverState;
    }

    @Override
    public void show() {
        bg = new Texture(Gdx.files.internal("GameOverScreen.png"),true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"),true);
        // Create a table for layout
        var camera = new OrthographicCamera();
        camera.zoom = 1f;
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport,game.getSpriteBatch());
        stage2 = new Stage(viewport,game.getSpriteBatch());

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table2 = new Table();
        table2.setFillParent(true);
        table.setFillParent(true); // Make table fill the stage
        stage.addActor(table);
        stage2.addActor(table2);
        soundManager.onGameStateChange(gameOverState);
        table2.add(new Label("GAME OVER!", game.getSkin(), "title")).padBottom((float) viewport.getScreenHeight()/3);


// create and add the buttons
        TextButton retryButton = new TextButton("Retry", game.getSkin());
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                soundManager.onGameStateChange(mainState);
                game.goToGame();  // Calls the restart method on the game instance
            }
        });
        table.add(retryButton).width(300).row();

/// Menu Button
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

    @Override
    public void render(float delta) {
        soundManager.setKeySoundVolume(0);
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float scaleX = Gdx.graphics.getWidth() / (float) bg.getWidth();
        float scaleY = Gdx.graphics.getHeight() / (float) bg.getHeight();

//        float scaleX = viewport.getScreenWidth()/(float)bg.getWidth();
//        float scaleY = viewport.getScreenHeight()/(float)bg.getHeight();
        float scale = Math.min(scaleX, scaleY);
        float bgWidth = bg.getWidth() * scale;
        float bgHeight = bg.getHeight() * scale;
        float bgX = (Gdx.graphics.getWidth() - bgWidth) / 2f;
        float bgY = (Gdx.graphics.getHeight() - bgHeight) / 2f;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.getBatch().begin();

        stage.getBatch().draw(bg, bgX, bgY, bgWidth, bgHeight);
        stage.getBatch().end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage

        stage.getBatch().begin();
        // Draw the overlay texture slightly offset to center it on the cursor
//        stage.setDebugAll(true);
        stage.getBatch().setColor(1f,1f,1f,0.5f);
        stage.getBatch().draw(overlayTexture, mouseX - 3840/2f, mouseY -  2160/2f);
        stage.getBatch().setColor(1f,1f,1f,1f);
        stage.getBatch().end();


        stage2.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage2.draw(); // Draw the stage


    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport size if the window is resized
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // Dispose of the stage and resources when done
        stage.dispose();
    }

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
