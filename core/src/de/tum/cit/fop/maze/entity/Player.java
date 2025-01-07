package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.abilities.Collectable;
import de.tum.cit.fop.maze.abilities.Item;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player implements Entity, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


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
    public final Vector2 resetpos;
    private int health;
    private int armor;
    private List<Powerup> powerups;
    private int money;
    private int maxHealth;
    private List<Item> hasEquipped;
    private  int keys=0;

    private float flickerAlpha = 1.0f;  // Initialize alpha to full visibility
    private boolean isFlickering = false;  // Track if the player is flickering
    private float flickerTime = 0f;  // Timer for a single flickering effect
    private static final float FLICKER_DURATION = 0.1f;  // Duration for each flicker step
    private float flickertotaltime = 0f; //Timer to keep track of time for whole flicker animation
    private float totalflickerduration; //Total animation duration

    private Vector2 lastValidPosition;

    private boolean isSprinting;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(float x, float y, TiledMap tiledMap, int health,int armor, List<String> powerups, int money) {
        this.tileSize = 16;
        this.velocity = new Vector2(0, 0);
        this.tiledMap = tiledMap;
        this.width = tileSize;
        this.height = tileSize;
        this.speed = 150;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.animationTime = 0f;
        //added in order that they are shown in the map file, not in the id order
//        this.collidable = Arrays.asList((TiledMapTileLayer) tiledMap.getLayers().get(1), (TiledMapTileLayer) tiledMap.getLayers().get(2), (TiledMapTileLayer) tiledMap.getLayers().get(3));

        this.isSprinting = false;

        ///Entities variables
        this.position = new Vector2(x, y);
        this.resetpos = new Vector2(50, 50);
        this.health = health;
        this.armor = armor;
        this.money = money;
        this.powerups = new ArrayList<>();
        this.collider = new Rectangle(position.x-8, position.y-8, width, height);
        this.maxHealth = 7;
        this.hasEquipped = new ArrayList<>();

        this.lastValidPosition = new Vector2(x, y);
    }
    public void startFlickering(float time) {
        isFlickering = true;
        flickerTime = 0f;
        totalflickerduration = time;
    }

    public void stopFlickering() {
        isFlickering = false;
        flickerAlpha = 1.0f;  // Reset alpha to normal
    }

    public void updateFlickerEffect(float delta) {
        if (isFlickering) {
            flickertotaltime += delta;
            flickerTime += delta;
            //stops flickering when time is greater than total animation duration (Ik the variable names could be confusing sorry.)
            if(flickertotaltime>totalflickerduration){
                stopFlickering();
                isFlickering = false;
                flickertotaltime=0f;
            }
            //basically toggles the alphe value that each flicker duration the value swaps from 0 to 1 or from 1 to 0 which is then used as the opacity for the spritebatch
            if (flickerTime >= FLICKER_DURATION) {
                // Toggle alpha between 1.0 (visible) and 0.0 (invisible)
                flickerAlpha = (flickerAlpha == 1.0f) ? 0.0f : 1.0f;
                flickerTime = 0f;  // Reset flicker time
            }
        }
    }

    public void setCurrentAnimation(Animation<TextureRegion> animation) {
        this.currentAnimation = animation;
    }

    public void update(float delta, CollisionManager colManager) {
        updateFlickerEffect(delta);
        if (isMoving) {
            float newX = position.x + velocity.x * speed * delta;
            float newY = position.y + velocity.y * speed * delta;
            Rectangle newPos = new Rectangle(newX-7, newY-7, width-2, height-2);

            if (!colManager.checkCollision(newPos)) {
                lastValidPosition.set(position.x, position.y);
                position.x = newX;
                position.y = newY;
                collider.x = newX-8;
                collider.y = newY-8;
            }

            //make a set of all x, y coordinates of walls. check if the set of walls includes those x, y coordinates. set player position to current position

            animationTime += delta;
        } else {
            animationTime = 0;
        }
    }


    public void render(SpriteBatch batch) {
        if (currentAnimation != null) {
            if (isFlickering) {
                batch.setColor(1,1,1,flickerAlpha);
            } else {
                batch.setColor(1,1,1,1);
            }
            TextureRegion frame = currentAnimation.getKeyFrame(animationTime, true);
            batch.draw(frame, position.x-(width/2), position.y-(height/2), width, height);
            batch.setColor(1, 1, 1, 1);
        }

    }
    public void respawn(){
        setPosition(new Vector2(50,50));
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

    public void setSpeed(float speed) {
        this.speed = speed;
    }

//    public boolean isColliding(float newX, float newY)  {
//        boolean colliding = false;
//        int i = 0;
//        //checks through all wall layers, (alse checks for an offset to avoid entering walls)
//        while (!colliding && i < collidable.size())  {
//            colliding = colliding || checkCollision(newX+5, newY, collidable.get(i)) || checkCollision(newX-5, newY, collidable.get(i));
//            //colliding = colliding || checkCollision(newX-6, newY, collidable.get(i));
//            //skipping the back walls to it keeps the current overlap
//            if (i != 2)
//                colliding = colliding || checkCollision(newX, newY - 7, collidable.get(i));
//            i++;
//        }
//        return colliding;
//    }

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

    ///Items
    public void equipItem(Item item) {
        hasEquipped.add(item);
    }

    public void unequipItem(Item item)  {
        hasEquipped.remove(item);
    }

    public List<Item> getHasEquipped() {
        return hasEquipped;
    }

    /// Entity's methods

    @Override
    public void heal() {
        if (health < maxHealth)
            health += 1;
    }

    @Override
    public void takeDamage() {
        health -= 1;
        System.out.println(health);
        Sound hurtSFX = Gdx.audio.newSound(Gdx.files.internal("music/hitHurt.wav"));
        hurtSFX.play();
    }

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
        collider.x = position.x-8;
        collider.y = position.y-8;
    }

    @Override
    public boolean isFollowing() {
        return false;
    }

    @Override
    public void setFollowing(boolean following) {

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
    public List<Powerup> getPowerUps() {
        return powerups;
    }

    @Override
    public void setPowerUps(List<Powerup> powerUps) {
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
            //no longer need to save positions if were just always starting player at map start point (maybe need to for checkpoints)
//            this.position = loadedPlayer.position;
            this.armor = loadedPlayer.armor;
            this.money = loadedPlayer.money;
            //doesn't need to get the powerups since they're temporary and only found mid-level
//            loadedPlayer.getPowerUps().forEach(powerup -> {
//                powerup.initializeTransientFields(loadedPlayer);
//
//            });
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public float getSpeed() {
        return speed;
    }


    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        isSprinting = sprinting;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Rectangle getCollider() {
        return collider;
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }
}
