package de.tum.cit.fop.maze.arbitrarymap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private TiledMap map;
    private List<Rectangle> collisionObjects = new ArrayList<>();

    public MapManager(TiledMap tiledMap) {
        this.map = tiledMap;

        loadCollisionObjects();
    }

    private void loadCollisionObjects() {
        MapObjects objects = map.getLayers().get("CollisionObjects").getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObj) {
                collisionObjects.add(rectObj.getRectangle());
            }
        }
    }

    public List<Rectangle> getCollisionObjects() {
        return collisionObjects;
    }

    public TiledMap getMap() {
        return map;
    }
}

