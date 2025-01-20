package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.Powerup;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;

import java.util.*;


public class Enemy implements Entity {
    public boolean isDead = false;
    public Vector2 position;
    private Texture texture;
    private Animation animation;
    public Rectangle scanRange;
    private Player player;
    private boolean following;
    private boolean roaming;

    private Vector2 roamingTarget;
    private long lastRoamUpdateTime = 0;
    private long roamDelay = 200;

    public Rectangle damageCollider;
//    private Music chaseMusic;;
    ///sound manager stuff.
    private SoundManager soundManager;
    private Map<String,Integer> chaseState = new HashMap<String,Integer>();
    private Map<String,Integer> mainState = new HashMap<String,Integer>();

    private float lastDamageTime; // To track the last damage time
    private float cooldownTime=2f;
    private HUD hud;
    int scanrangewidth;
    int scanrangeheight;
    //for some reason if I just create a Vector2 initial position it does not work dk why
    private float initialposx;
    private float initialposy;
    private int health;

    private float movementSpeed;
    private List<Node> currentPath;
    private Vector2 savedVector;

    public Enemy(float x, float y,Player player,HUD hud,SoundManager soundManager) {
        this.player = player;
        position = new Vector2(x,y);
        initialposx = x;
        initialposy = y;
        texture=new Texture("TiledMaps/SlimeA.png");
        animation=new Animation(new TextureRegion(texture),16,3f);
        scanrangewidth = 100;
        scanrangeheight = 100;
        scanRange = new Rectangle(position.x-scanrangewidth/2f+8,position.y-scanrangeheight/2f+4,scanrangeheight,scanrangewidth);
        damageCollider = new Rectangle(position.x-2,position.y-5,20,20);
        health = 3;

        movementSpeed = 120 * Gdx.graphics.getDeltaTime(); // uses delta time to allow for frames to be bad

//        chaseMusic = Gdx.audio.newMusic(Gdx.files.internal("ChaseMusic.mp3")); // Replace with your music file
//        chaseMusic.setLooping(true);
        this.soundManager=soundManager;
        chaseState.put("crackles",0);
        chaseState.put("wind",1);
        chaseState.put("piano",1);
        chaseState.put("strings",0);
        chaseState.put("pad",0);
        chaseState.put("drums",1);
        chaseState.put("bass",1);

        mainState.put("crackles",1);
        mainState.put("wind",1);
        mainState.put("piano",1);
        mainState.put("strings",0);
        mainState.put("pad",1);
        mainState.put("drums",0);
        mainState.put("bass",1);

        this.hud=hud;

        roaming = true;
    }

    public void update(float delta, CollisionManager colManager){
        if(!isDead){
            animation.update(delta);
            updateMovement(colManager);
            checkDamaging(delta);
        }
    }
    public TextureRegion getEnemy(){
        return animation.getFrame();
    }

    @Override
    public void heal() {

    }

    @Override
    public void takeDamage(float amount) {
    }


    @Override
    public float getHealth() {
        return 0;
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
    public void takedamage(int amount){
        health -= amount;
        if (health <= 0) {
            isDead = true;
            System.out.println("Enemy defeated!");
        }
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
    long lastMovementUpdateTime = 0, movementDelay = 500;

    public void updateMovement(CollisionManager colManager) {
        float distanceX = player.getPosition().x - this.position.x;
        float distanceY = player.getPosition().y - this.position.y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (scanRange.overlaps(player.collider) && !following && hasClearLineOfSight(position, player.getPosition(), colManager)) {
//        if (scanRange.overlaps(player.collider) && !following) {
            roaming = false;
            following = true;
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

            this.position.x += directionX * movementSpeed;
            this.position.y += directionY * movementSpeed;
            updateColliders();
        } else if (following) {

            //decides how long until the path si recalculated (rn 500 millisec)
            if (TimeUtils.millis() - lastMovementUpdateTime >= movementDelay) {
                currentPath = findPlayerPath(colManager);
                lastMovementUpdateTime = TimeUtils.millis();
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

                this.position.x += directionX * movementSpeed;
                this.position.y += directionY * movementSpeed;

                updateColliders();

                //makes sure that it updates once the node is reached
                if (length < movementSpeed) {
                    currentPath.remove(0);
                }
            }
        }

        //make new vector every update
        //always update the player movement
        //load the vector

        // Direction vectors
        Vector2[] directions = {
                new Vector2(0, 1),   // Up
                new Vector2(0, -1),  // Down
                new Vector2(-1, 0),  // Left
                new Vector2(1, 0)    // Right
        };

        if (roaming) {
            //finds random positon
            if (TimeUtils.millis() - lastMovementUpdateTime >= movementDelay) {
                movement = (int)(4 * Math.random());
                lastMovementUpdateTime = TimeUtils.millis();
            }
            //constatnyly move in that directoin
            float newX = this.position.x + (directions[movement].x * movementSpeed * 0.6f);
            float newY = this.position.y + (directions[movement].y * movementSpeed * 0.6f);

            Vector2 newPosition = new Vector2(newX, newY);

            int retryCount = 0;
            //collision check
            while (!hasClearLineOfSight(this.position, newPosition, colManager) && retryCount < 5) {
                movement = (int)(4 * Math.random());

                //check again
                newX = this.position.x + (directions[movement].x * movementSpeed * 0.6f);
                newY = this.position.y + (directions[movement].y * movementSpeed * 0.6f);

                newPosition = new Vector2(newX, newY);
                retryCount++;
            }

            if (retryCount < 5) {
                this.position.x = newX;
                this.position.y = newY;

                updateColliders();
            } else {
                System.out.println("stuck");
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
        Node start = new Node((int) (this.position.x), (int) (this.position.y), null, 0, 0);
        Node goal = new Node((int) (player.getPosition().x-7), (int) (player.getPosition().y-7), null, 0, 0);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Set<Node> closedSet = new HashSet<>();

        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

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



    public void attack(){
        player.takeDamage(0.25f);
        player.redEffectTime=0f;
        player.isRedEffectActive=true;
        player.applyKnockback(getPosition(),150);

        System.out.println("restarted");
        hud.updateHearts(player.getHealth());
        if(player.getHealth()%1==0){
            player.respawn();
            setPosition(new Vector2(initialposx,initialposy));
            player.startFlickering(cooldownTime);
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
