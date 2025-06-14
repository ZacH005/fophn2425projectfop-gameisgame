package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.Collectable;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.*;

public class RangeEnemy extends Enemy {
    private List<Projectile> projectiles = new ArrayList<>();
    private Rectangle shootingRange;

    /**
     * Constructs a RangeEnemy object with the specified position, player reference, HUD, sound manager, animations, and health.
     *
     * @param x           The x-coordinate of the enemy's initial position.
     * @param y           The y-coordinate of the enemy's initial position.
     * @param player      The player object that the enemy interacts with.
     * @param hud         The HUD object for displaying game information.
     * @param soundManager The sound manager for playing sounds.
     * @param animations  A map of animations for the enemy.
     * @param health      The initial health of the enemy.
     */
    public RangeEnemy(float x, float y, Player player, HUD hud, SoundManager soundManager, Map<String, Animation<TextureRegion>> animations, int health) {
        super(x, y, player, hud, soundManager, animations, health);
        shootingRange = new Rectangle(position.x - 140, position.y - 140, 2 * 140, 2 * 140);
    }

    /**
     * Initiates an attack on the player. The enemy will face the player and fire a projectile.
     */
    @Override
    protected void attack() {
        attacking = true;
        attackTimeElapsed = 0f;

        float directionX = player.getPosition().x - this.position.x;
        float directionY = player.getPosition().y - this.position.y;

        if (Math.abs(directionX) > Math.abs(directionY)) {
            currentAnimation = directionX > 0 ? animations.get("rightAttack") : animations.get("leftAttack");
        } else {
            currentAnimation = directionY > 0 ? animations.get("upAttack") : animations.get("downAttack");
        }

        animationTime = 0f;

        Vector2 direction = new Vector2(directionX, directionY).nor();
        projectiles.add(new Projectile(position.x, position.y, direction, 200f));
    }

    private long lastMovementUpdateTime = 0, movementDelay = 500;
    private boolean attacking = false;

    /**
     * Updates the enemy's movement based on the player's position and collision manager.
     *
     * @param colManager The collision manager for handling collisions.
     */
    @Override
    public void updateMovement(CollisionManager colManager) {
        float distanceX = player.getPosition().x - this.position.x;
        float distanceY = player.getPosition().y - this.position.y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (scanRange.overlaps(player.collider) && !following) {
            roaming = false;
            following = true;
            soundManager.onGameStateChange(chaseState);
        } else if (scanRange.overlaps(player.collider) && following) {
            soundManager.onGameStateChange(chaseState);
        }
        if (distance > 140.0f && following) {
            following = false;
            soundManager.onGameStateChange(mainState);
            roaming = true;
            return;
        }
        if (distance < 15.0f && following) {
            following = false;
            roaming = false;
            float directionX = player.getPosition().x - this.position.x;
            float directionY = player.getPosition().y - this.position.y;

            if (Math.abs(directionX) > Math.abs(directionY)) {
                currentAnimation = directionX > 0 ? animations.get("rightIdle") : animations.get("leftIdle");
            } else {
                currentAnimation = directionY > 0 ? animations.get("upIdle") : animations.get("downIdle");
            }
            soundManager.onGameStateChange(chaseState);
            return;
        }

        if (following && hasClearLineOfSight(position, player.getPosition(), colManager)) {
            float directionX = player.getPosition().x - this.position.x;
            float directionY = player.getPosition().y - this.position.y;

            float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

            if (length > 0) {
                directionX /= length;
                directionY /= length;
            }

            this.position.x += directionX * movementSpeed * 0.5f;
            this.position.y += directionY * movementSpeed * 0.5f;
            updateColliders();

            if (Math.abs(directionX) > Math.abs(directionY)) {
                currentAnimation = directionX > 0 ? animations.get("rightWalk") : animations.get("leftWalk");
            } else {
                currentAnimation = directionY > 0 ? animations.get("upWalk") : animations.get("downWalk");
            }
        } else if (following) {
            if (TimeUtils.millis() - lastMovementUpdateTime >= movementDelay) {
                currentPath = findPlayerPath(colManager);
                lastMovementUpdateTime = TimeUtils.millis();
            }
            if (currentPath == null || currentPath.isEmpty()) {
                following = false;
                roaming = true;
                return;
            }

            List<Node> path = currentPath;

            if (path != null && !path.isEmpty()) {
                Node nextNode;

                if (path.size() > 2) {
                    nextNode = path.get(1);
                } else {
                    nextNode = path.get(0);
                }

                float directionX = nextNode.x - this.position.x;
                float directionY = nextNode.y - this.position.y;

                float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

                if (length > 0) {
                    directionX /= length;
                    directionY /= length;
                }

                this.position.x += directionX * movementSpeed * 0.5f;
                this.position.y += directionY * movementSpeed * 0.5f;

                updateColliders();

                if (length < movementSpeed) {
                    currentPath.remove(0);
                }

                if (Math.abs(directionX) > Math.abs(directionY)) {
                    currentAnimation = directionX > 0 ? animations.get("rightWalk") : animations.get("leftWalk");
                } else {
                    currentAnimation = directionY > 0 ? animations.get("upWalk") : animations.get("downWalk");
                }
            }
        }

        Vector2[] directions = {
                new Vector2(0, 1),
                new Vector2(0, -1),
                new Vector2(-1, 0),
                new Vector2(1, 0),
                new Vector2(0, 0)
        };

        if (roaming) {
            if (TimeUtils.millis() - lastMovementUpdateTime >= movementDelay) {
                movement = (int) (5 * Math.random());
                lastMovementUpdateTime = TimeUtils.millis();
            }

            float newX = this.position.x + (directions[movement].x * movementSpeed * 0.2f);
            float newY = this.position.y + (directions[movement].y * movementSpeed * 0.2f);

            Vector2 newPosition = new Vector2(newX, newY);

            int retryCount = 0;
            while (!hasClearLineOfSight(this.position, newPosition, colManager) && retryCount < 5) {
                movement = (int) (5 * Math.random());

                newX = this.position.x + (directions[movement].x * movementSpeed * 0.3f);
                newY = this.position.y + (directions[movement].y * movementSpeed * 0.3f);

                newPosition = new Vector2(newX, newY);
                retryCount++;
            }

            if (retryCount < 5) {
                this.position.x = newX;
                this.position.y = newY;
                updateColliders();
            } else {
                currentAnimation = animations.get("downIdle");
            }

            if (!directions[movement].equals(new Vector2(0, 0)))
                lastDirection = directions[movement];

            switch (movement) {
                case 0:
                    currentAnimation = animations.get("upWalk");
                    break;
                case 1:
                    currentAnimation = animations.get("downWalk");
                    break;
                case 2:
                    currentAnimation = animations.get("leftWalk");
                    break;
                case 3:
                    currentAnimation = animations.get("rightWalk");
                    break;
                case 4:
                    if (lastDirection == null) {
                        currentAnimation = animations.get("downIdle");
                        break;
                    }
                    if (lastDirection.x > 0) {
                        currentAnimation = animations.get("rightIdle");
                        break;
                    }
                    if (lastDirection.x < 0) {
                        currentAnimation = animations.get("leftIdle");
                        break;
                    }
                    if (lastDirection.y < 0) {
                        currentAnimation = animations.get("downIdle");
                        break;
                    }
                    if (lastDirection.y > 0) {
                        currentAnimation = animations.get("upIdle");
                        break;
                    }
                    break;
            }
            updateScanRange();
        }
    }

    int movement = 0;

    /**
     * Checks if there is a clear line of sight between two positions.
     *
     * @param start      The starting position.
     * @param end        The ending position.
     * @param colManager The collision manager for handling collisions.
     * @return True if there is a clear line of sight, otherwise false.
     */
    private boolean hasClearLineOfSight(Vector2 start, Vector2 end, CollisionManager colManager) {
        float steps = 10;
        float stepX = (end.x - start.x) / steps;
        float stepY = (end.y - start.y) / steps;

        for (int i = 1; i <= steps; i++) {
            float checkX = start.x + i * stepX;
            float checkY = start.y + i * stepY;

            Rectangle checkRect = new Rectangle(checkX, checkY, 16, 16);
            if (colManager.checkMapCollision(checkRect) != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Finds a path from the enemy's current position to the player's position using A* algorithm.
     *
     * @param colManager The collision manager for handling collisions.
     * @return A list of nodes representing the path, or null if no path is found.
     */
    private List<Node> findPlayerPath(CollisionManager colManager) {
        int maxNodes = 7840;
        Node start = new Node((int) this.position.x, (int) this.position.y, null, 0, 0);
        Node goal = new Node((int) player.getPosition().x - 8, (int) player.getPosition().y - 8, null, 0, 0);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Set<Node> closedSet = new HashSet<>();

        openSet.add(start);
        int nodesExplored = 0;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            nodesExplored++;

            if (current.equals(goal)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current, colManager)) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeGCost = current.gCost + distanceBetween(current, neighbor);

                if (!openSet.contains(neighbor) || tentativeGCost < neighbor.gCost) {
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = heuristic(neighbor, goal);
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }

            if (nodesExplored >= maxNodes) {
                following = false;
                roaming = true;
                return null;
            }
        }

        return null;
    }

    /**
     * Retrieves the neighboring nodes of a given node.
     *
     * @param node       The node to find neighbors for.
     * @param colManager The collision manager for handling collisions.
     * @return A list of neighboring nodes.
     */
    private List<Node> getNeighbors(Node node, CollisionManager colManager) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            Rectangle potentialPosition = new Rectangle(newX, newY, 14, 14);
            if (colManager.checkMapCollision(potentialPosition) == null) {
                neighbors.add(new Node(newX, newY, null, 0, 0));
            }
        }

        return neighbors;
    }

    /**
     * Calculates the Euclidean distance between two nodes.
     *
     * @param a The first node.
     * @param b The second node.
     * @return The distance between the two nodes.
     */
    private double distanceBetween(Node a, Node b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    /**
     * Calculates the heuristic (estimated cost) from a node to the goal node.
     *
     * @param a The current node.
     * @param b The goal node.
     * @return The heuristic value.
     */
    private double heuristic(Node a, Node b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    int n = 5;

    /**
     * Reconstructs the path from the goal node back to the start node.
     *
     * @param node The goal node.
     * @return A list of nodes representing the path.
     */
    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        int count = 0;

        while (node != null) {
            if (count % n == 0) {
                path.add(node);
            }
            count++;
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Updates the scan range of the enemy based on its last movement direction.
     */
    private void updateScanRange() {
        float offsetX = 0, offsetY = 0;

        if (lastDirection != null) {
            if (lastDirection.x > 0) {
                scanRange.set(position.x + offsetX, position.y - scanrangeheight / 2f, scanrangewidth, scanrangeheight);
            } else if (lastDirection.x < 0) {
                offsetX = -scanrangewidth + 16;
                scanRange.set(position.x + offsetX, position.y - scanrangeheight / 2f, scanrangewidth, scanrangeheight);
            } else if (lastDirection.y > 0) {
                scanRange.set(position.x - scanrangewidth / 2f, position.y + offsetY, scanrangeheight, scanrangewidth);
            } else if (lastDirection.y < 0) {
                offsetY = -scanrangeheight + 16;
                scanRange.set(position.x - scanrangewidth / 2f, position.y + offsetY, scanrangeheight, scanrangewidth);
            }
        }
    }

    /**
     * Updates the projectiles associated with the enemy.
     *
     * @param delta The time elapsed since the last update.
     */
    @Override
    public void updateProjectiles(float delta) {
        for (Projectile projectile : projectiles) {
            projectile.update(delta);
        }
        projectiles.removeIf(projectile -> !projectile.isActive());

        shootingRange.setPosition(position.x - 140, position.y - 140);
    }

    /**
     * Renders the projectiles associated with the enemy.
     *
     * @param batch The sprite batch used for rendering.
     */
    @Override
    public void renderProjectiles(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }
    }

    /**
     * Checks if the enemy is damaging the player and initiates an attack if conditions are met.
     */
    @Override
    protected void checkDamaging(CollisionManager colManager) {
        ArrayList<Projectile> activeShots = new ArrayList<>();
        projectiles.stream().filter(Projectile::isActive).forEach(activeShots::add);

        if (shootingRange.overlaps(player.collider) && hasClearLineOfSight(position, player.getPosition(), colManager)) {
            if (TimeUtils.nanoTime() - lastDamageTime >= cooldownTime * 1_000_000_000L) {
                attack();
                lastDamageTime = TimeUtils.nanoTime();
            }
        }

        for (Projectile projectile : activeShots) {
            if (projectile.getCollider().overlaps(player.collider)) {
                player.takeDamage(0.25f);
                player.redEffectTime = 0f;
                player.isRedEffectActive = true;
                player.applyKnockback(getPosition(), 150);

                hud.updateHearts(player.getHealth());
                if (player.getHealth() % 1 == 0) {
                    player.startFlickering(cooldownTime);
                }
                projectile.deactivate();
            } else if (colManager.checkMapCollision(projectile.getCollider()) != null)
                projectile.deactivate();
        }
    }

    /// ENTITY METHODS

    /**
     * Heals the enemy. This method is currently empty.
     */
    @Override
    public void heal() {
    }

    /**
     * Applies damage to the enemy and triggers knockback and sound effects.
     *
     * @param amount The amount of damage to apply.
     */
    @Override
    public void takeDamage(float amount) {
        if (health > 0) {
            health -= (int) amount;
            Sound hurtSound = soundManager.getSound("enemyHurt");
            long id = hurtSound.play(soundManager.getSfxVolume());
            Random random = new Random();
            hurtSound.setPitch(id, 0.8f + random.nextFloat() * 2);
            applyKnockback(player.getPosition(), 150);
        }
        hurtParticle.setPosition(damageCollider.x + damageCollider.width / 2,
                damageCollider.y + damageCollider.height / 2);
        hurtParticle.reset();

        if (health == 0 && !isDead) {
            isDead = true;
            soundManager.onGameStateChange(mainState);
        }
    }

    /**
     * Gets the current health of the enemy.
     *
     * @return The current health of the enemy.
     */
    @Override
    public float getHealth() {
        return 0;
    }

    /**
     * Sets the health of the enemy.
     *
     * @param health The new health value.
     */
    @Override
    public void setHealth(float health) {
    }

    /**
     * Gets the current position of the enemy.
     *
     * @return The current position of the enemy.
     */
    @Override
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Sets the position of the enemy.
     *
     * @param position The new position of the enemy.
     */
    @Override
    public void setPosition(Vector2 position) {
    }

    /**
     * Checks if the enemy is following the player.
     *
     * @return True if the enemy is following the player, otherwise false.
     */
    @Override
    public boolean isFollowing() {
        return false;
    }

    /**
     * Sets whether the enemy is following the player.
     *
     * @param following True to make the enemy follow the player, otherwise false.
     */
    @Override
    public void setFollowing(boolean following) {
    }

    /**
     * Gets the armor value of the enemy.
     *
     * @return The armor value of the enemy.
     */
    @Override
    public int getArmor() {
        return 0;
    }

    /**
     * Sets the armor value of the enemy.
     *
     * @param armor The new armor value.
     */
    @Override
    public void setArmor(int armor) {
    }

    /**
     * Gets the list of power-ups associated with the enemy.
     *
     * @return The list of power-ups.
     */
    @Override
    public List<Powerup> getPowerUps() {
        return List.of();
    }

    /**
     * Sets the list of power-ups associated with the enemy.
     *
     * @param powerUps The new list of power-ups.
     */
    @Override
    public void setPowerUps(List<Powerup> powerUps) {
    }

    /**
     * Gets the amount of money the enemy has.
     *
     * @return The amount of money.
     */
    @Override
    public int getMoney() {
        return 0;
    }

    /**
     * Sets the amount of money the enemy has.
     *
     * @param money The new amount of money.
     */
    @Override
    public void setMoney(int money) {
    }

    /**
     * Saves the current state of the enemy to a file.
     *
     * @param filename The name of the file to save the state to.
     */
    @Override
    public void saveState(String filename) {
    }

    /**
     * Loads the state of the enemy from a file.
     *
     * @param filename The name of the file to load the state from.
     */
    @Override
    public void loadState(String filename) {
    }

    /**
     * Gets the shooting range of the enemy.
     *
     * @return The shooting range of the enemy.
     */
    public Rectangle getShootingRange() {
        return shootingRange;
    }

    /**
     * Sets the shooting range of the enemy.
     *
     * @param shootingRange The new shooting range.
     */
    public void setShootingRange(Rectangle shootingRange) {
        this.shootingRange = shootingRange;
    }
}