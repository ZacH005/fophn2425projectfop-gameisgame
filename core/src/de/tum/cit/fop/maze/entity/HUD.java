package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;

/**
 * HUD class is responsible for displaying and updating the on-screen interface of the game,
 * including the player's health, score, time, and other in-game elements.
 */
public class HUD {
    public Stage stage;
    private Viewport viewport;

    private Integer keysno;
    private int gemsno;
    private float elapsedTime;
    private Integer score;

    Label keysLabel;
    Label scoreLabel1;
    Label timeLabel;
    Label timecounterLabel;
    Label worldLabel;
    Label scoreLabel;
    Label gemLabel;
    Label gemCount;
    Label tutorialLabel;

    private Texture fullHeartTexture;
    private Texture halfHeartTexture;

    private Table heartTable;
    private int maxGems;
    private boolean isTutorial;
    float timer = 0;
    float timer2 = 0;

    private int maxHearts;
    private boolean[] heartStates;

    private int currentHeartIndex;
    private Player player;
    private Array<TextureRegion> frames;
    Label.LabelStyle labelStyle;

    /**
     * Constructor for HUD.
     * Initializes score, timer, and heart system.
     *
     * @param batch The SpriteBatch used for rendering.
     * @param game The ScreenManager instance for managing the game state.
     * @param player The player object containing player data.
     * @param isTutorial Boolean flag to indicate if it's a tutorial.
     */
    public HUD(SpriteBatch batch, ScreenManager game, Player player, boolean isTutorial) {
        keysno = player.getKeys();
        gemsno = player.getGems();
        elapsedTime = 0;
        score = 0;
        maxGems = 0;
        this.isTutorial = isTutorial;

        TextureRegion region = new TextureRegion(new Texture("HUD heart.png"));

        frames = new Array<TextureRegion>();
        int frameWidth = region.getRegionWidth() / 5;
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(region, i * frameWidth, 0, frameWidth, region.getRegionHeight()));
        }

        this.player = player;

        this.maxHearts = player.getMaxHealth();

        viewport = new ScreenViewport();
        stage = new Stage(viewport, batch);

        fullHeartTexture = new Texture("heart pixel art 32x32.png");

        heartStates = new boolean[maxHearts];
        for (int i = 0; i < maxHearts; i++) {
            heartStates[i] = true;
        }
        currentHeartIndex = maxHearts - 1;

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        BitmapFont originalFont = game.getSkin().getFont("font");
        originalFont.getData().setScale(0.7f);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = game.getSkin().getFont("font");
        tutorialLabel = new Label("Pick up the golden axe.", labelStyle);

        keysLabel = new Label(String.format("%03d", keysno), labelStyle);
        gemLabel = new Label("GEMS", labelStyle);
        gemCount = new Label(gemsno + "/" + maxGems, labelStyle);

        timeLabel = new Label("TIME", labelStyle);

        timecounterLabel = new Label("00:00", labelStyle);

        worldLabel = new Label("AXES", labelStyle);

        heartTable = new Table();

        table.add(heartTable).expandX();

        Table table1 = new Table();
        Table table2 = new Table();
        Table table3 = new Table();
        table1.add(worldLabel).expandX();
        table1.row();
        table1.add(keysLabel).expandX();
        table.add(table1).expandX();
        table3.add(gemLabel).expandX();
        table3.row();
        table3.add(gemCount).expandX();
        table.add(table3).expandX();
        table2.add(timeLabel).expandX();
        table2.row();
        table2.add(timecounterLabel).expandX();
        table.add(table2).expandX();

        table.padTop(10);
        Table tutorialTable = new Table();
        tutorialTable.bottom();
        tutorialTable.setFillParent(true);
        tutorialTable.add(tutorialLabel).expandX().padBottom(10);
        stage.addActor(table);
        if (isTutorial) {
            stage.addActor(tutorialTable);
        }

        com.badlogic.gdx.Gdx.input.setInputProcessor(stage);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keyCode) {
                if (keyCode == Input.Keys.Z) {
                    System.out.println("Z key pressed");
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Updates the HUD every frame, including the time and the player's data.
     *
     * @param delta The delta time since the last frame.
     */
    public void updateHUD(float delta) {
        keysLabel.setText(String.format("%03d", player.getKeys()));
        gemCount.setText(player.getGems() + "/" + maxGems);
        if (isTutorial) {
            System.out.println(player.getHitexit());
            if (player.getKeys() == 1 && player.getBrokenwalls() == 0) {
                tutorialLabel.setText("You need golden axes to break walls. Approach the wall and press Space to break.");
            } else if (player.getKeys() == 0 && player.getBrokenwalls() == 1 && player.getGems() == 0) {
                timer += delta;
                if (timer > 3f) {
                    tutorialLabel.setText("Press Space to break the gem. No golden axe is required.");
                } else {
                    tutorialLabel.setText("Watch Out ! The water is toxic.");
                }
            } else if (player.getGems() == 1 && player.getHitexit() == 0) {
                tutorialLabel.setText("Follow the arrow indicator to find the exit.");
            } else if (player.getHitexit() == 1) {
                timer2 += delta;
                if (timer2 <= 6f) {
                    tutorialLabel.setText("You have 1/2 gems. Collect all gems to unlock the exit");
                } else if (timer2 > 6f && timer2 < 10f) {
                    tutorialLabel.setText("You might run into trouble on your way! Use your axe to defend yourself. Have fun exploring!");
                } else {
                    tutorialLabel.setVisible(false);
                }
            }
        }

        elapsedTime += Gdx.graphics.getDeltaTime();

        int minutes = (int) (elapsedTime / 60);
        int seconds = (int) (elapsedTime % 60);

        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        timecounterLabel.setText(formattedTime);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }

    /**
     * Updates the player's hearts based on their current health.
     *
     * @param currentHealth The player's current health.
     */
    public void updateHearts(float currentHealth) {
        heartTable.clear();

        float remainder = currentHealth % 1;
        int x = (int) (remainder / 0.25);
        Image heart;
        if (x == 0) {
            heart = new Image(frames.get(0));
        } else {
            int index = Math.max(0, Math.min(4, 4 - x));
            heart = new Image(frames.get(index));
        }
        heart.setName("heart_");
        heartTable.add(heart).size(54, 42);
        scoreLabel1 = new Label(String.format("x%01d", (int) Math.ceil(player.getHealth())), labelStyle);
        heartTable.add(scoreLabel1);
    }

    /**
     * Sets the maximum number of gems in the game.
     *
     * @param maxGems The maximum number of gems.
     */
    public void setMaxGems(int maxGems) {
        this.maxGems = maxGems;
    }

    /**
     * Disposes of resources used by the HUD.
     */
    public void dispose() {
        fullHeartTexture.dispose();
        halfHeartTexture.dispose();
        stage.dispose();
    }

    public int getMaxHearts() {
        return maxHearts;
    }

    public void setMaxHearts(int maxHearts) {
        this.maxHearts = maxHearts;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
