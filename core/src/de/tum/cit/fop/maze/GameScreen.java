package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.entity.Player;
//banans comment
/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private final RenderMap map;
    private Player player;

    private float sinusInput = 0f;

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 1f;
        this.map = new RenderMap(game, camera);

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        float playerX = map.getStartPointx();
        float playerY = map.getStartPointy();
        float playerWidth = map.getTileSize();
        float playerHeight = 64;
        player = new Player(playerX, playerY, playerWidth, playerHeight, 200);

        player.setCurrentAnimation(game.getCharacterIdleAnimation());
    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {

        Animation<TextureRegion> currentAnimation = game.getCharacterIdleAnimation();

        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

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

        player.update(delta, map.getMapBounds());

        camera.position.set(player.getPosition().x + 8, player.getPosition().y + 16, 0);
        camera.update();

        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        map.render();
        player.render(game.getSpriteBatch());


        // Draw the character next to the text :) / We can reuse sinusInput here
//        game.getSpriteBatch().draw(currentAnimation.getKeyFrame(sinusInput, true), camera.position.x-16, camera.position.y -32, 16, 32);
        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.dispose();
    }

    // Additional methods and logic can be added as needed for the game screen


    public MazeRunnerGame getGame() {
        return game;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public BitmapFont getFont() {
        return font;
    }

    public RenderMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public float getSinusInput() {
        return sinusInput;
    }

    public void setSinusInput(float sinusInput) {
        this.sinusInput = sinusInput;
    }
}
