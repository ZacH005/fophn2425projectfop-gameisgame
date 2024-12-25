package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.entity.Player;

import java.util.ArrayList;

/**
 * The GameScreen class handles gameplay, rendering the Tiled map and the player.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    //private final BitmapFont font;

    //Tiled map which is an object
    private TiledMap tiledMap;
    //tiled comes with a renderer
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    private float tileSize;

    private Texture lightTexture;


    /**
     * Constructor for GameScreen. Sets up camera, map, and player.
     */
    public GameScreen(MazeRunnerGame game) {
        //game is game
        this.game = game;
        this.tileSize = 16.0f;

        //CAMERA THINGS:
        camera = new OrthographicCamera();
        //this was kinda from before, i don't understand all this
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        camera.zoom = 1f;

        //this is from the template should be good later
//        font = game.getSkin().getFont("font");

        //MAP STUFF::
        //decided to load map in the game screen since it's super simple in libgdx with tiled
        tiledMap = new TmxMapLoader().load("TiledMaps/CaveMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        //THIS IS ALL PLAYER THINGS:
        //need to make it so when the map loads, it chooses the specific location of the starting block
        float startPlayerX = (2*tileSize)+tileSize/2;
        float startPlayerY = (2*tileSize);
        //just initializing the player
        player = new Player(startPlayerX, startPlayerY, 150, tiledMap,100,100,new ArrayList<String>(),0);
        player.setCurrentAnimation(game.getCharacterIdleAnimation());

        // LIGHTING TEXTURE
        lightTexture = createConeLightTexture(256, 256);
    }

    @Override
    public void render(float delta) {
        //options/pause button
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ///saving the player state to a txt file before pausing
            player.saveState("playerstate.txt");
            game.goToPause();
        }

        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        //changed this to a method because it was way too much to just be sitting in render
        handleInput();

        //updating the player, doesn't have any bounds anymore, but theres also no collisions
        player.update(delta);
        //literally is just moving the camera with the player, can be changed easily
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        //basically its just saying the map should be shown in (camera)
        mapRenderer.setView(camera);
        //literally just renders the map. that's it... but it is now rendering layers specfiically ina. diff order
        mapRenderer.render();

        //I don't get projectionmatrices, needed to be attached to the spritebatch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        //renders the player, pretty chill
        player.render(game.getSpriteBatch());

        // LIGHTING: Darken the screen and draw the light texture
        renderLighting();

        game.getSpriteBatch().end();

        //this is so that some walls render after the player (over), but now that collisions are working this isn't as necessary, could be useful for smth else
//        mapRenderer.render(new int[]{1, 2});
    }

    /**
     * Renders the lighting effect by drawing the light texture over a dark screen.
     */
    private void renderLighting() {
        // Enable additive blending for light
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        // Determine light position and rotation based on direction
        float x = player.getPosition().x;
        float y = player.getPosition().y;

        float lightWidth = 70;
        float lightHeight = 256;

        float lightx = x;
        float lighty = y;

        float rotation = getRotationForDirection(player.getDirection());
        //down == y - lightHeight
        //up == y
        //right == x + lightWidth + 1.7f*tileSize, y - lightHeight / 2 + 2
        //left == x - 2*lightWidth - tileSize - 8, y - lightHeight / 2 + 2

        switch (player.getDirection())  {
            case DOWN -> {
                lighty = y - lightHeight;
                lightx = x - lightWidth / 2;
            }
            case RIGHT -> {
                lightx = x + lightWidth + 1.7f*tileSize;
                lighty = y - lightHeight / 2 + 2;
            }
            case LEFT -> {
                lightx = x - 2*lightWidth - tileSize - 8;
                lighty = y - lightHeight / 2 + 2;
            }
            default -> {
                lightx = x - lightWidth / 2;
                lighty = y;
            }
        }

        game.getSpriteBatch().draw(lightTexture,
                lightx, lighty,   // Position
                lightWidth / 2, lightHeight / 2,           // Origin of rotation
                lightWidth, lightHeight,                   // Size
                1, 1,                                      // Scaling
                rotation,                                  // Rotation
                0, 0, lightTexture.getWidth(), lightTexture.getHeight(), false, false);

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private float getRotationForDirection(Player.Direction direction) {
        switch (direction) {
            case UP:
                return 180f;   // Default orientation (cone pointing up)
            case DOWN:
                return 0f; // Rotate 180 degrees
            case LEFT:
                return -90f;  // Rotate 90 degrees counter-clockwise
            case RIGHT:
                return 90f; // Rotate 90 degrees clockwise
            default:
                return 0f;
        }
    }



    /**
     * Creates a circular light texture with a radial gradient.
     */
    private Texture createConeLightTexture(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);

        int tipX = width / 2;  // Tip of the cone (centered horizontally)
        int tipY = 0;          // Tip of the cone (top of the texture)

        for (int y = 0; y < height; y++) {
            float alpha = 1 - (y / (float) height); // Fade transparency as it moves down
            int coneWidth = (int) ((y / (float) height) * width); // Width of the cone at each y

            for (int x = tipX - coneWidth / 2; x < tipX + coneWidth / 2; x++) {
                if (x >= 0 && x < width) { // Ensure within bounds
                    pixmap.setColor(1, 0.8f, 0.5f, alpha);
                    pixmap.drawPixel(x, y);
                }
            }
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
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
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 2f, height / 2f);
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        tiledMap.dispose();
        lightTexture.dispose();
    }

    @Override
    public void show() {
        ///loading the player state from a txt file after resuming
        player.loadState("playerstate.txt");
    }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
