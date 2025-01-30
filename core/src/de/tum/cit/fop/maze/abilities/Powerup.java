package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entity.Player;

/**
 * The Powerup class represents an abstract power-up that can be collected by the player.
 * It extends the GameEffect class and implements the Collectable interface.
 */
public abstract class Powerup extends GameEffect implements Collectable<Powerup> {
    private Vector2 position;
    private Rectangle collider;
    private boolean isEquipped;

    /**
     * Constructs a new Powerup with the specified name, description, texture path, and position.
     *
     * @param name        The name of the power-up.
     * @param description The description of the power-up.
     * @param texturePath The path to the texture representing the power-up.
     * @param x           The x-coordinate of the power-up's position.
     * @param y           The y-coordinate of the power-up's position.
     */
    public Powerup(String name, String description, String texturePath, float x, float y) {
        super(name, description, texturePath);
        this.position = new Vector2(x, y);
        this.collider = new Rectangle(x, y, 16, 16);
        this.isEquipped = false;
    }

    /**
     * Checks if the power-up is equipped by the player.
     *
     * @return True if the power-up is equipped, false otherwise.
     */
    public boolean isEquipped() {
        return isEquipped;
    }

    /**
     * Sets the equipped status of the power-up.
     *
     * @param equipped The equipped status to set.
     */
    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    /**
     * Gets the position of the power-up.
     *
     * @return The position of the power-up as a Vector2.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Gets the collider of the power-up.
     *
     * @return The collider of the power-up as a Rectangle.
     */
    public Rectangle getCollider() {
        return collider;
    }

    /**
     * Picks up the power-up.
     *
     * @return The power-up itself.
     */
    @Override
    public Powerup pickUp() {
        return this;
    }

    /**
     * Checks if the power-up can be picked up by the player.
     *
     * @param player The player attempting to pick up the power-up.
     * @return True if the power-up can be picked up, false otherwise.
     */
    @Override
    public boolean checkPickUp(Player player) {
        if (collider.overlaps(player.collider))
            setEquipped(true);
        return isEquipped();
    }
}