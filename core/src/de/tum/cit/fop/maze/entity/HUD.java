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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;

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

    private Texture fullHeartTexture;
    private Texture halfHeartTexture;

    private Table heartTable;
    private int maxGems;

    private int maxHearts;
    private boolean[] heartStates; // Array to track heart states (true = full, false = half)

    private int currentHeartIndex;// Track the last heart's index to modify it
    private Player player;
    private Array<TextureRegion> frames;
    Label.LabelStyle labelStyle;
    /**
     * Constructor for HUD.
     * Initializes score, timer, and heart system.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    public HUD(SpriteBatch batch, ScreenManager game, Player player) {
        keysno = player.getKeys();
        gemsno=player.getGems();
        elapsedTime = 0;
        score = 0;
        maxGems = 0;

        TextureRegion region= new TextureRegion(new Texture("HUD heart.png"));

        frames = new Array<TextureRegion>();
        int frameWidth = region.getRegionWidth() / 5;
        for(int i = 0; i < 5; i++){
            frames.add(new TextureRegion(region, i * frameWidth, 0, frameWidth, region.getRegionHeight()));
        }

        this.player = player;

        this.maxHearts = player.getMaxHealth();

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

        labelStyle = new Label.LabelStyle();
        labelStyle.font = game.getSkin().getFont("font");  // Use the "font" from the skin
        
        keysLabel = new Label(String.format("%03d", keysno), labelStyle);
        gemLabel = new Label("GEMS", labelStyle);
        gemCount =new Label(gemsno+"/"+maxGems, labelStyle);


        timeLabel = new Label("TIME", labelStyle);

        timecounterLabel = new Label("00:00", labelStyle);

        worldLabel = new Label("AXES", labelStyle);

        heartTable=new Table();

        table.add(heartTable).expandX();
        
        Table table1=new Table();
        Table table2 =new Table();
        Table table3 =new Table();
        // Add labels to the table
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
        // Add the table to the stage
        stage.addActor(table);

        // Create table for hearts


        // Add heart table to the stage

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
    public void updateHUD(){
        keysLabel.setText(String.format("%03d", player.getKeys()));
        gemCount.setText(player.getGems()+"/"+maxGems);

        // Update elapsed time every frame
        elapsedTime += Gdx.graphics.getDeltaTime(); // deltaTime gives you the time since the last frame

        // Calculate minutes and seconds
        int minutes = (int) (elapsedTime / 60); // Get minutes
        int seconds = (int) (elapsedTime % 60); // Get seconds

        // Format the time as "MM:SS"
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        // Update the label text
        timecounterLabel.setText(formattedTime);

        // Draw everything
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }
    // Method to progressively change the last heart from full to half, then remove it
    public void updateHearts(float currentHealth) {

        // Clear the heart table
        heartTable.clear();

        // Recreate hearts based on current health
        float remainder = currentHealth%1;
        int x = (int)(remainder/0.25);
        Image heart;
        if(x==0){
            heart = new Image(frames.get(0));
        }
        else{
            heart = new Image(frames.get(4-x));
        }
        heart.setName("heart_"); // Assign unique names
        heartTable.add(heart).size(54, 42);
        scoreLabel1 = new Label(String.format("x%01d",(int) Math.ceil(player.getHealth())), labelStyle);
        heartTable.add(scoreLabel1);
    }




    public void setMaxGems(int maxGems) {
        this.maxGems = maxGems;
    }

    /**
     * Disposes of resources used by the HUD.
     */
    public void dispose() {
        fullHeartTexture.dispose();
        halfHeartTexture.dispose();  // Dispose half heart texture
        stage.dispose();
    }

    public int getMaxHearts() {
        return maxHearts;
    }

    public void setMaxHearts(int maxHearts) {
        this.maxHearts = maxHearts;
    }
}
