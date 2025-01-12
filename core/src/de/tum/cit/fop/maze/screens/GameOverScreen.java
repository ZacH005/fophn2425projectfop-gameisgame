package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private ScreenManager game; // Reference to game class
    private SoundManager soundManager;
    private Map<String,Integer> mainState;
    private Map<String,Integer> gameOverState = new HashMap<String,Integer>();

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
        gameOverState.put("key_sound",0);
        soundManager.onGameStateChange(gameOverState);

    }

    public Map<String, Integer> getGameOverState() {
        return gameOverState;
    }

    @Override
    public void show() {
        // Create a table for layout
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport,game.getSpriteBatch());
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true); // Make table fill the stage
        stage.addActor(table);

        soundManager.onGameStateChange(gameOverState);
        Label.LabelStyle style = new Label.LabelStyle(game.getSkin().getFont("title"), Color.WHITE); // Access skin from game
        Label gameOverLabel = new Label("GAME OVER", style);


        table.add(gameOverLabel).padBottom(80).row();

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
        // Clear the screen with black color
        ScreenUtils.clear(Color.BLACK);

        // Draw the stage (which includes the table with the label)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // Update the stage
        stage.draw();  // Draw the stage
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
