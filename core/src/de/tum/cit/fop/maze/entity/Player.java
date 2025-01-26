package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.Collectable;
import de.tum.cit.fop.maze.abilities.Item;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Player implements Entity, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int maxGems=0;

    ///footstep sfx
    private int currentTileX;
    private int currentTileY;
    private float footstepTimer = 0f;
    private boolean adjust = false;
    private static final float FOOTSTEP_INTERVAL = 0.2f;// 300 ms between footsteps
    private boolean isAttackingWall=false;

    private boolean isKnockedBack = false;
    private float knockbackTime = 0f;
    private static final float KNOCKBACK_DURATION = 0.1f; // Duration of knockback in seconds
    private Vector2 knockbackVelocity = new Vector2();

    private Vector2 velocity;
    private float speed;
    private boolean isMoving;

    private Animation<TextureRegion> currentAnimation;
    private float animationTime;

    private Direction direction;
    public boolean isAttack=false;

    private float width;
    private float height;

    private TiledMap tiledMap;
    private int tileSize;
    private int gems;

    public boolean isRedEffectActive = false;
    public float redEffectTime = 0f;
    public static final float RED_EFFECT_DURATION = 0.2f;

    public Rectangle collider;

    private List<TiledMapTileLayer> collidable;
    ///Entitiy's variables
    private Vector2 position;
    public final Vector2 resetpos;
    private float health;
    private int armor;
    private List<Powerup> powerups;
    private int money;
    private int maxHealth;
    private List<Item> hasEquipped;
    private  int keys=0;
    public Rectangle newPos;

    private float flickerAlpha = 1.0f;  // Initialize alpha to full visibility
    private boolean isFlickering = false;  // Track if the player is flickering
    private float flickerTime = 0f;  // Timer for a single flickering effect
    private static final float FLICKER_DURATION = 0.1f;  // Duration for each flicker step
    private float flickertotaltime = 0f; //Timer to keep track of time for whole flicker animation
    private float totalflickerduration; //Total animation duration

    private Vector2 lastValidPosition;

    private boolean isSprinting;

    private SoundManager soundManager;

//    private Weapon weapon;

    private OrthographicCamera camera;

    private Animation<TextureRegion> swipeAnimation;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private final Vector2 startPos;

    public Player(float x, float y, TiledMap tiledMap, float health, int armor, List<String> powerups, int money, SoundManager soundManager, OrthographicCamera camera) {
        System.out.println("initilizing player");
        this.tileSize = 16;
        this.velocity = new Vector2(0, 0);
        this.tiledMap = tiledMap;
        this.width = tileSize;
        this.height = tileSize;
        this.speed = 150;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.animationTime = 0f;
        this.newPos = new Rectangle(x,y,width,height);
        //added in order that they are shown in the map file, not in the id order
//        this.collidable = Arrays.asList((TiledMapTileLayer) tiledMap.getLayers().get(1), (TiledMapTileLayer) tiledMap.getLayers().get(2), (TiledMapTileLayer) tiledMap.getLayers().get(3));

        this.isSprinting = false;

        /// sound manager
        this.soundManager = soundManager;

        ///Entities variables
        this.gems=0;
        this.startPos = new Vector2(x, y);
        this.position = startPos;
        this.resetpos = new Vector2(25, 25);
        this.health = health;
        this.armor = armor;
        this.money = money;
        this.powerups = new ArrayList<>();
        this.collider = new Rectangle(position.x-tileSize/2+5, position.y-tileSize/2, width, height);
        this.maxHealth = 7;
        this.hasEquipped = new ArrayList<>();

        this.lastValidPosition = new Vector2(x, y);
        this.currentTileX = (int) position.x / tileSize;
        this.currentTileY = (int) position.y / tileSize;

//        this.weapon = new Weapon("icons/realPickaxe.png", 50);

        this.camera = camera;

        loadWeaponAnimation();
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
    public void attack(List<Enemy> enemies) {
        attackHitbox = getAttackHitbox();

        for (Enemy enemy : enemies) {
//            if (weapon.isInSector(enemy.getPosition(), position)) {
//                enemy.takedamage(1); // Adjust damage as needed
//            }

            if (attackHitbox.overlaps(enemy.damageCollider))    {
//                System.out.println("attacking" + enemy);
//                System.out.println(enemy.getHealth());
                enemy.takeDamage(1f);

            }
        }
        isAttack = true;
    }

    // Add this method to your Player class
    public Rectangle getAttackHitbox() {
        float width = 30;
        float height = 48;

        float x = position.x;
        float y = position.y;

        switch (direction) {
            case LEFT:
                x -= this.width + 8;
                y -= 40/2f;
                break;
            case RIGHT:
                x += this.width - 8;
                y -= 40/2f;
                break;
            case UP:
//                y += 16f;
                x -= 16f;

                width = 48;
                height = 30;
                break;
            case DOWN:
                y -= 48/2f;
                x -= 16f;
                width = 48;
                height = 30;
                break;
        }

        return new Rectangle(x, y, width, height);
    }

    private Rectangle attackHitbox;

    public void loadWeaponAnimation()    {
        Texture swipeSheet = new Texture(Gdx.files.internal("animations/player/pickaxeSwipe_dark2 copy.png"));
        int frameWidth = 57, frameHeight = 32, swipeAnimationFrames = 6, y = 0;

        Array<TextureRegion> swipeFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < swipeAnimationFrames; col++) {
            swipeFrames.add(new TextureRegion(swipeSheet, col*(frameWidth), y, frameWidth, frameHeight));
        }

        this.swipeAnimation = new Animation<>(0.05f, swipeFrames);
    }


//    public void attack(List<Enemy> enemies){
//        for (Enemy enemy : enemies) {
//            if (weapon.getAttackArea().overlaps(enemy.getDamageCollider())) {
//                System.out.println("attacked");
//                enemy.takedamage(1); // Adjust damage value
//            }
//        }
////        isAttack=true;
////        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
////        scheduler.schedule(() -> {
////            isAttack = false;
////            enemy.takedamage(1);
////            adjust=false;
////            System.out.println("attacked");
////            scheduler.shutdown(); // Shut down the scheduler
////        }, 400, TimeUnit.MILLISECONDS);
//    }

    public void applyKnockback(Vector2 enemyPosition, float force) {
        // Calculate the direction of the knockback (away from the enemy)
        Vector2 knockbackDirection = new Vector2(position.x - enemyPosition.x, position.y - enemyPosition.y).nor();
        knockbackVelocity.set(knockbackDirection.scl(force));
        isKnockedBack = true;
        knockbackTime = 0f;
    }


    public void updateFlickerEffect(float delta) {
        if (isFlickering) {
            stop();
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

    boolean trapped = false;

    public void update(float delta, CollisionManager colManager) {
        updateFlickerEffect(delta);
//        System.out.println(direction);

        if (isRedEffectActive) {
            redEffectTime += delta;
            if (redEffectTime >= RED_EFFECT_DURATION) {
                isRedEffectActive = false;
            }
        }

        footstepTimer += delta; // Update the timer

        animationTime += delta;

        if (isAttack)
            attackAnimationTime += delta;

        if (isKnockedBack) {
            knockbackTime += delta;
            if (knockbackTime < KNOCKBACK_DURATION) {
                // Apply knockback velocity
                float newX = position.x + knockbackVelocity.x * delta;
                float newY = position.y + knockbackVelocity.y * delta;
                newPos = new Rectangle(newX, newY - 7, width - 2, height - 2);

                if (colManager.checkMapCollision(newPos) == null) {
                    position.x = newX;
                    position.y = newY;
                    collider.x = newX;
                    collider.y = newY - 8;
                }
            } else {
                // End knockback effect
                isKnockedBack = false;
                knockbackVelocity.set(0, 0);
            }
        } else {
            if (isMoving) {
                float newX = position.x + velocity.x * speed * delta;
                float newY = position.y + velocity.y * speed * delta;
                newPos = new Rectangle(newX, newY - 7, width - 2, height - 2);

            //door check
//            Door door = colManager.checkDoorCollision(newPos);
//            if (door != null && keys > 0)    {
//                colManager.openDoor(door);
//                soundManager.playSound("mcOpenNormalDoor_sfx");
//                door.setCurrentTexture(door.getOpenTexture());
//                keys -= 1;
//            }

                if (colManager.checkEventCollision(newPos) != null && colManager.checkEventCollision(newPos).equals("Finish")&& gems==maxGems) {
                    colManager.setWonLevel(true);
                    System.out.println(colManager.isWonLevel());
                }

                if (colManager.checkMapCollision(newPos) == null) {
                    position.x = newX;
                    position.y = newY;
                    collider.x = newX;
                    collider.y = newY - 8;

                    // Detect tile crossing so you can play footstep sound at the right time
                    int newTileX = (int) newX / tileSize;
                    int newTileY = (int) newY / tileSize;

                    if ((newTileX != currentTileX || newTileY != currentTileY) && footstepTimer >= FOOTSTEP_INTERVAL) {
                        currentTileX = newTileX;
                        currentTileY = newTileY;

                        // Play footstep sound and reset timer
                        soundManager.playSound("mcFootstep_sfx");
                        footstepTimer = 0f;
                    }
                } else if (colManager.checkMapCollision(newPos).equals("Trap")) {
                    RectangleMapObject r=(RectangleMapObject) colManager.getMapCollider(newPos);
                    redEffectTime=0f;
                    isRedEffectActive=true;
                    takeDamage(0.25f);
                    applyKnockback(new Vector2((r.getRectangle().x+(r.getRectangle().getWidth()/2)),(r.getRectangle().y+(r.getRectangle().getHeight()/2))),1200);
                    startFlickering(2f);
                }

//                System.out.println(colManager.checkMapCollision(newPos));

//            } else if (colManager.checkListCollision(colManager.getTrapObjects(), collider) && !trapped)   {
//                trapped = true;
//                respawn();
//                takeDamage();
//                startFlickering(2f);
//            }

//            animationTime += delta;
            } else {
//            animationTime = 0;
            }
        }

        Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
//        weapon.update(position, mousePosition, camera);
    }


    public void render(SpriteBatch batch) {
//        System.out.println(startPos);
        if (isAttack) {
            TextureRegion swipeFrame = swipeAnimation.getKeyFrame(attackAnimationTime, false);

            float swipeWidth = attackHitbox.width, swipeHeight = attackHitbox.height, swipeRotation = 0f, swipeY = attackHitbox.y, swipeX = attackHitbox.x;

            if (direction == Direction.UP)  {
                if (!swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
            }
            if (direction == Direction.DOWN)  {
                if (swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
            }
            if (direction == Direction.RIGHT)  {
                if (swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
                swipeHeight = attackHitbox.width;
                swipeWidth = attackHitbox.height;
                swipeRotation = 90f;
                swipeX +=32;
            }
            if (direction == Direction.LEFT)  {
                if (!swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
                swipeHeight = attackHitbox.width;
                swipeWidth = attackHitbox.height;
                swipeRotation = 90f;
                swipeX +=28;
            }
            if (isAttackingWall){
                batch.setColor(1f,1f,0f,1f);
            }
            batch.draw(swipeFrame, swipeX, swipeY, 0f, 0f, swipeWidth, swipeHeight, 1f, 1f, swipeRotation);
            if (isAttackingWall){
                batch.setColor(1f,1f,1f,1f);
            }
        } else {
            attackAnimationTime = 0;
        }

//        System.out.println(health);
        if (currentAnimation != null) {

            TextureRegion frame = currentAnimation.getKeyFrame(animationTime, true);

            if (isFlickering) {
                batch.setColor(1, 1, 1, flickerAlpha); // Flicker effect
            } else if (isRedEffectActive) {
                batch.setColor(0.7f, 0, 0, 1); // Red tint for damage effect
            } else {
                batch.setColor(1, 1, 1, 1); // Normal color
            }

            if (isAttack)
                frame = currentAnimation.getKeyFrame(attackAnimationTime, false);
            else
                frame = currentAnimation.getKeyFrame(animationTime, true);

            if(adjust) {
                batch.draw(frame, position.x - (width / 2) - 2.5f + 4f, position.y - (height / 2), width * 2.0f, height * 2.0f);
            }else{
                batch.draw(frame, position.x-(width/2)-2.5f, position.y-(height/2), width*2.0f, height*2.0f);
            }

            batch.setColor(1, 1, 1, 1);
        }
    }
    private float attackAnimationTime;

    public void respawn(){
        setPosition(new Vector2(startPos));
        trapped = false;
    }

    public void setAdjust(boolean adjust) {
        this.adjust = adjust;
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

    public Vector2 getVelocity() {
        return velocity;
    }

    /// Entity's methods

    @Override
    public void heal() {
        if (health < maxHealth)
            health += 1;
    }

    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    @Override
    public void takeDamage(float x) {
        health -= x;
        System.out.println(health);
        soundManager.playSound("playerHurt");
    }

    @Override
    public float getHealth() {
        return health;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    @Override
    public void setHealth(float health) {
        this.health = health;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position = position;
        collider.x = position.x;
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

//    public Weapon getWeapon() {
//        return weapon;
//    }
//
//    public void setWeapon(Weapon weapon) {
//        this.weapon = weapon;
//    }


    public Animation<TextureRegion> getSwipeAnimation() {
        return swipeAnimation;
    }

    public void setSwipeAnimation(Animation<TextureRegion> swipeAnimation) {
        this.swipeAnimation = swipeAnimation;
    }

    public void setAttackHitbox(Rectangle attackHitbox) {
        this.attackHitbox = attackHitbox;
    }

    public void setMaxGems(int maxGems) {
        this.maxGems = maxGems;
    }

    public void setAttackingWall(boolean attackingWall) {
        isAttackingWall = attackingWall;
    }
}
