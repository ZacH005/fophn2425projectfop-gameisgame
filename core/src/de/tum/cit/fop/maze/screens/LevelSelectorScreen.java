package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.entity.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * The LevelSelectorScreen class displays the level selection menu, allowing users to select and play available levels.
 * It also manages sound and particle effects associated with the screen.
 */
public class LevelSelectorScreen extends ScreenAdapter {
    private Stage stage;
    private Stage stage2;
    private Table table;
    private Table table2;
    private ScreenManager game;
    private User user;
    private SoundManager soundManager;
    private Texture bg;
    private Texture overlayTexture;
    private ParticleEffect particleEffect;
    Map<String, Integer> menuState = new HashMap<String, Integer>();

    /**
     * Constructs a LevelSelectorScreen.
     *
     * @param game The ScreenManager instance for managing the game's screens.
     */
    public LevelSelectorScreen(ScreenManager game) {
        this.game = game;
        this.user = game.getUser();

        var camera = new OrthographicCamera();
        camera.zoom = 1f;

        Viewport viewport = new ScreenViewport(camera);

        stage = new Stage(viewport, game.getSpriteBatch());
        stage2 = new Stage(viewport, game.getSpriteBatch());
        this.soundManager = game.getSoundManager();

        menuState.put("crackles", 1);
        menuState.put("wind", 1);
        menuState.put("piano", 1);
        menuState.put("strings", 0);
        menuState.put("pad", 0);
        menuState.put("drums", 0);
        menuState.put("bass", 0);
        menuState.put("slowerDrums", 1);

        soundManager.onGameStateChange(menuState);

        table = new Table();
        table2 = new Table();

        table.setFillParent(true);
        table2.setFillParent(true);

        stage.addActor(table);
        stage2.addActor(table2);

        table2.add(new Label("Select Level", game.getSkin(), "title")).padBottom((float) viewport.getScreenHeight() / 1.3f);

        loadLevelButtons(table);
    }

    /**
     * Loads the level buttons from the available .tmx files and adds them to the UI.
     *
     * @param table The table to which the level buttons will be added.
     */
    private void loadLevelButtons(Table table) {
        FileHandle levelsDirectory = Gdx.files.local("assets/TiledMaps");
        Array<FileHandle> tmxFiles = new Array<>();

        for (FileHandle file : levelsDirectory.list()) {
            if (file.extension().equals("tmx")) {
                tmxFiles.add(file);
            }
        }

        tmxFiles.sort(Comparator.comparing(file -> file.nameWithoutExtension()));

        if (tmxFiles.size == 0) {
            table.add(new Label("No levels yet, care to add some?", game.getSkin())).row();
            return;
        }

        if (tmxFiles.size == user.getCompletedLevels().size()) {
            user.resetCompletedLevels();
            System.out.println("reset all maps");
        }

        for (FileHandle file : tmxFiles) {
            final String levelName = file.nameWithoutExtension();
            TextButton levelButton = new TextButton(levelName, game.getSkin());

            if (!user.getCompletedLevels().contains(levelName + ".tmx")) {
                levelButton.setDisabled(true);
            }

            table.add(levelButton).width(300).padBottom(10).row();

            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!levelButton.isDisabled()) {
                        soundManager.playSound("click");
                        game.loadLevel(levelName);
                    }
                }
            });
        }

        TextButton goBackButton = new TextButton("Back to Menu", game.getSkin());
        table.add(goBackButton).width(300).row();
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                game.goToMenu();
            }
        });
    }

    /**
     * Renders the screen, including particle effects and UI elements.
     *
     * @param delta The time elapsed since the last render.
     */
    @Override
    public void render(float delta) {
        soundManager.setKeySoundVolume(0f);
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
     * Resizes the viewport when the screen is resized.
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
        stage.dispose();
    }

    /**
     * Sets up resources and input processors when the screen is shown.
     */
    @Override
    public void show() {
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/effects/Particle Park Flame.p"), Gdx.files.internal("particles/images"));
        particleEffect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - 100);
        particleEffect.scaleEffect(5f);

        bg = new Texture(Gdx.files.internal("Menu UI Design.png"), true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"), true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
