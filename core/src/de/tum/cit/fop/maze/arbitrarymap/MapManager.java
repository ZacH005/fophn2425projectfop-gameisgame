package de.tum.cit.fop.maze.arbitrarymap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.HeartUp;
import de.tum.cit.fop.maze.abilities.Key;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.abilities.SpeedUp;
import de.tum.cit.fop.maze.entity.Door;
import de.tum.cit.fop.maze.entity.Enemy;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private TiledMap map;
    private List<RectangleMapObject> collisionObjects = new ArrayList<>();
    private List<Door> doorObjects = new ArrayList<>();
    private List<Powerup> powerups = new ArrayList<>();
    private List<RectangleMapObject> eventObjects = new ArrayList<>();
    private List<RectangleMapObject> enemies = new ArrayList<>();
    private SoundManager soundManager;
    //    private List<Rectangle> trapObjects = new ArrayList<>();

    public MapManager(TiledMap tiledMap, SoundManager soundManager) {
        this.map = tiledMap;
        this.soundManager = soundManager;
        loadCollisionObjects();
        loadPowerupObjects();
        loadDoorObjects();
        loadEventObjects();
        loadEnemies();

    }

    private void loadCollisionObjects() {
        MapObjects objects = map.getLayers().get("CollisionObjects").getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                collisionObjects.add(rectObj);
            } else if (object instanceof EllipseMapObject circleObj)    {

            }
        }
    }

    private void loadDoorObjects()  {
        MapObjects objects = map.getLayers().get("DoorObjects").getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                Door newDoor = new Door(rectObj);
                doorObjects.add(newDoor);
            }
        }
    }

    private void loadEventObjects() {
        MapObjects objects = map.getLayers().get("EventObjects").getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                eventObjects.add(rectObj);
            }
        }
    }

    private void loadEnemies()  {
        MapObjects objects = map.getLayers().get("EnemyObjects").getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                enemies.add(rectObj);
            }
        }
    }



//    private void loadTrapObjects()  {
//        MapObjects objects = map.getLayers().get("TrapObjects").getObjects();
//
//        for (MapObject object : objects)    {
//            if (object instanceof RectangleMapObject rectObj)   {
//                trapObjects.add(rectObj.getRectangle());
//            }
//        }
//    }

    private void loadPowerupObjects() {
        MapObjects objects = map.getLayers().get("PowerUpObjects").getObjects();

        for (MapObject object : objects) {
//            System.out.println(object);
            if (object instanceof RectangleMapObject rectObj) {
                Rectangle rectangle = rectObj.getRectangle();
                Powerup powerup = null;

                String type = object.getProperties().get("type", String.class);
//                System.out.println("Adding powerup");

                if ("SpeedUp".equals(type)) {
                    powerup = new SpeedUp(rectangle.x, rectangle.y,this.soundManager);
                } else if ("HeartUp".equals(type)) {
                    powerup = new HeartUp(rectangle.x, rectangle.y,this.soundManager);
                }
                 else if("Key".equals(type)) {
                     powerup = new Key(rectangle.x, rectangle.y,this.soundManager);
                }

                if (powerup != null) {
                    powerups.add(powerup);
                }
            }
        }
    }

    public List<Powerup> getPowerups() {
        return powerups;
    }

    public List<RectangleMapObject> getCollisionObjects() {
        return collisionObjects;
    }

//    public List<Rectangle> getTrapObjects() {
//        return trapObjects;
//    }

    public TiledMap getMap() {
        return map;
    }

    public List<RectangleMapObject> getEnemies() {
        return enemies;
    }

    public List<Door> getDoorObjects()    {
        return doorObjects;
    }

    public List<RectangleMapObject> getEventObjects() {
        return eventObjects;
    }



}

