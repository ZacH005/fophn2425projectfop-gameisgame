package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.SoundManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private final Stage stage2;
    private SoundManager soundManager;
    Map<String,Integer> menuState = new HashMap<String,Integer>();
    private SpriteBatch batch;
    private Texture overlayTexture;
    private Texture bg;
    private Table table;
    private Table table2;
    private Viewport viewport;
    private ScreenManager game;
    ///PARTICLEEEEEEEEEEEEEES
    private ParticleEffect particleEffect;
    /// responsive screen
    float scale ;
    float bgWidth;
    float bgHeight;
    float bgX ;
    float bgY ;
    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(ScreenManager game) {
        this.game = game;
        var camera = new OrthographicCamera();
        camera.zoom = 1f; // Set camera zoom for a closer view

         viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        stage2 = new Stage(viewport, game.getSpriteBatch());


        table = new Table(); // Create a table for layout
        table2 = new Table();
        table2.setFillParent(true);
        table.setFillParent(true); // Make the table fill the stage


        stage.addActor(table); // Add the table to the stage
        stage2.addActor(table2);

        //sound manager
        soundManager = game.getSoundManager();
        soundManager.loadSound("click","music/UI/menu_select.ogg");

        menuState.put("crackles",1);
        menuState.put("wind",1);
        menuState.put("piano",1);
        menuState.put("strings",0);
        menuState.put("pad",0);
        menuState.put("drums",0);
        menuState.put("bass",0);


        // Add a label as a title
        table2.add(new Label("Blind Cave Game!", game.getSkin(), "title")).padBottom((float) viewport.getScreenHeight()/2);

        if(game.getUser().getCompletedLevels().isEmpty()){
            TextButton startNewGameButton = new TextButton("Start New Game", game.getSkin());
            table.add(startNewGameButton).width(300).row();
            startNewGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundManager.playSound("click");
                    game.goToGame();
                }
            });
        }
        else{
            TextButton ContinueButton = new TextButton("Continue", game.getSkin());
            table.add(ContinueButton).width(300).row();
            ContinueButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundManager.playSound("click");
                    game.goToGame();
                }
            });
        }

/// map selector button
        TextButton goToLevelSelectorButton = new TextButton("Levels", game.getSkin());
        table.add(goToLevelSelectorButton).width(300).row();
        goToLevelSelectorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                game.goToLevelSelector();
            }
        });
        soundManager.onGameStateChange(menuState);

//        // Create and add a button to go to the game screen
//        TextButton goToGameButton = new TextButton("Continue Game", game.getSkin());
//        table.add(goToGameButton).width(300).row();
//        goToGameButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                game.goToGame();
//            }
//        });

        TextButton goToSettingsButton = new TextButton("Go To Settings", game.getSkin());
        table.add(goToSettingsButton).width(300).row();
        goToSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                game.goToSettings(); // Change to the game screen when button is pressed
            }
        });

        TextButton exitGameButton = new TextButton("Exit Game", game.getSkin());
        table.add(exitGameButton).width(300).row();
        exitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playSound("click");
                Gdx.app.exit(); // exit game
                //System.exit(-1); this is a force shut, which we don't need because we need to
                // do cleanups and to save userdata.
            }
        });
        bg = new Texture(Gdx.files.internal("Menu UI Design.png"),true);
        overlayTexture = new Texture(Gdx.files.internal("DK-MenuVersion2.png"),true);
    }


    @Override
    public void render(float delta) {
        soundManager.setKeySoundVolume(0);

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float scaleX = Gdx.graphics.getWidth() / (float) bg.getWidth();
        float scaleY = Gdx.graphics.getHeight() / (float) bg.getHeight();
         scale = Math.min(scaleX, scaleY);
         bgWidth = bg.getWidth() * scale;
         bgHeight = bg.getHeight() * scale;
         bgX = (Gdx.graphics.getWidth() - bgWidth) / 2f;
         bgY = (Gdx.graphics.getHeight() - bgHeight) / 2f;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.getBatch().begin();

        stage.getBatch().draw(bg, bgX, bgY, bgWidth, bgHeight);
        ///PARTICLEEEEEEEEEEEEEES
        particleEffect.start();
        particleEffect.draw(stage.getBatch(),delta);
        particleEffect.setPosition(Gdx.graphics.getWidth()/2f ,Gdx.graphics.getHeight()/2f-100);
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
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        batch.dispose();
        overlayTexture.dispose();
        bg.dispose();
        stage.dispose();
    }

    @Override
    public void show() {
        soundManager.setKeySoundVolume(0f);
        ///PARTICLEEEEEEEEEEEEEES
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/effects/Particle Park Flame.p"), Gdx.files.internal("particles/images"));
        particleEffect.setPosition(Gdx.graphics.getWidth()/2f ,Gdx.graphics.getHeight()/2f-100);
        particleEffect.scaleEffect(5f);


        // Set the input processor so the stage can receive input events
//        batch = new SpriteBatch();
//        backgroundBatch = new SpriteBatch();
//        batch.setProjectionMatrix(stage.getCamera().combined);
//        backgroundBatch.setProjectionMatrix(stage.getCamera().combined);


//        batch.setColor(1f,1f,1f,0.8f);

        // Hide the default system cursor
//        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);

        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
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
