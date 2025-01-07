package de.tum.cit.fop.maze.arbitrarymap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.abilities.HeartUp;
import de.tum.cit.fop.maze.abilities.Key;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.abilities.SpeedUp;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private TiledMap map;
    private List<Rectangle> collisionObjects = new ArrayList<>();
    private List<Powerup> powerups = new ArrayList<>();

    public MapManager(TiledMap tiledMap) {
        this.map = tiledMap;

        loadCollisionObjects();
        loadPowerupObjects();
    }

    private void loadCollisionObjects() {
        MapObjects objects = map.getLayers().get("CollisionObjects").getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                collisionObjects.add(rectObj.getRectangle());
            }
        }
    }

    private void loadPowerupObjects() {
        MapObjects objects = map.getLayers().get("PowerUpObjects").getObjects();

        for (MapObject object : objects) {
//            if (object.getProperties().containsKey("X") && object.getProperties().containsKey("X")) {
//                float x = object.getProperties().get("X", Float.class);
//                float y = object.getProperties().get("X", Float.class);
//                String type = object.getProperties().get("type", String.class);
//
//                System.out.println("adding powerup" + object.getProperties());
//
//                Powerup powerup = null;
//
//                if ("SpeedUp".equals(type)) {
//                    powerup = new SpeedUp(x, y);
//                    System.out.println("added speedup");
//                } else if ("HeartUp".equals(type)) {
//                    powerup = new HeartUp(x, y);
//                }
//
//                if (powerup != null) {
//                    powerups.add(powerup);
//                }
//            }

            if (object instanceof RectangleMapObject rectObj) {
                Rectangle rectangle = rectObj.getRectangle();
                Powerup powerup = null;

                String type = object.getProperties().get("type", String.class);
                System.out.println("Adding powerup");

                if ("SpeedUp".equals(type)) {
                    powerup = new SpeedUp(rectangle.x, rectangle.y);
                } else if ("HeartUp".equals(type)) {
                    powerup = new HeartUp(rectangle.x, rectangle.y);
                }
                 else if("Key".equals(type)) {
                     powerup = new Key(rectangle.x, rectangle.y);
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

    public List<Rectangle> getCollisionObjects() {
        return collisionObjects;
    }

    public TiledMap getMap() {
        return map;
    }
}

