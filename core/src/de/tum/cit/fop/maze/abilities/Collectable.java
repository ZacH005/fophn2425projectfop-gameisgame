package de.tum.cit.fop.maze.abilities;

import de.tum.cit.fop.maze.entity.Player;

/**
 * The Collectable interface defines the behavior of objects that can be collected by the player.
 *
 * @param <T> The type of the object that is collected.
 */
public interface Collectable<T> {

    /**
     * Picks up the collectable object.
     *
     * @return The collected object of type T.
     */
    T pickUp();

    /**
     * Checks if the collectable object can be picked up by the player.
     *
     * @param player The player attempting to pick up the object.
     * @return True if the object can be picked up, false otherwise.
     */
    boolean checkPickUp(Player player);

    /**
     * Applies the effect of the collectable object to the player.
     *
     * @param player The player to whom the effect is applied.
     */
    void applyEffect(Player player);
}