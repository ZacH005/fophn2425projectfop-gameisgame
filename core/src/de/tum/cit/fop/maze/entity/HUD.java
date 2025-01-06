package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;

public class HUD {
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countdownLabel;
    Label scoreLabel1;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label scoreLabel;

    private Texture fullHeartTexture;
    private Texture halfHeartTexture;

    private Table heartTable;

    private int maxHearts;
    private boolean[] heartStates; // Array to track heart states (true = full, false = half)

    private int currentHeartIndex; // Track the last heart's index to modify it

    /**
     * Constructor for HUD.
     * Initializes score, timer, and heart system.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    public HUD(SpriteBatch batch, ScreenManager game, int maxHearts) {
        worldTimer = 0;
        timeCount = 0;
        score = 0;

        this.maxHearts = maxHearts;

        viewport = new ScreenViewport();
        stage = new Stage(viewport, batch);

        // Load textures for hearts
        fullHeartTexture = new Texture("heart pixel art 32x32.png");

        // Set the maximum number of hearts (5 hearts for max health = 10)

        // Initialize heart states and set the current heart index to max (start with all full hearts)
        heartStates = new boolean[maxHearts]; // All hearts start as full
        for (int i = 0; i < maxHearts; i++) {
            heartStates[i] = true; // true means full heart initially
        }
        currentHeartIndex = maxHearts - 1; // Start with the last heart

        // Create table for HUD elements (timer, score, etc.)
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // Labels (same as before)
        BitmapFont originalFont = game.getSkin().getFont("font"); // Assuming "font" is the name of the font in your skin
        originalFont.getData().setScale(0.7f); // Scale down the font size by 50%

        // Labels setup code...

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.getSkin().getFont("font");  // Use the "font" from the skin
        
        countdownLabel = new Label(String.format("%03d", worldTimer), labelStyle);

        scoreLabel1 = new Label(String.format("%06d", score), labelStyle);

        timeLabel = new Label("TIME", labelStyle);

        levelLabel = new Label("LEVEL 1", labelStyle);

        worldLabel = new Label("WORLD 1", labelStyle);

        scoreLabel = new Label("SCORE", labelStyle);

        // Add labels to the table
        table.add(scoreLabel).expandX().padTop(10).padLeft(30);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel1).expandX().padLeft(30);
        table.add(countdownLabel).expandX();
        table.add(levelLabel).expandX();

        // Add the table to the stage
        stage.addActor(table);

        // Create table for hearts
        heartTable = new Table();
        heartTable.top().left(); // Align to top-left corner
        heartTable.setFillParent(true);

        // Add full hearts initially
        for (int i = 0; i < maxHearts; i++) {
            Image heart = new Image(fullHeartTexture); // Full hearts initially
            heart.setName("heart_" + i); // Assign unique names
            heartTable.add(heart).size(32, 32).pad(5);
            if ((i+1)%5==0){
               heartTable.row();
            }
        }

        // Add heart table to the stage
        stage.addActor(heartTable);

        // Explicitly set the input processor
        com.badlogic.gdx.Gdx.input.setInputProcessor(stage); // Make sure stage receives input

        // Set up input processor for the Z key press
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keyCode) {
                if (keyCode == Input.Keys.Z) {  // If the Z key is pressed
                    System.out.println("Z key pressed"); // Debugging line// Call the method to hit the last heart
                    return true;
                }
                return false;
            }
        });
    }

    // Method to progressively change the last heart from full to half, then remove it
    public void updateHearts(int currentHealth) {

        // Clear the heart table
        heartTable.clear();

        System.out.println(currentHealth+"hearts");

        // Recreate hearts based on current health
        for (int i = 0; i < maxHearts; i++) {
            if (i < currentHealth) {
                // Full heart for each health point
                heartTable.add(new Image(fullHeartTexture)).size(32, 32).pad(5);
            }
            if ((i + 1) % 5 == 0) {
                heartTable.row(); // Wrap to the next row
            }
        }
    }


    /**
     * Updates the world timer, score, or other HUD elements.
     *
     * @param delta Time since the last frame.
     */
    public void update(float delta) {
        timeCount += delta;
        if (timeCount >= 1) {
            worldTimer++;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    /**
     * Disposes of resources used by the HUD.
     */
    public void dispose() {
        fullHeartTexture.dispose();
        halfHeartTexture.dispose();  // Dispose half heart texture
        stage.dispose();
    }
}
