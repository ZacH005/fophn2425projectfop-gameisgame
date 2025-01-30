package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class VictoryScreen implements Screen {
    private Stage stage;
    private Stage stage2;
    private Table table;
    private Table table2;
    private Texture bg;
    private Texture overlayTexture;
    private ScreenManager game;
    private SoundManager soundManager;
    private Map<String,Integer> winState = new HashMap<String,Integer>();
    private int score;

    /**
     * Constructor for the VictoryScreen.
     *
     * @param game The main game class, used to access global resources and methods.
     * @param time The time taken to complete the level.
     */
    public VictoryScreen(ScreenManager game, float time) {
        this.game = game;
        this.soundManager = game.getSoundManager();
        winState.put("crackles", 0);
        winState.put("wind", 0);
        winState.put("piano", 0);
        winState.put("strings", 1);
        winState.put("pad", 0);
        winState.put("drums", 0);
        winState.put("bass", 0);
        winState.put("key_sound", 0);
        winState.put("slowerDrums", 1);
        soundManager.onGameStateChange(winState);
        score = (int) (10000 - Math.min(time * 50 - 300, 9999));
    }

    /**
     * Displays the victory screen, setting up the layout and buttons.
     */
    @Override
    public void show() {

        // Create a table for layout
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

        Label.LabelStyle style = new Label.LabelStyle(game.getSkin().getFont("title"), Color.WHITE);
        Label gameOverLabel = new Label("You Won", style);
        table2.add(gameOverLabel).padBottom((float) viewport.getScreenHeight() / 3);

        Label.LabelStyle normalStyle = new Label.LabelStyle(game.getSkin().getFont("font"), Color.WHITE);
        Label scoreLabel = new Label("Your Score: " + score, normalStyle);
        table.add(scoreLabel).padBottom(10).row();

        bg = new Texture(Gdx.files.internal("VictoryScreen.png"), true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"), true);

        FileHandle levelsDirectory = Gdx.files.local("assets/TiledMaps");
        int counter = 0;
        for (FileHandle file : levelsDirectory.list()) {
            if (file.extension().equals("tmx")) {
                counter++;
            }
        }
        if (game.getUser().getCompletedLevels().size() != counter) {
            TextButton nextLevelButton = new TextButton("Next Level", game.getSkin());
            nextLevelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToNextLevel();
                }
            });
            table.add(nextLevelButton).width(300).row();

            TextButton levelSelectorButton = new TextButton("Go to Levels", game.getSkin());
            levelSelectorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToLevelSelector();
                }
            });
            table.add(levelSelectorButton).width(300).row();
        } else {
            TextButton CreditsButton = new TextButton("Credits", game.getSkin());
            CreditsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.getUser().resetCompletedLevels();
                    game.goToCredits();
                }
            });
            table.add(CreditsButton).width(300).row();
        }
    }

    /**
     * Renders the victory screen.
     *
     * @param delta The time in seconds since the last render.
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
     * Resizes the stage when the screen size changes.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the resources used by the screen.
     */
    @Override
    public void dispose() {
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
