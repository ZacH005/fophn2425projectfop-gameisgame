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

public class Player implements Entity {

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

    public Rectangle collider;

    private List<TiledMapTileLayer> collidable;
    ///Entitiy's variables
    private Vector2 position;
    private int health;
    private int armor;
    private List<String> powerups;
    private int money;


    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(float x, float y, float speed, TiledMap tiledMap, int health,int armor, List<String> powerups, int money) {
        this.tileSize = 16;
        this.velocity = new Vector2(0, 0);
        this.tiledMap = tiledMap;
        this.width = tileSize;
        this.height = tileSize;
        this.speed = speed;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.animationTime = 0f;
        //added in order that they are shown in the map file, not in the id order
        this.collidable = Arrays.asList((TiledMapTileLayer) tiledMap.getLayers().get(1), (TiledMapTileLayer) tiledMap.getLayers().get(2), (TiledMapTileLayer) tiledMap.getLayers().get(3));

        ///Entities variables
        this.position = new Vector2(x, y);
        this.health = health;
        this.armor = armor;
        this.money = money;
        this.powerups = new ArrayList<>();
        this.collider = new Rectangle(x, y, width, height);
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
                collider.x = newX;
                collider.y = newY;
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
        while (!colliding && i < collidable.size())  {
            colliding = colliding || checkCollision(newX+5, newY, collidable.get(i)) || checkCollision(newX-5, newY, collidable.get(i));
            //colliding = colliding || checkCollision(newX-6, newY, collidable.get(i));
            //skipping the back walls to it keeps the current overlap
            if (i != 2)
                colliding = colliding || checkCollision(newX, newY - 7, collidable.get(i));
            i++;
        }
        return colliding;
    }

    private boolean checkCollision(float newX, float newY, TiledMapTileLayer wallLayer) {
        TiledMapTileLayer.Cell cell = null;

        //gets the next tile xy value
        int tileX = (int) newX / tileSize;
        int tileY = (int) newY / tileSize;

        //actually gets the tile
        cell = wallLayer.getCell(tileX, tileY);

        //since its checking the walls, basically if the tile is there it's a wall
        return cell != null;
    }
/// Entity's methods


    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    @Override
    public int getArmor() {
        return armor;
    }

    @Override
    public void setArmor(int armor) {
        this.armor = armor;
    }

    @Override
    public List<String> getPowerUps() {
        return powerups;
    }

    @Override
    public void setPowerUps(List<String> powerUps) {
        this.powerups = powerUps;
    }

    @Override
    public int getMoney() {
        return money;
    }

    @Override
    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public void saveState(String filename) {
        EntityUtils.saveToFile(this, filename);
    }

    @Override
    public void loadState(String filename) {
        Entity loaded = EntityUtils.loadFromFile(filename,this);
        if (loaded instanceof Player loadedPlayer) {
            this.health = loadedPlayer.health;
            this.position = loadedPlayer.position;
            this.armor = loadedPlayer.armor;
            this.money = loadedPlayer.money;
            this.powerups = loadedPlayer.powerups;

        }
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

}
