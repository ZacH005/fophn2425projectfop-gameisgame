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
import de.tum.cit.fop.maze.MazeRunnerGame;
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

    private final MazeRunnerGame game;
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
    private Texture backgroundoverlay;// Store all lights
    private HUD hud;
    private boolean isGameOver;

    private Texture lightTexture;
    /// updated the constructor to take a map path
    public GameScreen(MazeRunnerGame game, String mapPath ) {
        shapeRenderer = new ShapeRenderer();
        //game is game
        this.game = game;
        this.tileSize = 16.0f;
        //CAMERA THINGS:
        camera = new OrthographicCamera();
        //this was kinda from before, i don't understand all this
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 3f);
        camera.zoom = 1f;
        backgroundoverlay=new Texture("DK.png");

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

    @Override
    public void render(float delta) {
        if (player.getHealth()==0){
            isGameOver=true;
            game.setScreen(new GameOverScreen(game));
        }
        //options/pause button
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ///saving the player state to a txt file before pausing
            player.saveState("playerstate.txt");
            enemy.saveState("enemystate.txt");
            game.goToPause();
        }

        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        //changed this to a method because it was way too much to just be sitting in render
        handleInput();

        //updating the player, doesn't have any bounds anymore, but theres also no collisions
        player.update(delta);
        enemy.update(delta);

        //literally is just moving the camera with the player, can be changed easily
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        //basically its just saying the map should be shown in (camera)
        mapRenderer.setView(camera);

        // Render the map and player to the framebuffer (off-screen)
        ///actually does smth, uncomment end for it to work
//        lightBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  // Clear the framebuffer

        //literally just renders the map. that's it... but it is now rendering layers specfiically ina. diff order
        mapRenderer.render();

//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(1, 0, 0, 1); // Red color
//        shapeRenderer.rect(enemy.scanRange.getX(),enemy.scanRange.getY(), enemy.scanRange.width, enemy.scanRange.height);
//        shapeRenderer.end();

        //I don't get projectionmatrices, needed to be attached to the spritebatch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        // Apply lighting
//        lightBuffer.end();

        // Use the lighting shader
        ///this breaks everything for some reason vvv
//        lightingShader.bind();
//        lightingShader.setUniformf("u_lightPos", lights.get(0).position.x, lights.get(0).position.y);  // Set the light position
//        lightingShader.setUniformf("u_lightRadius", lights.get(0).radius);
        lightingShader.setUniformf("u_lightPos", player.getPosition().x, player.getPosition().y);  // Set the light position
        lightingShader.setUniformf("u_lightRadius", 300f);

        // Draw the framebuffer with lighting effects
//        game.getSpriteBatch().draw(lightBuffer.getColorBufferTexture(), 0, 0);

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

        //renders the player, pretty chill
        player.render(game.getSpriteBatch());
        game.getSpriteBatch().draw(enemy.getEnemy(),enemy.position.x,enemy.position.y);
        game.getSpriteBatch().setColor(1,1,1,0.9f);
        game.getSpriteBatch().draw(backgroundoverlay,player.getPosition().x-backgroundoverlay.getWidth()/4,player.getPosition().y-backgroundoverlay.getHeight()/4,960,540);
        game.getSpriteBatch().setColor(1,1,1,1);

        game.getSpriteBatch().end();

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


    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 3f, height / 3f);
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        tiledMap.dispose();
        lightBuffer.dispose();
        lightingShader.dispose();
    }

    @Override
    public void show() {
        ///loading the player state from a txt file after resuming
        player.loadState("playerstate.txt");
        enemy.loadState("enemystate.txt");
    }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }


}
