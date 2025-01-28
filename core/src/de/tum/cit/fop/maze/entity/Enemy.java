package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.Powerup;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.*;


public class Enemy implements Entity {
    public boolean isDead = false;
    public Vector2 position;
    public Rectangle scanRange;
    private Player player;
    private boolean following;
    private boolean roaming;

    private Animation<TextureRegion> currentAnimation;
    private float animationTime;

    public Rectangle damageCollider;
    ///sound manager stuff.
    private SoundManager soundManager;
    private Map<String,Integer> chaseState = new HashMap<String,Integer>();
    private Map<String,Integer> mainState = new HashMap<String,Integer>();

    private float lastDamageTime;
    private float cooldownTime=2f;
    private HUD hud;
    int scanrangewidth;
    int scanrangeheight;

    private float initialposx;
    private float initialposy;
    private int health;

    private float movementSpeed;
    private List<Node> currentPath;

    private Vector2 knockbackVelocity = new Vector2(0, 0);
    private float knockbackDuration = 0;
    private float knockbackTimeElapsed = 0;

    private Map<String, Animation<TextureRegion>> animations;

    private Vector2 lastDirection;

    private ParticleEffect hurtParticle;

    public Enemy(float x, float y,Player player,HUD hud,SoundManager soundManager, Map<String, Animation<TextureRegion>> animations) {

            this.player = player;
        position = new Vector2(x,y);
        initialposx = x;
        initialposy = y;
        scanrangewidth = 100;
        scanrangeheight = 100;
        scanRange = new Rectangle(position.x-scanrangewidth/2f+8,position.y-scanrangeheight/2f+4,scanrangeheight,scanrangewidth);
        damageCollider = new Rectangle(position.x-2,position.y-5,20,20);
        health = 3;

        movementSpeed = 5.5f;

        this.soundManager=soundManager;
        chaseState.put("crackles",0);
        chaseState.put("wind",1);
        chaseState.put("piano",1);
        chaseState.put("strings",0);
        chaseState.put("pad",0);
        chaseState.put("drums",1);
        chaseState.put("bass",1);
        chaseState.put("slowerDrums", 0);

        mainState.put("crackles",1);
        mainState.put("wind",1);
        mainState.put("piano",1);
        mainState.put("strings",0);
        mainState.put("pad",1);
        mainState.put("drums",0);
        mainState.put("bass",1);
        mainState.put("slowerDrums", 1);

        this.hud=hud;

        roaming = true;

        hurting = false;
        this.animations = animations;

        this.currentAnimation = animations.get("downWalk");

        hurtParticle = new ParticleEffect();
        hurtParticle.load(Gdx.files.internal("particles/effects/Particle Park Blood.p"), Gdx.files.internal("particles/images"));
    }

    public void update(float delta, CollisionManager colManager) {
        hurtParticle.update(delta);
        animationTime += delta;

        if (isDead) return;


        if (knockbackDuration > 0) {
            position.add(knockbackVelocity.x * delta, knockbackVelocity.y * delta);

            knockbackTimeElapsed += delta;
            if (knockbackTimeElapsed >= knockbackDuration) {
                knockbackVelocity.set(0, 0);
                knockbackDuration = 0;
                knockbackTimeElapsed = 0;
            }
            hurting = true;
            attacking = false;
            updateColliders();
        } else if (attacking) {
            attackTimeElapsed += delta;

            if (attackTimeElapsed >= attackDuration) {
                attacking = false;
                following = true;
                attackTimeElapsed = 0f;
            }
        } else {
            updateMovement(colManager);
            hurting = false;
        }

        // Check for damage/attacks
        checkDamaging(delta);
    }

    public void render(SpriteBatch batch) {
        hurtParticle.draw(batch);

        if (currentAnimation != null) {
            TextureRegion frame;

            if (isDead) {
                currentAnimation = animations.get("death");
                frame = currentAnimation.getKeyFrame(animationTime, false);
            } else if (hurting) {
                currentAnimation = animations.get("upKnock");
                batch.setColor(0.7f, 0, 0, 1);
                frame = currentAnimation.getKeyFrame(animationTime, true);
            } else if (attacking) {
                frame = currentAnimation.getKeyFrame(animationTime, false);
            } else {
                frame = currentAnimation.getKeyFrame(animationTime, true);
            }

            batch.draw(frame, position.x - ((float) 16 / 2), position.y - ((float) 16 / 2), 16 * 2.0f, 16 * 2.0f);
            batch.setColor(1, 1, 1, 1);
        }
    }



    public void applyKnockback(Vector2 sourcePosition, float strength) {
        Vector2 knockbackDirection = new Vector2(position.x - sourcePosition.x, position.y - sourcePosition.y).nor();

        knockbackVelocity.set(knockbackDirection.scl(strength));
        knockbackDuration = 0.095f;
        knockbackTimeElapsed = 0;
    }

//    public TextureRegion getEnemy(){
//        return animation.getFrame();
//    }

    @Override
    public void heal() {

    }

    @Override
    public float getHealth() {
        return health;
    }

    public boolean isFollowing() {
        return following;
    }

    @Override
    public void setFollowing(boolean following) {
        this.following=following;
    }

    @Override
    public void setHealth(float health) {

    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position.x=position.x;
        this.position.y=position.y;
        scanRange.setPosition(position.x-scanrangewidth/2f+8,position.y-scanrangeheight/2f+4);
        damageCollider.setPosition(position.x-2,position.y-5);
    }
    private void checkDamaging(float delta) {
        if (damageCollider.overlaps(player.collider)) {
            // only process if cooldown has passed
            if (TimeUtils.nanoTime() - lastDamageTime >= cooldownTime * 1000000000L) {
                // proceed with damage logic
                attack();
                // update last damage time
                lastDamageTime = TimeUtils.nanoTime();
            }
        }
    }

    @Override
    public int getArmor() {
        return 0;
    }

    @Override
    public void setArmor(int armor) {

    }

    @Override
    public List<Powerup> getPowerUps() {
        return List.of();
    }

    @Override
    public void setPowerUps(List<Powerup> powerUps) {

    }

    @Override
    public int getMoney() {
        return 0;
    }

    @Override
    public void setMoney(int money) {

    }
    private boolean hurting;
//    private float lastHurtingTime = 0, hurtCooldown = 0.2f; // Time in seconds (200f = 200 seconds)

    @Override
    public void takeDamage(float amount) {
//            hurting = true;  // Start hurting if the cooldown has passed

//        if (hurting) {
            // Play hurt sound and apply damage effects here
        if (health > 0) {
            health -= amount;
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
            System.out.println("Enemy defeated!");
        }

            // After hurting is active for the cooldown duration, set it to false
//            if (TimeUtils.nanoTime() - lastHurtingTime >= hurtCooldown * 1000000000L) {
//            }
//        }
    }


    @Override
    public void saveState(String filename) {
        EntityUtils.saveToFile(this, filename);
    }

    @Override
    public void loadState(String filename) {
        Entity loaded = EntityUtils.loadFromFile(filename,this);
        if (loaded instanceof Enemy loadedEnemy) {
            this.position = loadedEnemy.position;
        }
    }

    ///beginning of movement
    private long lastMovementUpdateTime = 0, movementDelay = 500;
    private boolean attacking = false;

    public void updateMovement(CollisionManager colManager) {
        float distanceX = player.getPosition().x - this.position.x;
        float distanceY = player.getPosition().y - this.position.y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

//        if (scanRange.overlaps(player.collider) && !following && hasClearLineOfSight(position, player.getPosition(), colManager)) {
        if (scanRange.overlaps(player.collider) && !following) {
            roaming = false;
            following = true;
            soundManager.onGameStateChange(chaseState);
        } else if (scanRange.overlaps(player.collider) && following)    {
            soundManager.onGameStateChange(chaseState);
        }
        if (distance > 140.0f&& following) {
            following = false;
            soundManager.onGameStateChange(mainState);
            roaming = true;
            return;
        }
        if (distance < 15.0f && following) {
            following = false;
            roaming = false;
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

            this.position.x += directionX * movementSpeed*0.5f;
            this.position.y += directionY * movementSpeed*0.5f;
            updateColliders();

            if (Math.abs(directionX) > Math.abs(directionY)) {
                currentAnimation = directionX > 0 ? animations.get("rightWalk") : animations.get("leftWalk");
            } else {
                currentAnimation = directionY > 0 ? animations.get("upWalk") : animations.get("downWalk");
            }
        } else if (following) {

            //decides how long until the path si recalculated (rn 500 millisec)
            if (TimeUtils.millis() - lastMovementUpdateTime >= movementDelay) {
                System.out.println("recalculating");
                currentPath = findPlayerPath(colManager);
                lastMovementUpdateTime = TimeUtils.millis();
            }
            if (currentPath == null || currentPath.isEmpty()) {
                System.out.println("No valid path. Switching to roaming mode.");
//                currentAnimation = animations.get("downIdle");
                following = false;
                roaming = true;
                return;
            }

            List<Node> path = currentPath;

            if (path != null && !path.isEmpty()) {
                //get direction of difference between first node and next node (get the vector)
                //move along that vector
                Node nextNode;

                if (path.size()>2) {
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

                //makes sure that it updates once the node is reached
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
            //finds random positon
            if (TimeUtils.millis() - lastMovementUpdateTime >= movementDelay) {
                movement = (int)(5 * Math.random());
                lastMovementUpdateTime = TimeUtils.millis();
            }
            //constatnyly move in that directoin
            float newX = this.position.x + (directions[movement].x * movementSpeed * 0.2f);
            float newY = this.position.y + (directions[movement].y * movementSpeed * 0.2f);

            Vector2 newPosition = new Vector2(newX, newY);

            int retryCount = 0;
            //collision check
            while (!hasClearLineOfSight(this.position, newPosition, colManager) && retryCount < 5) {
                movement = (int)(5 * Math.random());

                //check again
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
                System.out.println("stuck");
            }

            if (!directions[movement].equals(new Vector2(0, 0)))
                lastDirection = directions[movement];

            switch (movement)   {
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
                    if (lastDirection == null)  {
                        currentAnimation = animations.get("downIdle");
                        break;
                    }
                    if (lastDirection.x > 0)    {
                        currentAnimation = animations.get("rightIdle");
                        break;
                    }
                    if (lastDirection.x < 0)    {
                        currentAnimation = animations.get("leftIdle");
                        break;
                    }
                    if (lastDirection.y < 0)    {
                        currentAnimation = animations.get("downIdle");
                        break;
                    }
                    if (lastDirection.y > 0)    {
                        currentAnimation = animations.get("upIdle");
                        break;
                    }
                    break;
            }
        }


    }
    int movement = 0;


    private boolean hasClearLineOfSight(Vector2 start, Vector2 end, CollisionManager colManager) {
        float steps = 10;
        float stepX = (end.x - start.x) / steps;
        float stepY = (end.y - start.y) / steps;

        for (int i = 1; i <= steps; i++) {
            float checkX = start.x + i * stepX;
            float checkY = start.y + i * stepY;

            Rectangle checkRect = new Rectangle(checkX, checkY, 16, 16);
            if (colManager.checkMapCollision(checkRect) != null) {
//                System.out.println("unclear");
                return false;
            }
        }
//        System.out.println("clear");

        return true;
    }


    private List<Node> findPlayerPath(CollisionManager colManager) {
        int maxNodes = 7840;
        Node start = new Node((int) this.position.x, (int) this.position.y, null, 0, 0);
        Node goal = new Node((int) player.getPosition().x-8, (int) player.getPosition().y-8, null, 0, 0);

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
//                System.out.println("Pathfinding limit reached. Switching to roaming mode.");
                following = false;
                roaming = true;
                return null;
            }
        }

        return null;
    }



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

    private double distanceBetween(Node a, Node b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    private double heuristic(Node a, Node b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)); // Euclidean distance
    }

    //decides how many nodes get placed (ex. every 2 nodes), higher number = smoother movement (but leads to problems read bug sheet)
    int n = 5;
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


    private void updateColliders() {
        this.scanRange.setX(this.position.x - scanRange.getWidth() / 2f + 8);
        this.scanRange.setY(this.position.y - scanRange.getHeight() / 2f + 4);

        this.damageCollider.setX(this.position.x - 2);
        this.damageCollider.setY(this.position.y - 5);
    }

    ///end of movement

    private float attackDuration = 0.3f;
    private float attackTimeElapsed = 0f;

    public void attack(){
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

//        Sound attackSound = soundManager.getSound("enemyDeath_sfx");
//        attackSound.play(soundManager.getSfxVolume());

        player.takeDamage(0.25f);
        player.redEffectTime=0f;
        player.isRedEffectActive=true;
        player.applyKnockback(getPosition(),150);

//        System.out.println("restarted");
        hud.updateHearts(player.getHealth());
        if(player.getHealth()%1 ==0){
            player.respawn();
            setPosition(new Vector2(initialposx,initialposy));
            player.startFlickering(cooldownTime);
            roaming = true;
        }
    }

    public Rectangle getDamageCollider() {
        return damageCollider;
    }

    public Rectangle getScanRange() {
        return scanRange;
    }

    public List<Node> getCurrentPath() {
        return currentPath;
    }
}
