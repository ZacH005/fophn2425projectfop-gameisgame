package de.tum.cit.fop.maze.arbitrarymap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.HeartUp;
import de.tum.cit.fop.maze.abilities.Key;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.abilities.SpeedUp;
import de.tum.cit.fop.maze.entity.Door;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the map and its objects, such as collision objects, doors, power-ups, and enemies.
 */
public class MapManager {
    private TiledMap map;
    private List<RectangleMapObject> collisionObjects = new ArrayList<>();
    private List<Door> doorObjects = new ArrayList<>();
    private List<Powerup> powerups = new ArrayList<>();
    private List<RectangleMapObject> eventObjects = new ArrayList<>();
    private List<RectangleMapObject> enemies = new ArrayList<>();
    private SoundManager soundManager;

    /**
     * Constructs a MapManager and loads objects from the provided TiledMap.
     *
     * @param tiledMap     The tiled map containing game objects.
     * @param soundManager The sound manager for handling sound effects.
     */
    public MapManager(TiledMap tiledMap, SoundManager soundManager) {
        this.map = tiledMap;
        this.soundManager = soundManager;
        loadCollisionObjects();
        loadPowerupObjects();
        loadDoorObjects();
        loadEventObjects();
        loadEnemies();
    }

    /**
     * Loads collision objects from the map layer.
     */
    private void loadCollisionObjects() {
        MapObjects objects = map.getLayers().get("CollisionObjects").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                collisionObjects.add(rectObj);
            }
        }
    }

    /**
     * Loads door objects from the map layer.
     */
    private void loadDoorObjects() {
        MapObjects objects = map.getLayers().get("DoorObjects").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                doorObjects.add(new Door(rectObj));
            }
        }
    }

    /**
     * Loads event objects from the map layer.
     */
    private void loadEventObjects() {
        MapObjects objects = map.getLayers().get("EventObjects").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                eventObjects.add(rectObj);
            }
        }
    }

    /**
     * Loads enemy objects from the map layer.
     */
    private void loadEnemies() {
        MapObjects objects = map.getLayers().get("EnemyObjects").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                enemies.add(rectObj);
            }
        }
    }

    /**
     * Loads power-up objects from the map layer and assigns them appropriate types.
     */
    private void loadPowerupObjects() {
        MapObjects objects = map.getLayers().get("PowerUpObjects").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                Rectangle rectangle = rectObj.getRectangle();
                Powerup powerup = null;
                String type = object.getProperties().get("type", String.class);
                if ("SpeedUp".equals(type)) {
                    powerup = new SpeedUp(rectangle.x, rectangle.y, this.soundManager);
                } else if ("HeartUp".equals(type)) {
                    powerup = new HeartUp(rectangle.x, rectangle.y, this.soundManager);
                } else if ("Key".equals(type)) {
                    powerup = new Key(rectangle.x, rectangle.y, this.soundManager);
                }
                if (powerup != null) {
                    powerups.add(powerup);
                }
            }
        }
    }

    /**
     * Returns the list of power-ups in the map.
     *
     * @return A list of power-ups.
     */
    public List<Powerup> getPowerups() {
        return powerups;
    }

    /**
     * Returns the list of collision objects in the map.
     *
     * @return A list of collision objects.
     */
    public List<RectangleMapObject> getCollisionObjects() {
        return collisionObjects;
    }

    /**
     * Returns the tiled map associated with this manager.
     *
     * @return The tiled map.
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Returns the list of enemy objects in the map.
     *
     * @return A list of enemy objects.
     */
    public List<RectangleMapObject> getEnemies() {
        return enemies;
    }

    /**
     * Returns the list of door objects in the map.
     *
     * @return A list of door objects.
     */
    public List<Door> getDoorObjects() {
        return doorObjects;
    }

    /**
     * Returns the list of event objects in the map.
     *
     * @return A list of event objects.
     */
    public List<RectangleMapObject> getEventObjects() {
        return eventObjects;
    }
}
