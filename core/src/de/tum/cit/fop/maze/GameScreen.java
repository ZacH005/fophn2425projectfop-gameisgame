package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.entity.Player;

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
        //font = game.getSkin().getFont("font");

        //MAP STUFF::
        //decided to load map in the game screen since it's super simple in libgdx with tiled
        tiledMap = new TmxMapLoader().load("TiledMaps/CaveMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        //THIS IS ALL PLAYER THINGS:
        //need to make it so when the map loads, it chooses the specific location of the starting block
        //also the rest is so that it spawns in the middle of the tile, not in the corner/coordinate
        float startPlayerX = (2*tileSize)+tileSize/2;
        float startPlayerY = (2*tileSize);
        //just initializing the player
        player = new Player(startPlayerX, startPlayerY, 150, tiledMap);
        player.setCurrentAnimation(game.getCharacterIdleAnimation());
    }

    @Override
    public void render(float delta) {
        //options/pause button
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
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
        //literally just renders the map. that's it...
        mapRenderer.render();

        //I don't get projectionmatrices, needed to be attached to the spritebatch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        //renders the player, pretty chill
        player.render(game.getSpriteBatch());

        game.getSpriteBatch().end();
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
    }

    @Override
    public void show() { }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
