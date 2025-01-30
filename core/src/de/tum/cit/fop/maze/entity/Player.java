package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import de.tum.cit.fop.maze.ScreenShake;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Entity, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int maxGems = 0;
    private boolean isTakingDamage = false;
    private Timer.Task damageTask;
    private boolean isDead=false;

    private int currentTileX;
    private int currentTileY;
    private float footstepTimer = 0f;
    private boolean adjust = false;
    private static final float FOOTSTEP_INTERVAL = 0.2f;
    private boolean isAttackingWall = false;

    public boolean isKnockedBack = false;
    private float knockbackTime = 0f;
    private static final float KNOCKBACK_DURATION = 0.1f;
    private Vector2 knockbackVelocity = new Vector2();

    private Vector2 velocity;
    private float speed;
    private boolean isMoving;

    private Animation<TextureRegion> currentAnimation;
    private float animationTime;

    private Direction direction;
    public boolean isAttack = false;

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
    private Vector2 position;
    public final Vector2 resetpos;
    private float health;
    private int armor;
    private List<Powerup> powerups;
    private int money;
    private int maxHealth;
    private int keys = 0;
    public Rectangle newPos;
    private int brokenwalls = 0;
    private int hitexit = 0;

    private float flickerAlpha = 1.0f;
    private boolean isFlickering = false;
    private float flickerTime = 0f;
    private static final float FLICKER_DURATION = 0.1f;
    private float flickertotaltime = 0f;
    private float totalflickerduration;

    private Vector2 lastValidPosition;

    private boolean isSprinting;

    private SoundManager soundManager;

    private OrthographicCamera camera;

    private Animation<TextureRegion> swipeAnimation;

    private boolean inWater;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private float startposx;
    private float startposy;
    private Vector2 startPos;

    private ScreenShake screenShake;

    private ParticleEffect hurtParticle;

    /**
     * Constructs a new Player with the specified position, map, health, armor, powerups, money, sound manager, camera, and screen shake.
     *
     * @param x           The initial x-coordinate of the player.
     * @param y           The initial y-coordinate of the player.
     * @param tiledMap    The TiledMap the player is in.
     * @param health      The initial health of the player.
     * @param armor       The initial armor of the player.
     * @param powerups    The list of powerups the player has.
     * @param money       The initial money of the player.
     * @param soundManager The sound manager for playing sounds.
     * @param camera      The camera for rendering.
     * @param screenShake The screen shake effect.
     */
    public Player(float x, float y, TiledMap tiledMap, float health, int armor, List<String> powerups, int money, SoundManager soundManager, OrthographicCamera camera, ScreenShake screenShake) {
        this.tileSize = 16;
        this.velocity = new Vector2(0, 0);
        this.tiledMap = tiledMap;
        this.width = tileSize;
        this.height = tileSize;
        this.speed = 110;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.animationTime = 0f;
        this.newPos = new Rectangle(x, y, width, height);
        this.isSprinting = false;
        this.soundManager = soundManager;
        this.gems = 0;
        this.startPos = new Vector2(x, y);
        this.position = startPos;
        this.startposx = x;
        this.startposy = y;
        this.resetpos = new Vector2(25, 25);
        this.health = health;
        this.armor = armor;
        this.money = money;
        this.powerups = new ArrayList<>();
        this.collider = new Rectangle(position.x - tileSize / 2 + 5, position.y - tileSize / 2, width, height);
        this.maxHealth = 7;
        this.lastValidPosition = new Vector2(x, y);
        this.currentTileX = (int) position.x / tileSize;
        this.currentTileY = (int) position.y / tileSize;
        this.camera = camera;
        loadWeaponAnimation();
        this.screenShake = screenShake;
        hurtParticle = new ParticleEffect();
        hurtParticle.load(Gdx.files.internal("particles/effects/Particle Park Blood.p"), Gdx.files.internal("particles/images"));
    }

    /**
     * Starts the flickering effect for the player.
     *
     * @param time The duration of the flickering effect.
     */
    public void startFlickering(float time) {
        isFlickering = true;
        flickerTime = 0f;
        totalflickerduration = time;
    }

    /**
     * Stops the flickering effect for the player.
     */
    public void stopFlickering() {
        isFlickering = false;
        flickerAlpha = 1.0f;
    }

    /**
     * Attacks the specified list of enemies.
     *
     * @param enemies The list of enemies to attack.
     */
    public void attack(List<Enemy> enemies) {
        attackHitbox = getAttackHitbox();

        for (Enemy enemy : enemies) {
            if (attackHitbox.overlaps(enemy.damageCollider)) {
                enemy.takeDamage(1f);
                if (enemy.isDead)
                    screenShake.startShake(0.3f, 1f);
                else
                    screenShake.startShake(0.3f, 0.5f);
            }
        }
        isAttack = true;
    }

    /**
     * Gets the attack hitbox of the player.
     *
     * @return The Rectangle representing the attack hitbox.
     */
    public Rectangle getAttackHitbox() {
        float width = 30;
        float height = 48;

        float x = position.x;
        float y = position.y;

        switch (direction) {
            case LEFT:
                x -= this.width + 8;
                y -= 40 / 2f;
                break;
            case RIGHT:
                x += this.width - 8;
                y -= 40 / 2f;
                break;
            case UP:
                x -= 16f;
                width = 48;
                height = 30;
                break;
            case DOWN:
                y -= 48 / 2f;
                x -= 16f;
                width = 48;
                height = 30;
                break;
        }

        return new Rectangle(x, y, width, height);
    }

    private Rectangle attackHitbox;

    /**
     * Loads the weapon animation for the player.
     */
    public void loadWeaponAnimation() {
        Texture swipeSheet = new Texture(Gdx.files.internal("animations/player/pickaxeSwipe_dark2 copy.png"));
        int frameWidth = 57, frameHeight = 32, swipeAnimationFrames = 6, y = 0;

        Array<TextureRegion> swipeFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < swipeAnimationFrames; col++) {
            swipeFrames.add(new TextureRegion(swipeSheet, col * (frameWidth), y, frameWidth, frameHeight));
        }

        this.swipeAnimation = new Animation<>(0.05f, swipeFrames);
    }

    /**
     * Applies knockback to the player.
     *
     * @param enemyPosition The position of the enemy causing the knockback.
     * @param force         The force of the knockback.
     */
    public void applyKnockback(Vector2 enemyPosition, float force) {
        Vector2 knockbackDirection = new Vector2(position.x - enemyPosition.x, position.y - enemyPosition.y).nor();
        knockbackVelocity.set(knockbackDirection.scl(force));
        isKnockedBack = true;
        knockbackTime = 0f;
    }

    /**
     * Updates the flicker effect for the player.
     *
     * @param delta The time elapsed since the last update.
     */
    public void updateFlickerEffect(float delta) {
        if (isFlickering) {
            flickertotaltime += delta;
            flickerTime += delta;
            if (flickertotaltime > totalflickerduration) {
                stopFlickering();
                isFlickering = false;
                flickertotaltime = 0f;
            }
            if (flickerTime >= FLICKER_DURATION) {
                flickerAlpha = (flickerAlpha == 1.0f) ? 0.0f : 1.0f;
                flickerTime = 0f;
            }
        }
    }

    /**
     * Gets the number of broken walls.
     *
     * @return The number of broken walls.
     */
    public int getBrokenwalls() {
        return brokenwalls;
    }

    /**
     * Sets the number of broken walls.
     *
     * @param brokenwalls The number of broken walls.
     */
    public void setBrokenwalls(int brokenwalls) {
        this.brokenwalls = brokenwalls;
    }

    /**
     * Sets the current animation for the player.
     *
     * @param animation The animation to set.
     */
    public void setCurrentAnimation(Animation<TextureRegion> animation) {
        this.currentAnimation = animation;
    }

    boolean trapped = false;

    /**
     * Updates the player's state.
     *
     * @param delta      The time elapsed since the last update.
     * @param colManager The collision manager for checking collisions.
     */
    public void update(float delta, CollisionManager colManager) {
        hurtParticle.update(Gdx.graphics.getDeltaTime());
        updateFlickerEffect(delta);

        if (isRedEffectActive) {
            redEffectTime += delta;
            if (redEffectTime >= RED_EFFECT_DURATION) {
                isRedEffectActive = false;
            }
        }

        footstepTimer += delta;

        animationTime += delta;

        if (isAttack)
            attackAnimationTime += delta;

        if (isKnockedBack) {
            knockbackTime += delta;
            if (knockbackTime < KNOCKBACK_DURATION) {
                float newX = position.x + knockbackVelocity.x * delta;
                float newY = position.y + knockbackVelocity.y * delta;
                newPos = new Rectangle(newX, newY - 7, width - 2, height - 2);

                if (colManager.checkMapCollision(newPos) == null || colManager.checkMapCollision(newPos).equals("Water")) {
                    position.x = newX;
                    position.y = newY;
                    collider.x = newX;
                    collider.y = newY - 8;
                }
            } else {
                isKnockedBack = false;
                knockbackVelocity.set(0, 0);
            }
        } else {
            if (isMoving) {
                float newX = position.x + velocity.x * speed * delta;
                float newY = position.y + velocity.y * speed * delta;
                newPos = new Rectangle(newX, newY - 7, width - 2, height - 2);

                if (colManager.checkEventCollision(newPos) != null && colManager.checkEventCollision(newPos).equals("Finish")) {
                    hitexit = 1;
                }

                if (colManager.checkEventCollision(newPos) != null && colManager.checkEventCollision(newPos).equals("Finish") && gems == maxGems) {
                    colManager.setWonLevel(true);
                }

                if (colManager.checkMapCollision(newPos) == null || colManager.checkMapCollision(newPos).equals("Water")) {
                    position.x = newX;
                    position.y = newY;
                    collider.x = newX;
                    collider.y = newY - 8;

                    int newTileX = (int) newX / tileSize;
                    int newTileY = (int) newY / tileSize;

                    if ((newTileX != currentTileX || newTileY != currentTileY) && footstepTimer >= FOOTSTEP_INTERVAL) {
                        currentTileX = newTileX;
                        currentTileY = newTileY;
                        soundManager.playSound("mcFootstep_sfx");
                        footstepTimer = 0f;
                    }
                } else if (colManager.checkMapCollision(newPos).equals("Trap")) {
                    RectangleMapObject r = (RectangleMapObject) colManager.getMapCollider(newPos);
                    redEffectTime = 0f;
                    isRedEffectActive = true;
                    takeDamage(0.25f);
                    applyKnockback(new Vector2((r.getRectangle().x + (r.getRectangle().getWidth() / 2)), (r.getRectangle().y + (r.getRectangle().getHeight() / 2))), 800);
                    startFlickering(0.5f);
                }
                if (colManager.checkMapCollision(newPos)!= null && colManager.checkMapCollision(newPos).equals("Water")) {
                    inWater=true;
                    if (!isTakingDamage) {
                        startTakingDamage();
                    }
                } else {
                    stopTakingDamage();
                    inWater=false;
                }
            }
        }
    }

    private void startTakingDamage() {
        isTakingDamage = true;

        speed = 55;
        damageTask = new Timer.Task() {
            @Override
            public void run() {
                if(!isDead) {
                    takeDamage(0.25f);
                }
                }
        };

        Timer.schedule(damageTask, 0, 1f);
    }

    private void stopTakingDamage() {
        if (damageTask != null) {
            damageTask.cancel();
            damageTask = null;
        }
        if (isTakingDamage) {
            speed = 110;
            setSprinting(false);
        }
        isTakingDamage = false;
    }

    /**
     * Gets the exit hit status.
     *
     * @return The exit hit status.
     */
    public int getHitexit() {
        return hitexit;
    }

    /**
     * Renders the player on the screen.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    public void render(SpriteBatch batch) {
        hurtParticle.draw(batch);

        if (isAttack) {
            TextureRegion swipeFrame = swipeAnimation.getKeyFrame(attackAnimationTime, false);

            float swipeWidth = attackHitbox.width, swipeHeight = attackHitbox.height, swipeRotation = 0f, swipeY = attackHitbox.y, swipeX = attackHitbox.x;

            if (direction == Direction.UP) {
                if (!swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
            }
            if (direction == Direction.DOWN) {
                if (swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
            }
            if (direction == Direction.RIGHT) {
                if (swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
                swipeHeight = attackHitbox.width;
                swipeWidth = attackHitbox.height;
                swipeRotation = 90f;
                swipeX += 32;
            }
            if (direction == Direction.LEFT) {
                if (!swipeFrame.isFlipY())
                    swipeFrame.flip(false, true);
                swipeHeight = attackHitbox.width;
                swipeWidth = attackHitbox.height;
                swipeRotation = 90f;
                swipeX += 28;
            }
            if (isAttackingWall) {
                batch.setColor(1f, 1f, 0f, 1f);
            }
            batch.draw(swipeFrame, swipeX, swipeY, 0f, 0f, swipeWidth, swipeHeight, 1f, 1f, swipeRotation);
            if (isAttackingWall) {
                batch.setColor(1f, 1f, 1f, 1f);
            }
        } else {
            attackAnimationTime = 0;
        }

        if (currentAnimation != null) {
            TextureRegion frame = currentAnimation.getKeyFrame(animationTime, true);

            if (isFlickering) {
                batch.setColor(1, 1, 1, flickerAlpha);
            } else if (isRedEffectActive) {
                batch.setColor(0.7f, 0, 0, 1);
            } else {
                batch.setColor(1, 1, 1, 1);
            }

            if (isAttack)
                frame = currentAnimation.getKeyFrame(attackAnimationTime, false);
            else
                frame = currentAnimation.getKeyFrame(animationTime, true);

            if(inWater){
                TextureRegion headRegion = new TextureRegion(
                        frame.getTexture(),
                        frame.getRegionX(),
                        frame.getRegionY(),
                        frame.getRegionWidth(),
                        35
                );
                batch.draw(headRegion,position.x - (width / 2) - 2.5f, position.y - (height / 2), width*2.0f, ((height * 2.0f)/64)*35);
            }else {
                if (adjust) {
                    batch.draw(frame, position.x - (width / 2) - 2.5f + 4f, position.y - (height / 2), width * 2.0f, height * 2.0f);
                } else {
                    batch.draw(frame, position.x - (width / 2) - 2.5f, position.y - (height / 2), width * 2.0f, height * 2.0f);
                }
            }

            batch.setColor(1, 1, 1, 1);
        }
    }

    private float attackAnimationTime;

    /**
     * Respawns the player at the starting position.
     */
    public void respawn() {
        setPosition(new Vector2(startposx, startposy));
        trapped = false;
    }

    /**
     * Sets the adjust flag for rendering.
     *
     * @param adjust The adjust flag.
     */
    public void setAdjust(boolean adjust) {
        this.adjust = adjust;
    }

    /**
     * Moves the player in the specified direction.
     *
     * @param direction The direction to move.
     */
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

    /**
     * Stops the player's movement.
     */
    public void stop() {
        isMoving = false;
        velocity.set(0, 0);
    }

    /**
     * Sets the player's speed.
     *
     * @param speed The speed to set.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Gets the player's velocity.
     *
     * @return The player's velocity.
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Heals the player.
     */
    @Override
    public void heal() {
        if (health < maxHealth) {
            health = (float) Math.ceil(health + 1);
        }
    }

    /**
     * Gets the current animation of the player.
     *
     * @return The current animation.
     */
    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Makes the player take damage.
     *
     * @param x The amount of damage to take.
     */
    @Override
    public void takeDamage(float x) {
        health -= x;
        if(health==0){
            isDead=true;
        }
        soundManager.playSound("playerHurt");
        hurtParticle.setPosition(position.x + width / 2, position.y + height / 2);
        hurtParticle.reset();
        screenShake.startShake(0.5f, 1f);
    }

    /**
     * Gets the player's health.
     *
     * @return The player's health.
     */
    @Override
    public float getHealth() {
        return health;
    }

    /**
     * Gets the number of gems collected by the player.
     *
     * @return The number of gems.
     */
    public int getGems() {
        return gems;
    }

    /**
     * Sets the number of gems collected by the player.
     *
     * @param gems The number of gems.
     */
    public void setGems(int gems) {
        this.gems = gems;
    }

    /**
     * Sets the player's health.
     *
     * @param health The health to set.
     */
    @Override
    public void setHealth(float health) {
        this.health = health;
    }

    /**
     * Gets the player's position.
     *
     * @return The player's position.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Sets the player's position.
     *
     * @param position The position to set.
     */
    @Override
    public void setPosition(Vector2 position) {
        this.position = position;
        collider.x = position.x;
        collider.y = position.y - 8;
    }

    /**
     * Checks if the player is following another entity.
     *
     * @return True if the player is following, false otherwise.
     */
    @Override
    public boolean isFollowing() {
        return false;
    }

    /**
     * Sets the following status of the player.
     *
     * @param following The following status.
     */
    @Override
    public void setFollowing(boolean following) {
    }

    /**
     * Gets the player's armor.
     *
     * @return The player's armor.
     */
    @Override
    public int getArmor() {
        return armor;
    }

    /**
     * Sets the player's armor.
     *
     * @param armor The armor to set.
     */
    @Override
    public void setArmor(int armor) {
        this.armor = armor;
    }

    /**
     * Gets the player's powerups.
     *
     * @return The list of powerups.
     */
    @Override
    public List<Powerup> getPowerUps() {
        return powerups;
    }

    /**
     * Sets the player's powerups.
     *
     * @param powerUps The list of powerups.
     */
    @Override
    public void setPowerUps(List<Powerup> powerUps) {
        this.powerups = powerUps;
    }

    /**
     * Gets the player's money.
     *
     * @return The player's money.
     */
    @Override
    public int getMoney() {
        return money;
    }

    /**
     * Sets the player's money.
     *
     * @param money The money to set.
     */
    @Override
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * Saves the player's state to a file.
     *
     * @param filename The name of the file to save to.
     */
    @Override
    public void saveState(String filename) {
        EntityUtils.saveToFile(this, filename);
    }

    /**
     * Loads the player's state from a file.
     *
     * @param filename The name of the file to load from.
     */
    @Override
    public void loadState(String filename) {
        Entity loaded = EntityUtils.loadFromFile(filename, this);
        if (loaded instanceof Player loadedPlayer) {
            this.health = loadedPlayer.health;
            this.armor = loadedPlayer.armor;
            this.money = loadedPlayer.money;
        }
    }

    /**
     * Gets the player's direction.
     *
     * @return The player's direction.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Checks if the player is moving.
     *
     * @return True if the player is moving, false otherwise.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Gets the player's speed.
     *
     * @return The player's speed.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Checks if the player is sprinting.
     *
     * @return True if the player is sprinting, false otherwise.
     */
    public boolean isSprinting() {
        return isSprinting;
    }

    /**
     * Sets the player's sprinting status.
     *
     * @param sprinting The sprinting status.
     */
    public void setSprinting(boolean sprinting) {
        isSprinting = sprinting;
    }

    /**
     * Gets the player's maximum health.
     *
     * @return The player's maximum health.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Sets the player's maximum health.
     *
     * @param maxHealth The maximum health to set.
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * Gets the player's collider.
     *
     * @return The player's collider.
     */
    public Rectangle getCollider() {
        return collider;
    }

    /**
     * Gets the number of keys the player has.
     *
     * @return The number of keys.
     */
    public int getKeys() {
        return keys;
    }

    /**
     * Sets the number of keys the player has.
     *
     * @param keys The number of keys.
     */
    public void setKeys(int keys) {
        this.keys = keys;
    }

    /**
     * Gets the swipe animation of the player.
     *
     * @return The swipe animation.
     */
    public Animation<TextureRegion> getSwipeAnimation() {
        return swipeAnimation;
    }

    /**
     * Sets the swipe animation of the player.
     *
     * @param swipeAnimation The swipe animation to set.
     */
    public void setSwipeAnimation(Animation<TextureRegion> swipeAnimation) {
        this.swipeAnimation = swipeAnimation;
    }

    /**
     * Sets the attack hitbox of the player.
     *
     * @param attackHitbox The attack hitbox to set.
     */
    public void setAttackHitbox(Rectangle attackHitbox) {
        this.attackHitbox = attackHitbox;
    }

    /**
     * Sets the maximum number of gems the player can collect.
     *
     * @param maxGems The maximum number of gems.
     */
    public void setMaxGems(int maxGems) {
        this.maxGems = maxGems;
    }

    /**
     * Sets whether the player is attacking a wall.
     *
     * @param attackingWall True if the player is attacking a wall, false otherwise.
     */
    public void setAttackingWall(boolean attackingWall) {
        isAttackingWall = attackingWall;
    }
}