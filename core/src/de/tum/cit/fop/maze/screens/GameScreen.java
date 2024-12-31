package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.entity.Enemy;
import de.tum.cit.fop.maze.entity.Player;

import java.util.ArrayList;

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
    private float tileSize;
    private boolean following=false;

    private ShapeRenderer shapeRenderer;

    private Texture lightTexture;
    /// updated the constructor to take a map path
    public GameScreen(MazeRunnerGame game, String mapPath ) {
        shapeRenderer = new ShapeRenderer();
        //game is game
        this.game = game;
        this.tileSize = 16.0f;
        this.enemy=new Enemy(200,250);
        //CAMERA THINGS:
        camera = new OrthographicCamera();
        //this was kinda from before, i don't understand all this
        camera.setToOrtho(false, 480, 270);

        //this is from the template should be good later
//        font = game.getSkin().getFont("font");

        //MAP STUFF::
        //decided to load map in the game screen since it's super simple in libgdx with tiled
        // I modified this to load a map from a selector
        tiledMap = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        //THIS IS ALL PLAYER THINGS:
        //need to make it so when the map loads, it chooses the specific location of the starting block
        float startPlayerX = (2*tileSize)+tileSize/2;
        float startPlayerY = (2*tileSize);
        //just initializing the player
        player = new Player(startPlayerX, startPlayerY, 150, tiledMap,100,100,new ArrayList<String>(),0);
        player.setCurrentAnimation(game.getCharacterIdleAnimation());

        // LIGHTING TEXTURE
        //lightTexture = createConeLightTexture(256, 256);
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
        enemy.update(delta);
        checkfollows();

        //literally is just moving the camera with the player, can be changed easily
        camera.position.x = player.getPosition().x;
        camera.position.y = player.getPosition().y;
        camera.update();

        //basically its just saying the map should be shown in (camera)
        mapRenderer.setView(camera);
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

        //renders the player, pretty chill
        player.render(game.getSpriteBatch());
        game.getSpriteBatch().draw(enemy.getEnemy(),enemy.position.x,enemy.position.y);

        // LIGHTING: Darken the screen and draw the light texture
        //renderLighting();

        game.getSpriteBatch().end();



        if (delta % 1000 == 0)
            player.saveState("playerstate.txt");

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
    }


    @Override
    public void resize(int width, int height) {
//        camera.setToOrtho(false, width / 2f, height / 2f);
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

    public void checkfollows() {
        // calculates x and y distance
        float distanceX = player.getPosition().x - enemy.position.x;
        float distanceY = player.getPosition().y - enemy.position.y;

        // calculate the total distance (Pythagoras)
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        // check if the player is in range
        if (enemy.scanRange.overlaps(player.collider)) {
            following = true; // Start following
        }

        if (following) {
            // if close enough or escaped, stop moving
            if (distance < 5.0f||distance > 100.0f) { // Stop when within 5 units (captured) or when more than 100 units (escaped)
                following = false;
                return;
            }

            // moves the enemy towards the player
            float speed = 70 * Gdx.graphics.getDeltaTime(); // Speed in units per second
            float directionX = distanceX / distance; // Basic Vector Math to get unit vector direction without any extra magnitude
            float directionY = distanceY / distance;

            // Update enemy position
            enemy.position.x += directionX * speed;
            enemy.position.y += directionY * speed;

            // Update scan range based on the enemy's new position
            enemy.scanRange.setX(enemy.position.x - 24);
            enemy.scanRange.setY(enemy.position.y - 30);
        }
    }
}
