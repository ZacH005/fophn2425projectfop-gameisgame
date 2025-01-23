package de.tum.cit.fop.maze.arbitrarymap;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entity.Door;

import java.util.List;

public class CollisionManager {
    private List<RectangleMapObject> collisionObjects;
    private List<Door> doorObjects;
    private List<RectangleMapObject> eventObjects;
    private boolean wonLevel = false;
//    private List<Rectangle> trapObjects;

    public CollisionManager(List<RectangleMapObject> collisionObjects, List<Door> doorObjects, List<RectangleMapObject> eventObjects) {
        this.collisionObjects = collisionObjects;
        this.doorObjects = doorObjects;
        this.eventObjects = eventObjects;
//        this.trapObjects = trapObjects;
    }

    public Object checkMapCollision(Rectangle thingCollider) {
        for (RectangleMapObject collider : collisionObjects) {
            if (thingCollider.overlaps(collider.getRectangle())) {
                return collider.getProperties().get("type");
            }
        }
        return null;
    }
    public Object getMapCollider(Rectangle thingCollider) {
        for (RectangleMapObject collider : collisionObjects) {
            if (thingCollider.overlaps(collider.getRectangle())) {
                return collider;
            }
        }
        return null;
    }

    public Object checkEventCollision(Rectangle thingCollider) {
        for (RectangleMapObject collider : eventObjects) {
            if (thingCollider.overlaps(collider.getRectangle())) {
                return collider.getProperties().get("type");
            }
        }
        return null;
    }

    public Door checkDoorCollision(Rectangle thingCollider)   {
        for (Door collider : doorObjects) {
            if (thingCollider.overlaps(collider.getDetectObject().getRectangle())) {
                return collider;
            }
        }
        return null;
    }

    public void openDoor(Door doorCollider)  {
        if (!doorCollider.isOpen()) {
            doorCollider.setOpen(true);
            collisionObjects.remove(doorCollider.getColliderObject());
        }
    }
    public void  removeGem(RectangleMapObject thingCollider) {
        RectangleMapObject r =null;
        for(RectangleMapObject collider : collisionObjects) {
            if(thingCollider.equals(collider)){
                r=collider;
            }
        }
        if(r!=null){
            collisionObjects.remove(r);
        }
    }

//    private boolean checkWin()  {
//
//    }


    public boolean isWonLevel() {
        return wonLevel;
    }

    public void setWonLevel(boolean wonLevel) {
        this.wonLevel = wonLevel;
    }

    public boolean checkCollision(Rectangle mapObj, Rectangle thingCollider)    {
        return thingCollider.overlaps(mapObj);
    }

    public boolean checkListCollision(List<Rectangle> mapObjs, Rectangle thingCollider) {
        for (Rectangle collider : mapObjs) {
            if (thingCollider.overlaps(collider)) {
                return true;
            }
        }
        return false;
    }

//    public void resolveCollision(Rectangle entityBounds, Vector2 previousPosition) {
//        for (Rectangle collider : collisionObjects) {
//            if (entityBounds.overlaps(collider)) {
//                entityBounds.x = previousPosition.x;
//                entityBounds.y = previousPosition.y;
//                return;
//            }
//        }
//    }

    public List<RectangleMapObject> getCollisionObjects() {
        return collisionObjects;
    }

    public List<RectangleMapObject> getEventObjects() {
        return eventObjects;
    }
    //    public List<Rectangle> getTrapObjects() {
//        return trapObjects;
//    }
}
