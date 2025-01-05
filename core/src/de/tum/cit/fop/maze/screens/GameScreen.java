package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.abilities.Collectable;
import de.tum.cit.fop.maze.abilities.SpeedUp;
import de.tum.cit.fop.maze.entity.Enemy;
import de.tum.cit.fop.maze.entity.HUD;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.shaders.Light;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The GameScreen class handles gameplay, rendering the Tiled map and the player.
 */
public class GameScreen implements Screen {
    //Map Path
    private final ScreenManager game;
    private final OrthographicCamera camera;
    //private final BitmapFont font;

    //Tiled map which is an object
    private TiledMap tiledMap;
    //tiled comes with a renderer
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    private Enemy enemy;
    private List<Powerup> mapPowerups;

    private float tileSize;
    private boolean following=false;

    private ShapeRenderer shapeRenderer;

    private FrameBuffer lightBuffer;
    private ShaderProgram lightingShader;
    private ArrayList<Light> lights;
    private Texture darkCircleoverlay;// Store all lights
    private HUD hud;
    private boolean isGameOver;

    private Texture lightTexture;
    /// updated the constructor to take a map path
    private String mapPath;
    /// pause trigger
    private boolean isPaused;
    private PauseOverlay pauseOverlay;

    public GameScreen(ScreenManager game, String mapPath ) {
        this.mapPath = mapPath;
        shapeRenderer = new ShapeRenderer();

        //game is game
        this.game = game;
        this.tileSize = 16.0f;

        //CAMERA THINGS:
        camera = new OrthographicCamera();

        //this was kinda from before, i don't understand all this
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        camera.zoom = 1f;
        darkCircleoverlay =new Texture("DK.png");


        //this is from the template should be good later
//        font = game.getSkin().getFont("font");

        //MAP STUFF::
        //decided to load map in the game screen since it's super simple in libgdx with tiled
        // I modified this to load a map from a selector

        tiledMap = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        isGameOver=false;

        //THIS IS ALL PLAYER THINGS:
        //need to make it so when the map loads, it chooses the specific location of the starting block
        float startPlayerX = (2*tileSize)+tileSize/2;
        float startPlayerY = (2*tileSize);
        //just initializing the player
        player = new Player(startPlayerX, startPlayerY, tiledMap,5,100,new ArrayList<String>(),0);
        player.setCurrentAnimation(game.getCharacterIdleAnimation());
        hud=new HUD(game.getSpriteBatch(),game);

        this.enemy=new Enemy(200,250,player,hud);
        mapPowerups = new ArrayList<>();
        mapPowerups.add(new SpeedUp(player, "SpeedUp", "Fast Power", 4*tileSize, 3*tileSize));

        // LIGHTING
        lights = new ArrayList<>();
        lights.add(new Light(new Vector2(player.getPosition().x, player.getPosition().y), 300f));

        // Initialize frame buffer and shader
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 800, 600, false);
        lightingShader = new ShaderProgram(Gdx.files.internal("shaders/lighting.vert"), Gdx.files.internal("shaders/lighting.frag"));
        if (!lightingShader.isCompiled()) {
            Gdx.app.error("Shader", "Shader compilation failed: " + lightingShader.getLog());
        }
    }
//    public void completeLevel(){
//        //updates the index of the map being played
//        game.setIndexOfTheMapBeingPlayed(game.getIndexOfTheMapBeingPlayed()+1);
//
//
//    }
    @Override
    public void render(float delta) {

        if(isPaused) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPaused=false;
            }
            pauseOverlay.setVisible(isPaused);
            pauseOverlay.render(delta);
            return;
        }

        else{

                // winning
                if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                    // when a level is finished
                    // we first add it to the completed levels in the user data
                    // the map path is of form TiledMaps/mapName
                    // so we need to strip it just to the mapName
                    String mapName = mapPath.substring(mapPath.lastIndexOf('/') + 1);
                    if(!game.getUser().getCompletedLevels().contains(mapName)){
                        game.getUser().getCompletedLevels().add(mapName);
                        game.getUser().saveUserData("user_data.ser");
                        System.out.println(mapName+" is added to completed levels");
                    }
                    //now when the goToGame is called, we make sure it does not go to a finished map
                    // we already did that, so we can call goToGame in the ScreenManager
                    // and it won't load an level that's added to the completed levels
                    // we debug by prinitng which level we complete by pressing X
                    // and which levels are we loading
                    game.setScreen(new VictoryScreen(game));
                }
                //health trigger
                if (player.getHealth()==0){
                    isGameOver=true;
                    game.setScreen(new GameOverScreen(game));
                }
                //input updating ; find the method below for details
                handleInput();
                // updating characters
                player.update(delta);
                enemy.update(delta);
        }

        // Clear screen before prinitng each frame
        ScreenUtils.clear(0, 0, 0, 1);
        // Clear the framebuffer
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // moving the camera with the player
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
        //basically its just saying the map should be shown in (camera)
        mapRenderer.setView(camera);
        //literally just renders the map. that's it... but it is now rendering layers specfiically ina. diff order
        mapRenderer.render();

        // lights :
        // Set the light position
        lightingShader.setUniformf("u_lightPos", player.getPosition().x, player.getPosition().y);
        lightingShader.setUniformf("u_lightRadius", 300f);

        // Render the map and player to the framebuffer (off-screen)
        ///actually does smth, uncomment end for it to work
//        lightBuffer.begin();



//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(1, 0, 0, 1); // Red color
//        shapeRenderer.rect(enemy.scanRange.getX(),enemy.scanRange.getY(), enemy.scanRange.width, enemy.scanRange.height);
//        shapeRenderer.end();



        // Apply lighting
//        lightBuffer.end();

        // Use the lighting shader
        ///this breaks everything for some reason vvv
//        lightingShader.bind();
//        lightingShader.setUniformf("u_lightPos", lights.get(0).position.x, lights.get(0).position.y);  // Set the light position
//        lightingShader.setUniformf("u_lightRadius", lights.get(0).radius);


        // Draw the framebuffer with lighting effects
//        game.getSpriteBatch().draw(lightBuffer.getColorBufferTexture(), 0, 0);


        //I don't get projectionmatrices, needed to be attached to the spritebatch
// Set the projection matrix for the game camera
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().enableBlending();
        game.getSpriteBatch().begin();

        //loading powerups on map
        if (!mapPowerups.isEmpty()) {
            Iterator<Powerup> iterator = mapPowerups.iterator();
            while (iterator.hasNext()) {
                Powerup powerup = iterator.next();
                Vector2 position = powerup.getPosition();
                game.getSpriteBatch().draw(powerup.getTexture(), position.x, position.y);

                if (powerup instanceof Collectable<?> collectable) {
                    if (collectable.checkPickUp())
                        iterator.remove();
                }
            }
        }


// Render the player and enemy
        player.render(game.getSpriteBatch());
        game.getSpriteBatch().draw(enemy.getEnemy(), enemy.position.x, enemy.position.y);

// Apply the dark circle overlay
        game.getSpriteBatch().setColor(1, 1, 1, 0.9f);
        game.getSpriteBatch().draw(darkCircleoverlay, player.getPosition().x - darkCircleoverlay.getWidth() / 4, player.getPosition().y - darkCircleoverlay.getHeight() / 4, 960, 540);
        game.getSpriteBatch().setColor(1, 1, 1, 1);

        game.getSpriteBatch().end();

// Set the projection matrix for the HUD
        game.getSpriteBatch().setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();


//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(1, 0, 0, 1); // Red color
//        shapeRenderer.rect(enemy.damageCollider.getX(),enemy.damageCollider.getY(), enemy.damageCollider.width, enemy.damageCollider.height);
//        shapeRenderer.end();

        //this is so that some walls render after the player (over), but now that collisions are working this isn't as necessary, could be useful for smth else
//        mapRenderer.render(new int[]{1, 2});
    }


    private void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            isPaused = true;


        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.move(Player.Direction.LEFT);
            player.setCurrentAnimation(game.getCharacterLeftAnimation());
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            player.move(Player.Direction.RIGHT);
            player.setCurrentAnimation(game.getCharacterRightAnimation());
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.move(Player.Direction.UP);
            player.setCurrentAnimation(game.getCharacterUpAnimation());
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.move(Player.Direction.DOWN);
            player.setCurrentAnimation(game.getCharacterDownAnimation());
        } else {
            player.stop();
            player.setCurrentAnimation(game.getCharacterIdleAnimation());
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (!player.isSprinting())  {
                player.setSpeed(player.getSpeed()*1.50f);
                player.setSprinting(true);
                System.out.println(player.getSpeed());
            }
        }else{
            if (player.isSprinting())   {
                player.setSpeed(player.getSpeed()/1.50f);
                player.setSprinting(false);
                System.out.println(player.getSpeed());
            }
        }
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 2f, height / 2f);
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        tiledMap.dispose();
        lightBuffer.dispose();
        lightingShader.dispose();
        pauseOverlay.dispose();
    }

    @Override
    public void show() {
        ///loading the player state from a txt file after resuming
        player.loadState("playerstate.txt");
        enemy.loadState("enemystate.txt");
        pauseOverlay = new PauseOverlay(this,game);
    }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
