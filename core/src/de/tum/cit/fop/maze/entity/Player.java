package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Player {

    private Vector2 position;
    private Vector2 velocity;
    private float speed;
    private boolean isMoving;

    private Animation<TextureRegion> currentAnimation;
    private float animationTime;

    private Direction direction;

    private float width;
    private float height;

    private Rectangle collider;


    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(float x, float y, float width, float height, float speed) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.animationTime = 0f;
    }

    public void setCurrentAnimation(Animation<TextureRegion> animation) {
        this.currentAnimation = animation;
    }

    public void update(float delta, BoundingBox mapBounds) {
        if (isMoving) {
            float newX = position.x + velocity.x * speed * delta;
            float newY = position.y + velocity.y * speed * delta;

            if (mapBounds.contains(new Vector3(newX, newY, 0))) {
                position.x = newX;
                position.y = newY;
            }



            //make a set of all x, y coordinates of walls. check if the set of walls includes those x, y coordinates. set player position to current position

            animationTime += delta;
        } else {
            animationTime = 0;
        }
    }


    public void render(SpriteBatch batch) {
        if (currentAnimation != null) {
            TextureRegion frame = currentAnimation.getKeyFrame(animationTime, true);
            batch.draw(frame, position.x, position.y, width, height);
        }
    }

    public void move(Direction direction) {
        this.direction = direction;
        this.isMoving = true;

        switch (direction) {
            case UP:
                velocity.set(0, 1);
                break;
            case DOWN:
                velocity.set(0, -1);
                break;
            case LEFT:
                velocity.set(-1, 0);
                break;
            case RIGHT:
                velocity.set(1, 0);
                break;
        }
    }

    public void stop() {
        isMoving = false;
        velocity.set(0, 0);
    }

//    public boolean isCollided(Rectangle rect) {
//        Gdx.app.log("Collision Detected", "" + collider.overlaps(rect));
//        return rect.overlaps(collider);
//    }

    public Vector2 getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isMoving() {
        return isMoving;
    }
}
