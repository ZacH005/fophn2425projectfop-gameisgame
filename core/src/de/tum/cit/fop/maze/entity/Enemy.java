package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.Powerup;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Enemy implements Entity {
    public Vector2 position;
    private Texture texture;
    private Animation animation;
    public Rectangle scanRange;
    private Player player;
    private boolean following;
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
    private int initialposx;
    private int initialposy;

    public Enemy(int x, int y,Player player,HUD hud,SoundManager soundManager) {
        this.player = player;
        position = new Vector2(x,y);
        initialposx = x;
        initialposy = y;
        texture=new Texture("TiledMaps/SlimeA.png");
        animation=new Animation(new TextureRegion(texture),16,3f);
         scanrangewidth = 100;
         scanrangeheight = 100;
        scanRange = new Rectangle(position.x-scanrangewidth/2f+8,position.y-scanrangeheight/2f+4,scanrangeheight,scanrangewidth);
        damageCollider = new Rectangle(position.x+6,position.y+3,2,2);
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
    }

    public void update(float delta){
        animation.update(delta);
        checkfollows();
        checkDamaging(delta);
    }
    public TextureRegion getEnemy(){
        return animation.getFrame();
    }

    @Override
    public void takeDamage() {

    }

    @Override
    public int getHealth() {
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
    public void setHealth(int health) {

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
        damageCollider.setPosition(position.x+6,position.y+3);
    }
    private void checkDamaging(float delta) {
        if (damageCollider.overlaps(player.collider)) {
            // only process if cooldown has passed
            if (TimeUtils.nanoTime() - lastDamageTime >= cooldownTime * 1000000000L) {
                // proceed with damage logic
                player.takeDamage();
                player.respawn();// Reset player position
                setPosition(new Vector2(initialposx,initialposy));
                System.out.println("restarted");
                player.startFlickering(cooldownTime);
                hud.updateHearts(player.getHealth());
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
    public void checkfollows() {
        // calculates x and y distance
        float distanceX = player.getPosition().x - this.position.x;
        float distanceY = player.getPosition().y - this.position.y;

        // calculate the total distance (Pythagoras)
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        // check if the player is in range
        if (this.scanRange.overlaps(player.collider)) {
            if (!following) {
                following = true; // Start following
//              chaseMusic.play(); // Play suspenseful music
                soundManager.onGameStateChange(chaseState);
            }
        }

        if (following) {
            // if close enough or escaped, stop moving
            if (distance > 100.0f) { // Stop when within 5 units (captured) or when more than 100 units (escaped)
                following = false;
//              chaseMusic.stop();
                soundManager.onGameStateChange(mainState);
                return;
            }

            // moves the enemy towards the player
            float speed = 90 * Gdx.graphics.getDeltaTime(); // Speed in units per second
            float directionX = distanceX / distance; // Basic Vector Math to get unit vector direction without any extra magnitude
            float directionY = distanceY / distance;

            // Update enemy position
            this.position.x += directionX * speed;
            this.position.y += directionY * speed;

            // Update scan range based on the enemy's new position
            this.scanRange.setX(this.position.x - scanRange.getWidth()/2f+8);
            this.scanRange.setY(this.position.y - scanRange.getHeight()/2f+4);

            this.damageCollider.setX(this.position.x +6);
            this.damageCollider.setY(this.position.y + 3);
        }
    }
}
