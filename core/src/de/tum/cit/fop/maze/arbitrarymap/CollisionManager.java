package de.tum.cit.fop.maze.arbitrarymap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class CollisionManager {
    private List<Rectangle> collisionObjects;

    public CollisionManager(List<Rectangle> collisionObjects) {
        this.collisionObjects = collisionObjects;
    }

    public boolean checkCollision(Rectangle entityBounds) {
        for (Rectangle collider : collisionObjects) {
            if (entityBounds.overlaps(collider)) {
                return true;
            }
        }
        return false;
    }

    public void resolveCollision(Rectangle entityBounds, Vector2 previousPosition) {
        for (Rectangle collider : collisionObjects) {
            if (entityBounds.overlaps(collider)) {
                entityBounds.x = previousPosition.x;
                entityBounds.y = previousPosition.y;
                return;
            }
        }
    }
}
