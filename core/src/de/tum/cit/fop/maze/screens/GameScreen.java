package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import de.tum.cit.fop.maze.ScreenManager;
import de.tum.cit.fop.maze.SoundManager;

import de.tum.cit.fop.maze.abilities.*;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;
import de.tum.cit.fop.maze.arbitrarymap.MapManager;
import de.tum.cit.fop.maze.entity.Door;
import de.tum.cit.fop.maze.entity.Enemy;
import de.tum.cit.fop.maze.entity.HUD;
import de.tum.cit.fop.maze.entity.Node;
import de.tum.cit.fop.maze.entity.Player;

import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * The GameScreen class handles gameplay, rendering the Tiled map and the player.
 */
public class GameScreen implements Screen {
    //Map Path
    private final ScreenManager game;
    private final OrthographicCamera camera;
    private Texture ovrly;
    private float pulseTimer = 0f; // Timer for the pulsing effect
    private final float PULSE_SPEED = 8f; // Adjust this to control the pulse speed (higher = faster)
    private final float MIN_ALPHA = 0.3f; // Minimum alpha value for the effect
    private final float MAX_ALPHA = 0.8f; // Maximum alpha value for the effect



//    private final BitmapFont font;

    //Tiled map which is an object
    private TiledMap tiledMap;
    Texture arrowTexture = new Texture("icons/File1.png");
    Sprite arrowSprite = new Sprite(arrowTexture);
    //tiled comes with a renderer
    private OrthogonalTiledMapRenderer mapRenderer;
    private MapManager mapManager;
    private CollisionManager colManager;

    private Player player;
    private int keysCollectedCounter;
//    private Enemy enemy;
    private List<Powerup> mapPowerups;

    private float tileSize;
    private ParticleEffect particleEffect;
    private ParticleEffect particleEffect2;
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
    private List<Key> keysInTheMap;

    private List<Enemy> enemies;

    private boolean showDebugOverlay = false;

    private float gameTime;

    private float cameraZoom;

    public String getMapPath() {
        return mapPath;
    }

    public GameScreen(ScreenManager game, String mapPath) {
        cameraZoom = 1f;

        this.mapPath = mapPath;
        shapeRenderer = new ShapeRenderer();

        //game is game
        this.game = game;
        this.tileSize = 16.0f;

        //CAMERA THINGS:
        camera = new OrthographicCamera();

        //this was kinda from before, i don't understand all this
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        camera.zoom = cameraZoom;
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

        ovrly=new Texture("bld.png");

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
        player = new Player(startPlayerX, startPlayerY, tiledMap, 3, 100, new ArrayList<String>(), 0, soundManager, camera);
        player.setCurrentAnimation(game.getCharacterDownIdleAnimation());

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("TiledMaps/particles/hh.p"), Gdx.files.internal("TiledMaps/particles/"));
        particleEffect2=new ParticleEffect();
        particleEffect2.load(Gdx.files.internal("TiledMaps/particles/vv.p"), Gdx.files.internal("TiledMaps/particles/"));
        particleEffect.getEmitters().forEach(emitter -> {
            for (TextureRegion region : emitter.getSprites()) { // Use getSprites() for multiple textures
                region.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        });
        // Set the position of the particle effect
        particleEffect.setPosition(startPlayerX,startPlayerY );
        particleEffect2.setPosition(startPlayerX,startPlayerY );

        // Start the particle effect
        particleEffect.start();
        particleEffect2.start();
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

            gameTime = TimeUtils.nanoTime();
        }

        this.mapPowerups = mapManager.getPowerups();
        this.keysCollectedCounter= 0;
        // Check if there are Keys in the map
        keysInTheMap = mapPowerups.stream()
                .filter(Key.class::isInstance)
                .map(Key.class::cast)
                .collect(Collectors.toList());
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

        gameTime+=delta;

        if (isPaused) {
            pauseOverlay.setVisible(isPaused);
            pauseOverlay.render(delta);
            float lastKeySoundVolumeBeforePause = soundManager.getKeySoundVolume();
            soundManager.setKeySoundVolume(0f);
            // for the music
            musicVolume = pauseOverlay.getMusicVolume();
            soundManager.setMusicVolume(musicVolume);
            soundManager.onGameStateChange(pauseOverlay.getPauseState());
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                soundManager.setKeySoundVolume(lastKeySoundVolumeBeforePause);
                soundManager.onGameStateChange(mainState);
                isPaused = false;

            }

            camera.zoom = cameraZoom;

            return;

        }
        else {

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
            if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
                player.setHealth(player.getHealth() - 1);
            }
            if (player.getHealth() == 0) {

                isGameOver = true;
                player.setHealth(5);
                player.saveState("playerstate.txt");

//                soundManager.playSound("mcDeath_sfx");
                soundManager.playSound("losing sound");
                game.setScreen(new GameOverScreen(game));
            }

            /// key sound logic
            // play the key sound
            soundManager.playKeySound();
            Key closestKey = checkForKeysAndGetTheClosestOne(keysInTheMap);
            // if there are keys in the map and we haven't collected them yet
            if (!keysInTheMap.isEmpty()) {
                updateKeySound(closestKey);
            }


                //input updating ; find the method below for details
            handleInput();
            // updating characters
//            System.out.println(player.getCurrentAnimation().getKeyFrameIndex(gameTime));
            player.update(delta, colManager);
//            if (colManager.checkListCollision(mapManager.getTrapObjects(), player.collider))
//                player.takeDamage();
            enemies.forEach(enemy1 -> enemy1.update(delta, colManager));
            hud.updateHUD();
        }


        // Clear screen before prinitng each frame
        ScreenUtils.clear(0, 0, 0, 1);
        // Clear the framebuffer
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // moving the camera with the player
        camera.position.set(player.getPosition().x+8, player.getPosition().y+8, 0);
        camera.update();
        //basically its just saying the map should be shown in (camera)
        mapRenderer.setView(camera);
        if(player.getHealth()<2) {
            mapRenderer.getBatch().setColor(0.7f, 0.7f, 0.7f, 1f);
        }else{
            mapRenderer.getBatch().setColor(1f, 1f, 1f, 1f);
        }
        //literally just renders the map. that's it... but it is now rendering layers specfiically ina. diff order
        mapRenderer.render();

//        System.out.println(player.getKeys());

//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(1, 0, 0, 1); // Red color
//        shapeRenderer.rect(enemy.damageCollider.getX(), enemy.damageCollider.getY(), enemy.damageCollider.width, enemy.damageCollider.height);
//        shapeRenderer.rect(player.collider.getX(), player.collider.getY(), player.collider.width, player.collider.height);
//        shapeRenderer.end();

        //shapes need to be outside spritebatch
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL20.GL_BLEND); // Enable blending for transparency

        //I don't get projectionmatrices, needed to be attached to the spritebatch
// Set the projection matrix for the game camera
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().enableBlending();
        game.getSpriteBatch().begin();

        if (showDebugOverlay) {
            int fps = Gdx.graphics.getFramesPerSecond();
            BitmapFont font = new BitmapFont();
            font.draw(game.getSpriteBatch(), "FPS: " + fps, 0, 0);

            shapeRenderer.setColor(Color.GOLD);
            shapeRenderer.rect(player.collider.x, player.collider.y, player.collider.width, player.collider.height);
            shapeRenderer.setColor(Color.PINK);
//            shapeRenderer.rect(player.getWeapon().getAttackArea().x, player.getWeapon().getAttackArea().y, player.getWeapon().getAttackArea().width,player.getWeapon().getAttackArea().height);
            shapeRenderer.setColor(1, 0, 0, 0.5f); // Semi-transparent red
//            shapeRenderer.arc(player.collider.x, player.collider.y, player.getWeapon().getRange(),
//                    (float) Math.toDegrees(player.getWeapon().getRotationAngle()) - player.getWeapon().getSectorAngle() / 2, player.getWeapon().getSectorAngle());
            shapeRenderer.rect(player.getAttackHitbox().x, player.getAttackHitbox().y, player.getAttackHitbox().width, player.getAttackHitbox().height);
            //enemy related debug
            shapeRenderer.rect(player.newPos.x, player.newPos.y, player.newPos.width, player.newPos.height);

            for (RectangleMapObject walls : mapManager.getCollisionObjects())   {
                Rectangle wallRec = walls.getRectangle();
                shapeRenderer.setColor(Color.GRAY);
                shapeRenderer.rect(wallRec.x, wallRec.y, wallRec.width, wallRec.height);
            }
            for (Enemy enemy : enemies) { // Assuming you have a list of enemies
                List<Node> path = enemy.getCurrentPath();
                if (path != null) {
                    for (int i = 0; i < path.size() - 1; i++) {
                        Node current = path.get(i);
                        Node next = path.get(i + 1);

                        // Draw a line between nodes
                        shapeRenderer.setColor(1, 0, 0, 1); // Red for the path
                        shapeRenderer.line(current.x, current.y, next.x, next.y);
                        shapeRenderer.setColor(Color.BLUE);
                        shapeRenderer.point(current.x, current.y, 0);
                    }
                }
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(enemy.position.x-140, enemy.position.y-140, 2*140, 2*140);
                shapeRenderer.setColor(0, 1, 0, 1);
                shapeRenderer.rect(enemy.scanRange.x, enemy.scanRange.y, enemy.scanRange.width, enemy.scanRange.height);
                shapeRenderer.setColor(Color.ORANGE);
                shapeRenderer.rect(enemy.damageCollider.x, enemy.damageCollider.y, enemy.damageCollider.width, enemy.damageCollider.height);
            }
        }

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


                    if(powerup instanceof Key){

                        keysInTheMap.remove((Key) powerup);
                        if(keysInTheMap.isEmpty()){
                            soundManager.setKeySoundVolume(0f);
                        }
                        else{
                        Key closestKey = checkForKeysAndGetTheClosestOne(keysInTheMap);
                        updateKeySound(closestKey);
                        }
                    }

                    iterator.remove();
                    hud.updateHearts(player.getHealth());
                }
            }
        }

        //draws doors and sets their textures
//        mapManager.getDoorObjects().forEach(door -> game.getSpriteBatch().draw(door.getCurrentTexture(), door.getColliderObject().getRectangle().x, door.getColliderObject().getRectangle().y));

        trapexplosion();
// Render the player and enemy
        player.render(game.getSpriteBatch());


//        player.getWeapon().render(game.getSpriteBatch(), delta, player.getPosition());
        particleEffect.update(Gdx.graphics.getDeltaTime());

        // Clear the screen and render the particle effect
        particleEffect.draw(game.getSpriteBatch());

        particleEffect2.update(Gdx.graphics.getDeltaTime());

        particleEffect2.draw(game.getSpriteBatch());

        // Restart the effect if it's finished

        drawarrow();
        for (Enemy enemy : enemies) {
            if(!enemy.isDead){
                game.getSpriteBatch().draw(enemy.getEnemy(), enemy.position.x, enemy.position.y);
            }
        }


// Apply the dark circle overlay
//        game.getSpriteBatch().setColor(1, 1, 1, 0.9f);
//        game.getSpriteBatch().draw(darkCircleoverlay, player.getPosition().x - darkCircleoverlay.getWidth() / 4, player.getPosition().y - darkCircleoverlay.getHeight() / 4, 960, 540);
        game.getSpriteBatch().setColor(1, 1, 1, 1);

        game.getSpriteBatch().end();

// Set the projection matrix for the HUD
        game.getSpriteBatch().setProjectionMatrix(hud.stage.getCamera().combined);
        hud.updateHearts(player.getHealth());
        hud.stage.draw();
        pulseTimer += delta * PULSE_SPEED;

        // Calculate the alpha value using a sine wave
        float bloodAlpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * ((float) Math.sin(pulseTimer) * 0.5f + 0.5f);

        // Draw the pulsing blood overlay
        game.getSpriteBatch().begin();
        game.getSpriteBatch().setColor(0.8f, 0f, 0f, bloodAlpha);
        if(player.getHealth()<2) {// Set red color with pulsing alpha
            game.getSpriteBatch().draw(ovrly, 0, 0, 1580, 1080); // Replace `ovrly` with your blood overlay texture
        }
        game.getSpriteBatch().setColor(1f, 1f, 1f, 1f); // Reset color to default
        game.getSpriteBatch().end();


        //this is so that some walls render after the player (over), but now that collisions are working this isn't as necessary, could be useful for smth else
//        mapRenderer.render(new int[]{1, 2});
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }

    //let's find the closest key
    public Key checkForKeysAndGetTheClosestOne(List<Key> keys) {

        //get player position
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        // create a sudo key
        Key closestKey = null;
        // create a variable for minDistance
        float closestDistance = Float.MAX_VALUE;

        for (Key key : keys) {
            // check distance for each key in our map and calculate
            double xDiff = key.getPosition().x - playerX;
            double yDiff = key.getPosition().y - playerY;
            double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
            // if it's less than the last minDistance
            if (distance < closestDistance) {
                //then this key is the closest key
                closestKey = key;
                // and this distance is the new minDistance
                closestDistance = (float) distance; // Update closestDistance
            }
        }
        // return the key so we can see how far it is and adjust volume.

        return closestKey;
    }

    public void updateKeySound(Key key){
            // check wich is the closest key
//            Key closestKey = checkForKeysAndGetTheClosestOne(keysInTheMap);

            // if there's a closest key
            if (key != null) {
                // find the distance between this key and the player
                double xDiff = key.getPosition().x - player.getPosition().x;
                double yDiff = key.getPosition().y - player.getPosition().y;
                double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
                // if the distance is greater than 16 let's say
                if (distance >= 300) {
                    // mute the key sound
                    soundManager.setKeySoundVolume(0f);
                } else {
                    // else, increase sound when distance decreases
                    float keySoundVolume = 1f - (float) distance / 300; // Normalized
//                    System.out.println(keySoundVolume+" key sound volume supposdly");

                    soundManager.setKeySoundVolume(keySoundVolume);

                    float currentvol = soundManager.getKeySoundVolume();
//                    System.out.println(currentvol+" key sound volume actually");
                }
            }
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            showDebugOverlay = !showDebugOverlay;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.move(Player.Direction.LEFT);
            if(player.isSprinting()){
                player.setCurrentAnimation(game.getRunLeftAnimation());
            }
            else {
                player.setCurrentAnimation(game.getCharacterLeftAnimation());
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {

            player.move(Player.Direction.RIGHT);
            if(player.isSprinting()){
                player.setCurrentAnimation(game.getRunRightAnimation());
            }
            else {
                player.setCurrentAnimation(game.getCharacterRightAnimation());
            }
        } else if((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) && !player.isAttack){
            player.stop();
//            particleEffect.reset();
            handleBreaking(tiledMap);
            handleGemBreak(tiledMap);
            if ((player.getCurrentAnimation().equals(game.getCharacterDownIdleAnimation()) || player.getCurrentAnimation().equals(game.getCharacterDownAnimation()))) {
                player.setAdjust(true);
                player.setCurrentAnimation(game.getcharacterDownAttackAnimation());
            } else if ((player.getCurrentAnimation().equals(game.getCharacterRightIdleAnimation())) || player.getCurrentAnimation().equals(game.getCharacterRightAnimation())) {
                player.setCurrentAnimation(game.getCharacterRightAttackAnimation());
            } else if ((player.getCurrentAnimation().equals(game.getCharacterLeftIdleAnimation())) || player.getCurrentAnimation().equals(game.getCharacterLeftAnimation())) {
                player.setCurrentAnimation(game.getCharacterLeftAttackAnimation());
            } else if ((player.getCurrentAnimation().equals(game.getCharacterUpIdleAnimation())) || player.getCurrentAnimation().equals(game.getCharacterUpAnimation())) {
                player.setAdjust(true);
                player.setCurrentAnimation(game.getcharacterUpAttackAnimation());
            }
            player.getCurrentAnimation().setPlayMode(Animation.PlayMode.NORMAL);

            //attack cooldown

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                player.isAttack = false;
                scheduler.shutdown(); // Shut down the scheduler
            }, 400, TimeUnit.MILLISECONDS);
            player.attack(enemies);
//            for (Enemy enemy : enemies) {
//                if ((player.getCollider().overlaps(enemy.damageCollider))){
//                    player.attack(enemy);
//                } else {
//                    player.attack(null);
//                }
//            }
//            float lastDamageTime = 0;
//            if (TimeUtils.nanoTime() - lastDamageTime >= cooldownTime * 1000000000L) {
//                // proceed with damage logic
//                attack();
//                // update last damage time
//                lastDamageTime = TimeUtils.nanoTime();
//            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.move(Player.Direction.UP);
            if(player.isSprinting()){
                player.setCurrentAnimation(game.getRunUpAnimation());
            }
            else {
                player.setCurrentAnimation(game.getCharacterUpAnimation());
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.move(Player.Direction.DOWN);
            if(player.isSprinting()){
                player.setCurrentAnimation(game.getRunDownAnimation());
            }
            else {
                player.setCurrentAnimation(game.getCharacterDownAnimation());
            }
        } else if (!player.isAttack) {
            if (player.getCurrentAnimation().equals(game.getCharacterDownAnimation()) || player.getCurrentAnimation().equals(game.getCharacterDownAttackAnimation())||player.getCurrentAnimation().equals(game.getRunDownAnimation())) {
                player.setCurrentAnimation(game.getCharacterDownIdleAnimation());
                player.setAdjust(false);
            } else if (player.getCurrentAnimation().equals(game.getCharacterRightAnimation()) || player.getCurrentAnimation().equals(game.getCharacterRightAttackAnimation())||player.getCurrentAnimation().equals(game.getRunRightAnimation())) {
                player.setCurrentAnimation(game.getCharacterRightIdleAnimation());
                player.setAdjust(false);
            } else if (player.getCurrentAnimation().equals(game.getCharacterLeftAnimation()) || player.getCurrentAnimation().equals(game.getCharacterLeftAttackAnimation())||player.getCurrentAnimation().equals(game.getRunLeftAnimation())) {
                player.setCurrentAnimation(game.getCharacterLeftIdleAnimation());
                player.setAdjust(false);
            } else if (player.getCurrentAnimation().equals(game.getCharacterUpAnimation()) || player.getCurrentAnimation().equals(game.getcharacterUpAttackAnimation())||player.getCurrentAnimation().equals(game.getRunUpAnimation())) {
                player.setCurrentAnimation(game.getCharacterUpIdleAnimation());
                player.setAdjust(false);
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
    public void handleBreaking(TiledMap map) {
        MapLayer objectLayer = map.getLayers().get("BreakableWalls");
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("Opening");
        TiledMapTileLayer tileLayer1 = (TiledMapTileLayer) map.getLayers().get("Cracks");
        TiledMapTileLayer tileLayer2 = (TiledMapTileLayer) map.getLayers().get("Cracks2");

        Door door = colManager.checkDoorCollision(player.getCollider());
        if (door != null && player.getKeys() > 0)   {
            if (door.getDoorHealth() > 0)   {
                switch (door.getDoorHealth())   {
                    case 2:
//                        System.out.println("playsound");
                        soundManager.playSound("mineRock1");
                        tiledMap.getLayers().get("Anim").setVisible(true);
                        break;
                    case 1:
                        soundManager.playSound("mineRock2");
//                        System.out.println("playsound");
                        break;
                }
                door.setDoorHealth(door.getDoorHealth()-1);
//            door.setCurrentTexture(door.getOpenTexture());
            } else if (door.getDoorHealth() == 0)  {
                colManager.openDoor(door);
                soundManager.playSound("mineRock3");
//                System.out.println("playsound");
                for (MapObject object : objectLayer.getObjects()) {
                    if(player.collider.overlaps(((RectangleMapObject) object).getRectangle())) {
//                        tiledMap.getLayers().get("Anim").setVisible(true);
//                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//                        scheduler.schedule(() -> {
                            tiledMap.getLayers().get("Anim").setVisible(false);
                            breakTiles(object, tileLayer, 16, 16);
                            breakTiles(object,tileLayer1,16,16);
                            breakTiles(object,tileLayer2,16,16);
//                            scheduler.shutdown(); // Shut down the scheduler
//                        }, 1200, TimeUnit.MILLISECONDS);
                    }
                }
                player.setKeys(player.getKeys()-1);
            }
        }
    }
    public void handleGemBreak(TiledMap map) {
        MapLayer objectLayer = map.getLayers().get("BreakableWalls");
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("additives");
        RectangleMapObject r=null;

        for (MapObject object : objectLayer.getObjects()) {
            if ((player.collider.overlaps(((RectangleMapObject) object).getRectangle())) &&
                    (((RectangleMapObject) object).getProperties().get("type") != null) &&
                    (((RectangleMapObject) object).getProperties().get("type")).equals("Gem")) {

                r = (RectangleMapObject) object.getProperties().get("collider");
                particleEffect2.setPosition(r.getRectangle().x + r.getRectangle().getWidth() / 2,
                        r.getRectangle().y + r.getRectangle().getHeight() / 2);
                particleEffect2.reset(); // Start the particle effect

                // Delay the breaking effect
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        // Remove the object after the delay
                        objectLayer.getObjects().remove(object);

                        // Break tiles
                        breakTiles(object, tileLayer, 16, 16);

                        // Update the player's keys
                        player.setKeys(player.getKeys() + 1);
                    }
                }, 0.2f); // Delay in seconds (1.2 seconds in this case)

                break; // Exit the loop after scheduling
            }
        }
        if(r!=null){
            System.out.println("Before removal: " + objectLayer.getObjects().getCount());
            map.getLayers().get("CollisionObjects").getObjects().remove(r);
            colManager.removeGem(r);
            System.out.println("After removal: " + objectLayer.getObjects().getCount());
        }
    }
    public void breakTiles(MapObject object, TiledMapTileLayer tileLayer, float tileWidth, float tileHeight) {
        // Get object properties
        float objectX = (float) object.getProperties().get("x");
        float objectY = (float) object.getProperties().get("y");
        float objectWidth = (float) object.getProperties().get("width");
        float objectHeight = (float) object.getProperties().get("height");

        // Convert to tile coordinates
        int tileXStart = (int) (objectX / tileWidth);
        int tileYStart = (int) (objectY / tileHeight);
        int tileXEnd = (int) ((objectX + objectWidth) / tileWidth)+1;
        int tileYEnd = (int) ((objectY + objectHeight) / tileHeight)+1;

        // Remove tiles
        for (int x = tileXStart; x < tileXEnd; x++) {
            for (int y = tileYStart; y < tileYEnd; y++) {
                tileLayer.setCell(x, y, null); // Clear the tile
            }
        }
    }



    public void setPaused(boolean paused) {
        isPaused = paused;
    }
    public void trapexplosion(){
        for (RectangleMapObject object : mapManager.getCollisionObjects()) {
            String objectType = object.getProperties().get("type", String.class);
            if ("Trap".equals(objectType)) {
                Rectangle objectBounds = object.getRectangle();

                // Check collision with player
                if (player.newPos.overlaps(objectBounds)) {
                    System.out.println("f");
                    // Play particle effect at the object's position
                    float effectX = objectBounds.x + objectBounds.width / 2;
                    float effectY = objectBounds.y + objectBounds.height / 2;

                    particleEffect.setPosition(effectX, effectY);
                    particleEffect.reset();
                    player.newPos.setX(player.getPosition().x);
                    player.newPos.setY(player.getPosition().y);// Restart the effect when triggered
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 2f, height / 2f);
    }

    @Override
    public void dispose() {
        soundManager.dispose();
        mapRenderer.dispose();
        tiledMap.dispose();
        pauseOverlay.dispose();
        shapeRenderer.dispose();
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
    public void drawarrow(){

        RectangleMapObject rect=null;
        for(RectangleMapObject r:colManager.getEventObjects()){
            if ("Finish".equals(r.getProperties().get("type", String.class))) {
                rect = r;
                break;
            }
        }

        if (rect != null) {
            // Player's position
            float playerX = player.getPosition().x;
            float playerY = player.getPosition().y;

// Finish object's position
            Rectangle finishBounds = rect.getRectangle();
            float finishX = finishBounds.x + finishBounds.width / 2;
            float finishY = finishBounds.y + finishBounds.height / 2;

// Calculate angle
            float angle = MathUtils.atan2(finishY - playerY, finishX - playerX) * MathUtils.radiansToDegrees;

            arrowSprite.setScale(0.05f);
            arrowSprite.setPosition(playerX-143, playerY-120);
            arrowSprite.setRotation(90+angle); // Adjust rotation (optional)

// In render method
            arrowSprite.draw(game.getSpriteBatch());
        }
    }

    public float getCameraZoom() {
        return cameraZoom;
    }

    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
    }
}
