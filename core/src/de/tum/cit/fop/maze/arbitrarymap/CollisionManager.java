package de.tum.cit.fop.maze.arbitrarymap;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.entity.Door;

import java.util.List;

/**
 * Manages collision detection and handling within the game.
 */
public class CollisionManager {
    private List<RectangleMapObject> collisionObjects;
    private List<Door> doorObjects;
    private List<RectangleMapObject> eventObjects;
    private boolean wonLevel = false;

    /**
     * Constructs a CollisionManager with specified collision, door, and event objects.
     *
     * @param collisionObjects List of map collision objects.
     * @param doorObjects      List of doors in the map.
     * @param eventObjects     List of event-triggering objects.
     */
    public CollisionManager(List<RectangleMapObject> collisionObjects, List<Door> doorObjects, List<RectangleMapObject> eventObjects) {
        this.collisionObjects = collisionObjects;
        this.doorObjects = doorObjects;
        this.eventObjects = eventObjects;
    }

    /**
     * Checks if a given collider collides with any map object and returns its type.
     *
     * @param thingCollider The collider to check.
     * @return The type of the collided object, or null if no collision occurs.
     */
    public Object checkMapCollision(Rectangle thingCollider) {
        for (RectangleMapObject collider : collisionObjects) {
            if (thingCollider.overlaps(collider.getRectangle())) {
                return collider.getProperties().get("type");
            }
        }
        return null;
    }

    /**
     * Retrieves the specific map collider that a given collider overlaps with.
     *
     * @param thingCollider The collider to check.
     * @return The collided map object, or null if no collision occurs.
     */
    public Object getMapCollider(Rectangle thingCollider) {
        for (RectangleMapObject collider : collisionObjects) {
            if (thingCollider.overlaps(collider.getRectangle())) {
                return collider;
            }
        }
        return null;
    }

    /**
     * Checks if a given collider collides with any event object and returns its type.
     *
     * @param thingCollider The collider to check.
     * @return The type of the event object, or null if no collision occurs.
     */
    public Object checkEventCollision(Rectangle thingCollider) {
        for (RectangleMapObject collider : eventObjects) {
            if (thingCollider.overlaps(collider.getRectangle())) {
                return collider.getProperties().get("type");
            }
        }
        return null;
    }

    /**
     * Checks if a given collider collides with any door object.
     *
     * @param thingCollider The collider to check.
     * @return The door object if a collision occurs, otherwise null.
     */
    public Door checkDoorCollision(Rectangle thingCollider) {
        for (Door collider : doorObjects) {
            if (thingCollider.overlaps(collider.getDetectObject().getRectangle())) {
                return collider;
            }
        }
        return null;
    }

    /**
     * Opens a given door by removing its collider from the collision list.
     *
     * @param doorCollider The door to open.
     */
    public void openDoor(Door doorCollider) {
        if (!doorCollider.isOpen()) {
            doorCollider.setOpen(true);
            collisionObjects.remove(doorCollider.getColliderObject());
        }
    }

    /**
     * Removes a specified gem from the collision objects list.
     *
     * @param thingCollider The gem to remove.
     */
    public void removeGem(RectangleMapObject thingCollider) {
        RectangleMapObject r = null;
        for (RectangleMapObject collider : collisionObjects) {
            if (thingCollider.equals(collider)) {
                r = collider;
            }
        }
        if (r != null) {
            collisionObjects.remove(r);
        }
    }

    /**
     * Checks if two rectangles overlap.
     *
     * @param mapObj        The map object.
     * @param thingCollider The collider to check.
     * @return True if they overlap, false otherwise.
     */
    public boolean checkCollision(Rectangle mapObj, Rectangle thingCollider) {
        return thingCollider.overlaps(mapObj);
    }

    /**
     * Checks if a given collider collides with any object in a list of map objects.
     *
     * @param mapObjs       The list of map objects.
     * @param thingCollider The collider to check.
     * @return True if a collision occurs, false otherwise.
     */
    public boolean checkListCollision(List<Rectangle> mapObjs, Rectangle thingCollider) {
        for (Rectangle collider : mapObjs) {
            if (thingCollider.overlaps(collider)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the list of collision objects.
     *
     * @return The list of collision objects.
     */
    public List<RectangleMapObject> getCollisionObjects() {
        return collisionObjects;
    }

    /**
     * Gets the list of event objects.
     *
     * @return The list of event objects.
     */
    public List<RectangleMapObject> getEventObjects() {
        return eventObjects;
    }

    /**
     * Checks if the level is won.
     *
     * @return True if the level is won, false otherwise.
     */
    public boolean isWonLevel() {
        return wonLevel;
    }

    /**
     * Sets the level as won or not.
     *
     * @param wonLevel True if the level is won, false otherwise.
     */
    public void setWonLevel(boolean wonLevel) {
        this.wonLevel = wonLevel;
    }
}
