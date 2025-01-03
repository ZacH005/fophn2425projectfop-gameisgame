package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entity.Animation;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.screens.GameScreen;

import java.io.Serializable;

public abstract class Powerup implements Serializable {
    private String name;
    private String description;
    private int charges;
    private Vector2 position;
    private transient Rectangle collider;
    private transient Animation animation;
    private transient Texture texture;
    private transient Player player;
    private boolean isEquipped;
    private transient Music pickUpSound;

    public Powerup(Player player, String name, String description, float x, float y) {
        this.player = player;
        this.name = name;
        this.description = description;
        this.charges = 3;
        this.position = new Vector2(x,y);
        this.texture = new Texture("icons/speedUp.png");
        this.animation = new Animation(new TextureRegion(texture),1,3f);
        this.collider = new Rectangle(position.x,position.y,16,16);
        this.isEquipped = false;
        this.pickUpSound = Gdx.audio.newMusic(Gdx.files.internal("music/powerUp.wav"));
    }

    public void initializeTransientFields(Player player) {
        this.player = player;
        this.texture = new Texture("icons/speedUp.png");
        this.animation = new Animation(new TextureRegion(texture), 1, 3f);
        this.collider = new Rectangle(position.x, position.y, 16, 16);
        this.pickUpSound = Gdx.audio.newMusic(Gdx.files.internal("music/powerUp.wav"));
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Rectangle getCollider() {
        return collider;
    }

    public void setCollider(Rectangle collider) {
        this.collider = collider;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Music getPickUpSound() {
        return pickUpSound;
    }

    public void setPickUpSound(Music pickUpSound) {
        this.pickUpSound = pickUpSound;
    }
}
