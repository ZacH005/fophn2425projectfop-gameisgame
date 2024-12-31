package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.entity.User;

public class LevelSelectorScreen extends ScreenAdapter {
    private Stage stage;
    private MazeRunnerGame game;
    private User user;

    public LevelSelectorScreen(MazeRunnerGame game) {
        this.game = game;
        this.user = game.getUser(); // Assume the game class provides access to the user

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;

        // Create a viewport with the camera
        Viewport viewport = new ScreenViewport(camera);

        // Create a stage for UI elements
        stage = new Stage(viewport, game.getSpriteBatch());

        // Create a table for layout
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label for the title
        table.add(new Label("Select Level", game.getSkin(), "title")).padBottom(80).row();

        // Load .tmx files from the levels directory
        loadLevelButtons(table);
    }

    private void loadLevelButtons(Table table) {
        // Get all .tmx files in the "TiledMaps" directory
        FileHandle levelsDirectory = Gdx.files.local("assets/TiledMaps");
        Array<FileHandle> tmxFiles = new Array<>();
        for (FileHandle file : levelsDirectory.list()) {
            if (file.extension().equals("tmx")) {
                tmxFiles.add(file);
            }
        }

        // If no levels are found, show a message
        if (tmxFiles.size == 0) {
            table.add(new Label("No levels yet, care to add some?", game.getSkin())).row();
            return;
        }

        // Create buttons for each .tmx file found
        for (FileHandle file : tmxFiles) {
            final String levelName = file.nameWithoutExtension(); // Get the level name without extension
            TextButton levelButton = new TextButton(levelName, game.getSkin());

            // Disable the button if the level is completed
            if (!user.getCompletedLevels().contains(levelName)) {
                levelButton.setDisabled(true); // Disable button for completed level
            }

            table.add(levelButton).width(300).padBottom(10).row();

            // Set listener for each level button
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!levelButton.isDisabled()) {
                        // Load the corresponding level by its .tmx file
                        game.loadLevel(levelName);
                        System.out.println("Level " + levelName + " selected");
                    }
                }
            });
        }

        // Button to go back to the menu
        TextButton goBackButton = new TextButton("Back to Menu", game.getSkin());
        table.add(goBackButton).width(300).row();
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Return to the main menu
            }
        });
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
        stage.dispose(); // Dispose of the stage when screen is disposed
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Set the input processor so the stage can receive input events
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
