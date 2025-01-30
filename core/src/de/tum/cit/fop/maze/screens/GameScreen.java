package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import de.tum.cit.fop.maze.ScreenShake;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.*;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;
import de.tum.cit.fop.maze.arbitrarymap.MapManager;
import de.tum.cit.fop.maze.entity.*;

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
    private final ScreenManager game;
    private final OrthographicCamera camera;
    private Texture ovrly;
    private float pulseTimer = 0f;
    private final float PULSE_SPEED = 8f;
    private final float MIN_ALPHA = 0.3f;
    private final float MAX_ALPHA = 0.8f;

    private TiledMap tiledMap;
    private Texture arrowTexture = new Texture("icons/File1.png");
    private Sprite arrowSprite = new Sprite(arrowTexture);
    private OrthogonalTiledMapRenderer mapRenderer;
    private MapManager mapManager;
    private CollisionManager colManager;

    private Player player;
    private int keysCollectedCounter;
    private List<Powerup> mapPowerups;

    private float tileSize;
    private ParticleEffect particleEffect;
    private ParticleEffect particleEffect2;
    private boolean following = false;

    private ShapeRenderer shapeRenderer;

    private Texture darkCircleoverlay;
    private HUD hud;
    private boolean isGameOver;

    private Texture lightTexture;
    private String mapPath;
    private boolean isPaused;
    private PauseOverlay pauseOverlay;
    private SoundManager soundManager;
    private float musicVolume;
    private Map<String, Integer> mainState = new HashMap<>();
    private List<Key> keysInTheMap;

    private List<Enemy> enemies;

    private boolean showDebugOverlay = false;

    private float gameTime;

    private float cameraZoom;

    private EnemyManager enemyManager;

    private ScreenShake screenShake;

    /**
     * Gets the map path.
     *
     * @return The map path.
     */
    public String getMapPath() {
        return mapPath;
    }

    /**
     * Constructs a new GameScreen with the specified game, map path, and sound manager.
     *
     * @param game         The ScreenManager instance.
     * @param mapPath      The path to the map file.
     * @param soundManager The sound manager for playing sounds.
     */
    public GameScreen(ScreenManager game, String mapPath, SoundManager soundManager) {
        this.enemyManager = new EnemyManager();
        cameraZoom = 0.8f;

        this.mapPath = mapPath;
        shapeRenderer = new ShapeRenderer();

        this.game = game;
        this.tileSize = 16.0f;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        camera.zoom = cameraZoom;
        darkCircleoverlay = new Texture("DK.png");

        this.soundManager = soundManager;
        mainState.put("crackles", 1);
        mainState.put("wind", 1);
        mainState.put("piano", 1);
        mainState.put("strings", 0);
        mainState.put("pad", 1);
        mainState.put("drums", 0);
        mainState.put("bass", 1);
        mainState.put("slowerDrums", 1);
        soundManager.onGameStateChange(mainState);

        tiledMap = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        this.mapManager = new MapManager(tiledMap, soundManager);
        this.colManager = new CollisionManager(mapManager.getCollisionObjects(), mapManager.getDoorObjects(), mapManager.getEventObjects());
        isGameOver = false;

        ovrly = new Texture("bld.png");

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

        screenShake = new ScreenShake();
        screenShake.setOriginalPosition(camera.position.x, camera.position.y);

        player = new Player(startPlayerX, startPlayerY, tiledMap, 3, 100, new ArrayList<>(), 0, soundManager, camera, screenShake);
        player.setCurrentAnimation(game.getCharacterDownIdleAnimation());

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("TiledMaps/particles/hh.p"), Gdx.files.internal("TiledMaps/particles/"));
        particleEffect2 = new ParticleEffect();
        particleEffect2.load(Gdx.files.internal("TiledMaps/particles/vv.p"), Gdx.files.internal("TiledMaps/particles/"));
        particleEffect.getEmitters().forEach(emitter -> {
            for (TextureRegion region : emitter.getSprites()) {
                region.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        });

        if (mapPath.endsWith("UTutorial.tmx")) {
            hud = new HUD(game.getSpriteBatch(), game, player, true);
        } else {
            hud = new HUD(game.getSpriteBatch(), game, player, false);
        }

        enemies = new ArrayList<>();

        for (RectangleMapObject enemySpawn : mapManager.getEnemies()) {
            Rectangle rectangle = enemySpawn.getRectangle();
            Enemy enemy = null;

            String type = enemySpawn.getProperties().get("type", String.class);

            if ("Slime".equals(type)) {
                enemy = new MeleeEnemy(rectangle.x, rectangle.y, player, hud, soundManager, enemyManager.getSkeletonAnimations(), 3);
            } else if ("Range".equals(type)) {
                enemy = new RangeEnemy(rectangle.x, rectangle.y, player, hud, soundManager, enemyManager.getSkeletonAnimations(), 2);
            }

            if (enemy != null) {
                enemies.add(enemy);
            }

            gameTime = TimeUtils.nanoTime();
        }

        this.mapPowerups = mapManager.getPowerups();
        this.keysCollectedCounter = 0;
        keysInTheMap = mapPowerups.stream()
                .filter(Key.class::isInstance)
                .map(Key.class::cast)
                .collect(Collectors.toList());
        hud.setMaxGems(gemCounter());
        player.setMaxGems(gemCounter());
    }

    /**
     * Gets the player instance.
     *
     * @return The player instance.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the music volume.
     *
     * @param musicVolume The music volume to set.
     */
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    /**
     * Checks if the game is paused.
     *
     * @return True if the game is paused, false otherwise.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Gets the main state of the game.
     *
     * @return The main state of the game.
     */
    public Map<String, Integer> getMainState() {
        return mainState;
    }

    /**
     * Renders the game screen.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {
        gameTime += delta;

        if (isPaused) {
            pauseOverlay.setVisible(isPaused);
            pauseOverlay.render(delta);
            float lastKeySoundVolumeBeforePause = soundManager.getKeySoundVolume();
            soundManager.setKeySoundVolume(0f);
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
        } else {
            if (pauseOverlay != null) {
                musicVolume = pauseOverlay.getMusicVolume();
            } else {
                musicVolume = 0.5f;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.X) || colManager.isWonLevel()) {
                winLevel();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
                player.setHealth(player.getHealth() - 0.25f);
            }
            if (player.getHealth() == 0) {
                isGameOver = true;
                player.setHealth(5);
                player.saveState("playerstate.txt");
                soundManager.playSound("losing sound");
                game.setScreen(new GameOverScreen(game));
            }

            soundManager.playKeySound();
            Key closestKey = checkForKeysAndGetTheClosestOne(keysInTheMap);
            if (!keysInTheMap.isEmpty()) {
                updateKeySound(closestKey);
            }

            handleInput();
            player.update(delta, colManager);
            enemies.forEach(enemy1 -> enemy1.update(delta, colManager));
            hud.updateHUD(delta);
        }

        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(player.getPosition().x + 8, player.getPosition().y + 8, 0);
        clampCamera();
        camera.update();
        screenShake.setOriginalPosition(camera.position.x, camera.position.y);
        screenShake.update(delta, camera);

        mapRenderer.setView(camera);
        if (player.getHealth() < 2) {
            mapRenderer.getBatch().setColor(0.7f, 0.7f, 0.7f, 1f);
        } else {
            mapRenderer.getBatch().setColor(1f, 1f, 1f, 1f);
        }
        mapRenderer.render();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL20.GL_BLEND);

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
            shapeRenderer.setColor(1, 0, 0, 0.5f);
            shapeRenderer.rect(player.getAttackHitbox().x, player.getAttackHitbox().y, player.getAttackHitbox().width, player.getAttackHitbox().height);
            shapeRenderer.rect(player.newPos.x, player.newPos.y, player.newPos.width, player.newPos.height);

            for (RectangleMapObject walls : mapManager.getCollisionObjects()) {
                Rectangle wallRec = walls.getRectangle();
                shapeRenderer.setColor(Color.GRAY);
                shapeRenderer.rect(wallRec.x, wallRec.y, wallRec.width, wallRec.height);
            }
            for (Enemy enemy : enemies) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(enemy.getPosition().x - 140, enemy.getPosition().y - 140, 2 * 140, 2 * 140);
                if (enemy instanceof RangeEnemy)
                    shapeRenderer.rect(((RangeEnemy) enemy).getShootingRange().x, ((RangeEnemy) enemy).getShootingRange().y, ((RangeEnemy) enemy).getShootingRange().width, ((RangeEnemy) enemy).getShootingRange().height);
                shapeRenderer.setColor(0, 1, 0, 1);
                shapeRenderer.rect(enemy.scanRange.x, enemy.scanRange.y, enemy.scanRange.width, enemy.scanRange.height);
                shapeRenderer.setColor(Color.ORANGE);
                shapeRenderer.rect(enemy.damageCollider.x, enemy.damageCollider.y, enemy.damageCollider.width, enemy.damageCollider.height);
            }
        }

        if (!mapPowerups.isEmpty()) {
            Iterator<Powerup> iterator = mapPowerups.iterator();
            while (iterator.hasNext()) {
                Powerup powerup = iterator.next();
                Vector2 position = powerup.getPosition();
                game.getSpriteBatch().draw(powerup.getTexture(), position.x, position.y);

                if (powerup.checkPickUp(player)) {
                    player.getPowerUps().add(powerup.pickUp());
                    powerup.applyEffect(player);

                    if (powerup instanceof Key) {
                        keysInTheMap.remove((Key) powerup);
                        if (keysInTheMap.isEmpty()) {
                            soundManager.setKeySoundVolume(0f);
                        } else {
                            Key closestKey = checkForKeysAndGetTheClosestOne(keysInTheMap);
                            updateKeySound(closestKey);
                        }
                    }

                    iterator.remove();
                    hud.updateHearts(player.getHealth());
                }
            }
        }

        trapexplosion();

        enemies.forEach(enemy1 -> enemy1.render(game.getSpriteBatch()));
        player.render(game.getSpriteBatch());

        particleEffect.update(Gdx.graphics.getDeltaTime());
        particleEffect.draw(game.getSpriteBatch());

        particleEffect2.update(Gdx.graphics.getDeltaTime());
        particleEffect2.draw(game.getSpriteBatch());

        drawarrow();

        game.getSpriteBatch().end();

        game.getSpriteBatch().setProjectionMatrix(hud.stage.getCamera().combined);
        hud.updateHearts(player.getHealth());
        hud.stage.draw();
        pulseTimer += delta * PULSE_SPEED;

        float bloodAlpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * ((float) Math.sin(pulseTimer) * 0.5f + 0.5f);

        game.getSpriteBatch().begin();
        game.getSpriteBatch().setColor(0.8f, 0f, 0f, bloodAlpha);
        if (player.getHealth() < 2) {
            game.getSpriteBatch().draw(ovrly, 0, 0, hud.stage.getViewport().getScreenWidth(), hud.stage.getViewport().getScreenHeight());
        }
        game.getSpriteBatch().setColor(1f, 1f, 1f, 1f);
        game.getSpriteBatch().end();

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Finds the closest key to the player.
     *
     * @param keys The list of keys to check.
     * @return The closest key.
     */
    public Key checkForKeysAndGetTheClosestOne(List<Key> keys) {
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        Key closestKey = null;
        float closestDistance = Float.MAX_VALUE;

        for (Key key : keys) {
            double xDiff = key.getPosition().x - playerX;
            double yDiff = key.getPosition().y - playerY;
            double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
            if (distance < closestDistance) {
                closestKey = key;
                closestDistance = (float) distance;
            }
        }

        return closestKey;
    }

    /**
     * Updates the key sound based on the closest key.
     *
     * @param key The closest key.
     */
    public void updateKeySound(Key key) {
        if (key != null) {
            double xDiff = key.getPosition().x - player.getPosition().x;
            double yDiff = key.getPosition().y - player.getPosition().y;
            double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
            if (distance >= 300) {
                soundManager.setKeySoundVolume(0f);
            } else {
                float keySoundVolume = 1f - (float) distance / 300;
                soundManager.setKeySoundVolume(soundManager.getSfxVolume() * keySoundVolume);
            }
        }
    }

    /**
     * Handles the level win condition.
     */
    private void winLevel() {
        String mapName = mapPath.substring(mapPath.lastIndexOf('/') + 1);
        if (!game.getUser().getCompletedLevels().contains(mapName)) {
            game.getUser().getCompletedLevels().add(mapName);
            game.getUser().saveUserData("user_data.ser");
            player.saveState("playerstate.txt");
        }

        game.setScreen(new VictoryScreen(game, hud.getElapsedTime()));
        colManager.setWonLevel(false);
    }

    /**
     * Handles player input.
     */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            showDebugOverlay = !showDebugOverlay;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.move(Player.Direction.LEFT);
            if (player.isSprinting()) {
                player.setCurrentAnimation(game.getRunLeftAnimation());
            } else {
                player.setCurrentAnimation(game.getCharacterLeftAnimation());
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.move(Player.Direction.RIGHT);
            if (player.isSprinting()) {
                player.setCurrentAnimation(game.getRunRightAnimation());
            } else {
                player.setCurrentAnimation(game.getCharacterRightAnimation());
            }
        } else if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) && !player.isAttack) {
            player.stop();
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

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                player.isAttack = false;
                scheduler.shutdown();
            }, 400, TimeUnit.MILLISECONDS);
            player.attack(enemies);

        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.move(Player.Direction.UP);
            if (player.isSprinting()) {
                player.setCurrentAnimation(game.getRunUpAnimation());
            } else {
                player.setCurrentAnimation(game.getCharacterUpAnimation());
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.move(Player.Direction.DOWN);
            if (player.isSprinting()) {
                player.setCurrentAnimation(game.getRunDownAnimation());
            } else {
                player.setCurrentAnimation(game.getCharacterDownAnimation());
            }
        } else if (!player.isAttack) {
            if (player.getCurrentAnimation().equals(game.getCharacterDownAnimation()) || player.getCurrentAnimation().equals(game.getCharacterDownAttackAnimation()) || player.getCurrentAnimation().equals(game.getRunDownAnimation())) {
                player.setCurrentAnimation(game.getCharacterDownIdleAnimation());
                player.setAdjust(false);
            } else if (player.getCurrentAnimation().equals(game.getCharacterRightAnimation()) || player.getCurrentAnimation().equals(game.getCharacterRightAttackAnimation()) || player.getCurrentAnimation().equals(game.getRunRightAnimation())) {
                player.setCurrentAnimation(game.getCharacterRightIdleAnimation());
                player.setAdjust(false);
            } else if (player.getCurrentAnimation().equals(game.getCharacterLeftAnimation()) || player.getCurrentAnimation().equals(game.getCharacterLeftAttackAnimation()) || player.getCurrentAnimation().equals(game.getRunLeftAnimation())) {
                player.setCurrentAnimation(game.getCharacterLeftIdleAnimation());
                player.setAdjust(false);
            } else if (player.getCurrentAnimation().equals(game.getCharacterUpAnimation()) || player.getCurrentAnimation().equals(game.getcharacterUpAttackAnimation()) || player.getCurrentAnimation().equals(game.getRunUpAnimation())) {
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

    /**
     * Handles breaking walls and doors.
     *
     * @param map The TiledMap containing the breakable walls.
     */
    public void handleBreaking(TiledMap map) {
        MapLayer objectLayer = map.getLayers().get("BreakableWalls");
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("Opening");
        TiledMapTileLayer tileLayer1 = (TiledMapTileLayer) map.getLayers().get("Cracks");
        TiledMapTileLayer tileLayer2 = (TiledMapTileLayer) map.getLayers().get("Cracks2");

        Door door = colManager.checkDoorCollision(player.getCollider());
        if (door != null && player.getKeys() > 0) {
            player.setAttackingWall(true);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    player.setAttackingWall(false);
                }
            }, 0.2f);
            if (door.getDoorHealth() > 0) {
                switch (door.getDoorHealth()) {
                    case 2:
                        soundManager.playSound("mineRock1");
                        tiledMap.getLayers().get("Anim").setVisible(true);
                        screenShake.startShake(0.3f, 0.5f);
                        break;
                    case 1:
                        soundManager.playSound("mineRock2");
                        screenShake.startShake(0.3f, 0.8f);
                        break;
                }
                door.setDoorHealth(door.getDoorHealth() - 1);
            } else if (door.getDoorHealth() == 0) {
                colManager.openDoor(door);
                soundManager.playSound("mineRock3");
                for (MapObject object : objectLayer.getObjects()) {
                    if (player.collider.overlaps(((RectangleMapObject) object).getRectangle())) {
                        tiledMap.getLayers().get("Anim").setVisible(false);
                        breakTiles(object, tileLayer, 16, 16);
                        breakTiles(object, tileLayer1, 16, 16);
                        breakTiles(object, tileLayer2, 16, 16);
                    }
                    screenShake.startShake(0.4f, 3);
                }
                player.setKeys(player.getKeys() - 1);
                player.setBrokenwalls(player.getBrokenwalls() + 1);
            }
        }
    }

    /**
     * Handles breaking gems.
     *
     * @param map The TiledMap containing the breakable gems.
     */
    public void handleGemBreak(TiledMap map) {
        MapLayer objectLayer = map.getLayers().get("BreakableWalls");
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("additives");
        RectangleMapObject r = null;

        for (MapObject object : objectLayer.getObjects()) {
            if ((player.collider.overlaps(((RectangleMapObject) object).getRectangle())) &&
                    (((RectangleMapObject) object).getProperties().get("type") != null) &&
                    (((RectangleMapObject) object).getProperties().get("type")).equals("Gem")) {

                r = (RectangleMapObject) object.getProperties().get("collider");
                particleEffect2.setPosition(r.getRectangle().x + r.getRectangle().getWidth() / 2,
                        r.getRectangle().y + r.getRectangle().getHeight() / 2);
                particleEffect2.reset();
                screenShake.startShake(0.3f, 3);
                soundManager.playSound("gemBreak");

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        objectLayer.getObjects().remove(object);
                        breakTiles(object, tileLayer, 16, 16);
                        player.setGems(player.getGems() + 1);
                    }
                }, 0.15f);

                break;
            }
        }
        if (r != null) {
            map.getLayers().get("CollisionObjects").getObjects().remove(r);
            colManager.removeGem(r);
        }
    }

    /**
     * Breaks tiles in the specified layer.
     *
     * @param object    The MapObject representing the area to break.
     * @param tileLayer The TiledMapTileLayer to break tiles from.
     * @param tileWidth The width of each tile.
     * @param tileHeight The height of each tile.
     */
    public void breakTiles(MapObject object, TiledMapTileLayer tileLayer, float tileWidth, float tileHeight) {
        float objectX = (float) object.getProperties().get("x");
        float objectY = (float) object.getProperties().get("y");
        float objectWidth = (float) object.getProperties().get("width");
        float objectHeight = (float) object.getProperties().get("height");

        int tileXStart = (int) (objectX / tileWidth);
        int tileYStart = (int) (objectY / tileHeight);
        int tileXEnd = (int) ((objectX + objectWidth) / tileWidth) + 1;
        int tileYEnd = (int) ((objectY + objectHeight) / tileHeight) + 1;

        for (int x = tileXStart; x < tileXEnd; x++) {
            for (int y = tileYStart; y < tileYEnd; y++) {
                tileLayer.setCell(x, y, null);
            }
        }
    }

    /**
     * Counts the number of gems in the map.
     *
     * @return The number of gems.
     */
    public int gemCounter() {
        MapLayer objectLayer = tiledMap.getLayers().get("BreakableWalls");
        int count = 0;
        for (MapObject object : objectLayer.getObjects()) {
            if ((object.getProperties().get("type") != null) && (object.getProperties().get("type").equals("Gem"))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Sets the paused state of the game.
     *
     * @param paused The paused state to set.
     */
    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    /**
     * Handles trap explosions.
     */
    public void trapexplosion() {
        for (RectangleMapObject object : mapManager.getCollisionObjects()) {
            String objectType = object.getProperties().get("type", String.class);
            if ("Trap".equals(objectType)) {
                Rectangle objectBounds = object.getRectangle();

                if (player.newPos.overlaps(objectBounds)) {
                    soundManager.playSound("xplsv");
                    float effectX = objectBounds.x + objectBounds.width / 2;
                    float effectY = objectBounds.y + objectBounds.height / 2;
                    particleEffect.setPosition(effectX, effectY);
                    particleEffect.reset();
                    player.newPos.setX(player.getPosition().x);
                    player.newPos.setY(player.getPosition().y);
                }
            }
        }
    }

    /**
     * Resizes the game screen.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 2f, height / 2f);
    }

    /**
     * Disposes of resources used by the game screen.
     */
    @Override
    public void dispose() {
        mapRenderer.dispose();
        tiledMap.dispose();
        pauseOverlay.dispose();
        shapeRenderer.dispose();
    }

    /**
     * Clamps the camera to prevent it from going out of bounds.
     */
    private void clampCamera() {
        float halfViewportWidth = (camera.viewportWidth * camera.zoom) / 2;
        float halfViewportHeight = (camera.viewportHeight * camera.zoom) / 2;

        camera.position.x = MathUtils.clamp(camera.position.x, halfViewportWidth, 1920 - halfViewportWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, halfViewportHeight, 1080 - halfViewportHeight);
    }

    /**
     * Shows the game screen.
     */
    @Override
    public void show() {
        player.loadState("playerstate.txt");
        pauseOverlay = new PauseOverlay(this, game);
        soundManager.onGameStateChange(mainState);
    }

    /**
     * Gets the pause overlay.
     *
     * @return The pause overlay.
     */
    public PauseOverlay getPauseOverlay() {
        return pauseOverlay;
    }

    /**
     * Hides the game screen.
     */
    @Override
    public void hide() {
    }

    /**
     * Pauses the game screen.
     */
    @Override
    public void pause() {
    }

    /**
     * Resumes the game screen.
     */
    @Override
    public void resume() {
    }

    /**
     * Gets the camera.
     *
     * @return The camera.
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Gets the sound manager.
     *
     * @return The sound manager.
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * Draws an arrow pointing to the finish line.
     */
    public void drawarrow() {
        RectangleMapObject rect = null;
        for (RectangleMapObject r : colManager.getEventObjects()) {
            if ("Finish".equals(r.getProperties().get("type", String.class))) {
                rect = r;
                break;
            }
        }

        if (rect != null) {
            float playerX = player.getPosition().x;
            float playerY = player.getPosition().y;

            Rectangle finishBounds = rect.getRectangle();
            float finishX = finishBounds.x + finishBounds.width / 2;
            float finishY = finishBounds.y + finishBounds.height / 2;

            float angle = MathUtils.atan2(finishY - playerY, finishX - playerX) * MathUtils.radiansToDegrees;

            arrowSprite.setScale(0.05f);
            arrowSprite.setPosition(playerX - 143, playerY - 120);
            arrowSprite.setRotation(90 + angle);

            arrowSprite.draw(game.getSpriteBatch());
        }
    }

    /**
     * Gets the camera zoom level.
     *
     * @return The camera zoom level.
     */
    public float getCameraZoom() {
        return cameraZoom;
    }

    /**
     * Sets the camera zoom level.
     *
     * @param cameraZoom The camera zoom level to set.
     */
    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
    }
}