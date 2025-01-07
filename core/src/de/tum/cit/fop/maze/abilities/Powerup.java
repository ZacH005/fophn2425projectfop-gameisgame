package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entity.Player;

public abstract class Powerup extends GameEffect implements Collectable<Powerup> {
    private Vector2 position;
    private Rectangle collider;
    private transient Sound pickUpSound;
    private boolean isEquipped;

    public Powerup(String name, String description, String texturePath, float x, float y, Sound pickUpSound) {
        super(name, description, texturePath);
        this.position = new Vector2(x, y);
        this.collider = new Rectangle(x, y, 16, 16);
        this.pickUpSound = pickUpSound;
        this.isEquipped = false;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    public Sound getPickUpSound() {
        return pickUpSound;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getCollider() {
        return collider;
    }

    public Powerup pickUp() {
        pickUpSound.play();
        return this;
    }

    public boolean checkPickUp(Player player) {
        if (collider.overlaps(player.collider))
            setEquipped(true);
        return isEquipped();
    }
}
