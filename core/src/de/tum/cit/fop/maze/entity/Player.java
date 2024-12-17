package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private TiledMap tiledMap;
    private int tileSize;

    private Rectangle collider;

    private List<TiledMapTileLayer> wallLayers;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(float x, float y, float speed, TiledMap tiledMap) {
        this.tileSize = 16;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.tiledMap = tiledMap;
        this.width = tileSize;
        this.height = tileSize*2;
        this.speed = speed;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.animationTime = 0f;
        //added in order that they are shown in the map file, not in the id order
        this.wallLayers = Arrays.asList((TiledMapTileLayer) tiledMap.getLayers().get(1), (TiledMapTileLayer) tiledMap.getLayers().get(2), (TiledMapTileLayer) tiledMap.getLayers().get(3));
    }

    public void setCurrentAnimation(Animation<TextureRegion> animation) {
        this.currentAnimation = animation;
    }

    public void update(float delta) {
        if (isMoving) {
            float newX = position.x + velocity.x * speed * delta;
            float newY = position.y + velocity.y * speed * delta;

            if (!isColliding(newX, newY)) {
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
            batch.draw(frame, position.x-(width/2), position.y-(height/2), width, height);
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

    public boolean isColliding(float newX, float newY)  {
        boolean colliding = false;
        int i = 0;
        //checks through all wall layers, (alse checks for an offset to avoid entering walls)
        while (!colliding && i < wallLayers.size())  {
            colliding = colliding || checkCollision(newX+6, newY, wallLayers.get(i));
            colliding = colliding || checkCollision(newX-6, newY, wallLayers.get(i));
            //skipping the back walls to it keeps the current overlap
            if (i != 2)
                colliding = colliding || checkCollision(newX, newY - 9, wallLayers.get(i));
            i++;
        }
        return colliding;
    }

    private boolean checkCollision(float newX, float newY, TiledMapTileLayer wallLayer) {
        TiledMapTileLayer.Cell cell = null;

        //gets the next tile xy value
        int tileX = (int) newX / tileSize;
        int tileY = (int) newY / tileSize;

        //trying to figure out how to render the bottom walls over the player

        //have a second collision check which puts the layer in as a parameter and calls this method
        //check x-5 and x+5 in the check collisions at thre same time
        //actually gets the tile
        cell = wallLayer.getCell(tileX, tileY);

        //since its checking the walls, basically if the tile is there it's a wall
        return cell != null;
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
