package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.SoundManager;

import de.tum.cit.fop.maze.abilities.*;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;
import de.tum.cit.fop.maze.arbitrarymap.MapManager;
import de.tum.cit.fop.maze.entity.Enemy;
import de.tum.cit.fop.maze.entity.HUD;
import de.tum.cit.fop.maze.entity.Player;

import java.util.*;
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
    private MapManager mapManager;
    private CollisionManager colManager;

    private Player player;
//    private Enemy enemy;
    private List<Powerup> mapPowerups;

    private float tileSize;
    private boolean following=false;

    private ShapeRenderer shapeRenderer;

    private Texture darkCircleoverlay;// Store all lights
    private HUD hud;
    private boolean isGameOver;

    private Texture lightTexture;
    /// updated the constructor to take a map path
    private String mapPath;
    /// pause trigger
    private boolean isPaused;
    private PauseOverlay pauseOverlay;
    private SoundManager soundManager;
    private float musicVolume;
    private Map<String, Integer> mainState = new HashMap<String, Integer>();

    private List<Enemy> enemies;


    public GameScreen(ScreenManager game, String mapPath) {
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
        darkCircleoverlay = new Texture("DK.png");


        //this is from the template should be good later
//        font = game.getSkin().getFont("font");

        //MAP STUFF::
        //decided to load map in the game screen since it's super simple in libgdx with tiled
        // I modified this to load a map from a selector

        tiledMap = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        this.mapManager = new MapManager(tiledMap);
        this.colManager = new CollisionManager(mapManager.getCollisionObjects(), mapManager.getDoorObjects(), mapManager.getEventObjects());
        isGameOver = false;

        //THIS IS ALL PLAYER THINGS:
        Optional<RectangleMapObject> startObject = mapManager.getEventObjects().stream()
                .filter(recObj -> "Start".equals(recObj.getProperties().get("type")))
                .findFirst();

        float startPlayerX = (2 * tileSize) + tileSize / 2;
        float startPlayerY = (2 * tileSize);

        if (startObject.isPresent()) {
            RectangleMapObject start = startObject.get();
            startPlayerX = (float) start.getProperties().get("x");
            startPlayerY = (float) start.getProperties().get("y");
        }

        //sound stuff.
        this.soundManager = game.getSoundManager();

        mainState.put("crackles", 1);
        mainState.put("wind", 1);
        mainState.put("piano", 1);
        mainState.put("strings", 0);
        mainState.put("pad", 1);
        mainState.put("drums", 0);
        mainState.put("bass", 1);

        soundManager.onGameStateChange(mainState);

        //just initializing the player
        player = new Player(startPlayerX, startPlayerY, tiledMap, 3, 100, new ArrayList<String>(), 0, soundManager);
        player.setCurrentAnimation(game.getCharacterDownIdleAnimation());

        hud = new HUD(game.getSpriteBatch(), game, player);

        enemies = new ArrayList<>();

        for (RectangleMapObject enemySpawn : mapManager.getEnemies())   {
            Rectangle rectangle = enemySpawn.getRectangle();
            Enemy enemy = null;

            String type = enemySpawn.getProperties().get("type", String.class);
//                System.out.println("Adding powerup");

            if ("Slime".equals(type)) {
                enemy = new Enemy(rectangle.x, rectangle.y, player, hud, soundManager);
            }

            if (enemy != null) {
                enemies.add(enemy);
            }
        }

//        this.enemy = new Enemy(200, 250, player, hud, soundManager);



        this.mapPowerups = mapManager.getPowerups();


    }
//    public void completeLevel(){
//        //updates the index of the map being played
//        game.setIndexOfTheMapBeingPlayed(game.getIndexOfTheMapBeingPlayed()+1);
//
//
//    }

    public Player getPlayer() {
        return player;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public Map<String, Integer> getMainState() {
        return mainState;
    }

    @Override
    public void render(float delta) {

        if (isPaused) {
            pauseOverlay.setVisible(isPaused);
            pauseOverlay.render(delta);
            // for the music
            musicVolume = pauseOverlay.getMusicVolume();
            soundManager.setMusicVolume(musicVolume);
            soundManager.onGameStateChange(pauseOverlay.getPauseState());
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                soundManager.onGameStateChange(mainState);
                isPaused = false;
            }
            return;
        } else {

            if (pauseOverlay != null) {
                musicVolume = pauseOverlay.getMusicVolume();
            } else {
                musicVolume = 0.5f;
            }

            /// WINNING
            if (Gdx.input.isKeyJustPressed(Input.Keys.X) || colManager.isWonLevel()) {
                winLevel();
            }
            //health trigger
//            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
//                player.setHealth(player.getHealth() - 1);
//            }
            if (player.getHealth() == 0) {
                soundManager.playSound("losing sound");
                isGameOver = true;
                player.setHealth(5);
                player.saveState("playerstate.txt");


                game.setScreen(new GameOverScreen(game));
            }


            //input updating ; find the method below for details
            handleInput();
            // updating characters
            player.update(delta, colManager);
//            if (colManager.checkListCollision(mapManager.getTrapObjects(), player.collider))
//                player.takeDamage();
            enemies.forEach(enemy1 -> enemy1.update(delta));
            hud.updateHUD();
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

//        System.out.println(player.getKeys());

//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(1, 0, 0, 1); // Red color
//        shapeRenderer.rect(enemy.damageCollider.getX(), enemy.damageCollider.getY(), enemy.damageCollider.width, enemy.damageCollider.height);
//        shapeRenderer.rect(player.collider.getX(), player.collider.getY(), player.collider.width, player.collider.height);
//        shapeRenderer.end();

        //I don't get projectionmatrices, needed to be attached to the spritebatch
// Set the projection matrix for the game camera
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().enableBlending();
        game.getSpriteBatch().begin();

//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // Line mode to draw the border of the collider
//
//        // Assuming player.getCollider() returns a Rectangle or similar object
//        shapeRenderer.rect(player.getCollider().getX(), player.getCollider().getY(), player.getCollider().getWidth(), player.getCollider().getHeight(), Color.RED, Color.RED, Color.RED, Color.RED);
//
//        shapeRenderer.end();

//        shapeRenderer.setProjectionMatrix(camera.combined); // Set the projection matrix
//        shapeRenderer.begin(); // Specify ShapeType
//        shapeRenderer.setColor(1, 0, 0, 1f); // Red color
//        shapeRenderer.rect(player.collider.getX(), player.collider.getY(), player.collider.width, player.collider.height);
//        shapeRenderer.end();

        //loading powerups on map
        if (!mapPowerups.isEmpty()) {
            Iterator<Powerup> iterator = mapPowerups.iterator();
            while (iterator.hasNext()) {
                Powerup powerup = iterator.next();
                Vector2 position = powerup.getPosition();
                game.getSpriteBatch().draw(powerup.getTexture(), position.x, position.y);

                if (powerup.checkPickUp(player)) {
                    player.getPowerUps().add(powerup.pickUp());
                    powerup.applyEffect(player);
                    iterator.remove();
                    hud.updateHearts(player.getHealth());
                }
            }
        }

        //draws doors and sets their textures
        mapManager.getDoorObjects().forEach(door -> game.getSpriteBatch().draw(door.getCurrentTexture(), door.getColliderObject().getRectangle().x, door.getColliderObject().getRectangle().y));


// Render the player and enemy
        player.render(game.getSpriteBatch());
        for (Enemy enemy : enemies) {
            if(!enemy.isDead){
                game.getSpriteBatch().draw(enemy.getEnemy(), enemy.position.x, enemy.position.y);
            }
        }


// Apply the dark circle overlay
        game.getSpriteBatch().setColor(1, 1, 1, 0.9f);
//        game.getSpriteBatch().draw(darkCircleoverlay, player.getPosition().x - darkCircleoverlay.getWidth() / 4, player.getPosition().y - darkCircleoverlay.getHeight() / 4, 960, 540);
        game.getSpriteBatch().setColor(1, 1, 1, 1);

        game.getSpriteBatch().end();

// Set the projection matrix for the HUD
        game.getSpriteBatch().setProjectionMatrix(hud.stage.getCamera().combined);
        hud.updateHearts(player.getHealth());
        hud.stage.draw();

        //this is so that some walls render after the player (over), but now that collisions are working this isn't as necessary, could be useful for smth else
//        mapRenderer.render(new int[]{1, 2});

        //playing music
    }

    private void winLevel() {
        // when a level is finished
        // we first add it to the completed levels in the user data
        // the map path is of form TiledMaps/mapName
        // so we need to strip it just to the mapName
        String mapName = mapPath.substring(mapPath.lastIndexOf('/') + 1);
        if (!game.getUser().getCompletedLevels().contains(mapName)) {
            game.getUser().getCompletedLevels().add(mapName);
            game.getUser().saveUserData("user_data.ser");
            player.saveState("playerstate.txt");
        }
        //now when the goToGame is called, we make sure it does not go to a finished map
        // we already did that, so we can call goToGame in the ScreenManager
        // and it won't load an level that's added to the completed levels
        // we debug by prinitng which level we complete by pressing X
        // and which levels are we loading
        game.setScreen(new VictoryScreen(game));
        colManager.setWonLevel(false);
    }


    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.move(Player.Direction.LEFT);
            player.setCurrentAnimation(game.getCharacterLeftAnimation());
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            player.move(Player.Direction.RIGHT);
            player.setCurrentAnimation(game.getCharacterRightAnimation());
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            for (Enemy enemy : enemies) {
                if ((player.getCollider().overlaps(enemy.damageCollider))){
                    if((player.getCurrentAnimation().equals(game.getCharacterDownIdleAnimation())||player.getCurrentAnimation().equals(game.getCharacterDownAnimation()))){
                        player.setAdjust(true);
                        player.setCurrentAnimation(game.getcharacterDownAttackAnimation());
                    } else if ((player.getCurrentAnimation().equals(game.getCharacterRightIdleAnimation()))||player.getCurrentAnimation().equals(game.getCharacterRightAnimation())) {
                        player.setCurrentAnimation(game.getCharacterRightAttackAnimation());
                    } else if ((player.getCurrentAnimation().equals(game.getCharacterLeftIdleAnimation()))||player.getCurrentAnimation().equals(game.getCharacterLeftAnimation())) {
                        player.setCurrentAnimation(game.getCharacterLeftAttackAnimation());
                    } else if ((player.getCurrentAnimation().equals(game.getCharacterUpIdleAnimation()))||player.getCurrentAnimation().equals(game.getCharacterUpAnimation())) {
                        player.setAdjust(true);
                        player.setCurrentAnimation(game.getcharacterUpAttackAnimation());
                    }
                    player.attack(enemy);
                }
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.move(Player.Direction.UP);
            player.setCurrentAnimation(game.getCharacterUpAnimation());
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.move(Player.Direction.DOWN);
            player.setCurrentAnimation(game.getCharacterDownAnimation());
        } else if(!player.isAttack) {
            if(player.getCurrentAnimation().equals(game.getCharacterDownAnimation())||player.getCurrentAnimation().equals(game.getCharacterDownAttackAnimation())) {
                player.setCurrentAnimation(game.getCharacterDownIdleAnimation());
            } else if (player.getCurrentAnimation().equals(game.getCharacterRightAnimation())||player.getCurrentAnimation().equals(game.getCharacterRightAttackAnimation())) {
                player.setCurrentAnimation(game.getCharacterRightIdleAnimation());
            } else if (player.getCurrentAnimation().equals(game.getCharacterLeftAnimation())||player.getCurrentAnimation().equals(game.getCharacterLeftAttackAnimation())) {
                player.setCurrentAnimation(game.getCharacterLeftIdleAnimation());
            } else if (player.getCurrentAnimation().equals(game.getCharacterUpAnimation())||player.getCurrentAnimation().equals(game.getcharacterUpAttackAnimation())) {
                player.setCurrentAnimation(game.getCharacterUpIdleAnimation());
            }
            player.stop();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (!player.isSprinting()) {
                player.setSpeed(player.getSpeed() * 1.50f);
                player.setSprinting(true);

            }
        } else {
            if (player.isSprinting()) {
                player.setSpeed(player.getSpeed() / 1.50f);
                player.setSprinting(false);

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
        pauseOverlay.dispose();
    }

    @Override
    public void show() {
        ///loading the player state from a txt file after resuming

        player.loadState("playerstate.txt");
        System.out.println("I have this amount of hearts:" + player.getHealth());
        pauseOverlay = new PauseOverlay(this, game);
        soundManager.onGameStateChange(mainState);

    }

    public PauseOverlay getPauseOverlay() {
        return pauseOverlay;
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}
