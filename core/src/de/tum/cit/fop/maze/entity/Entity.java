package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.abilities.Powerup;

import java.util.List;

/**
 * Represents an entity in the game, defining common behaviors such as health management,
 * position control, inventory handling, and state persistence.
 */
public interface Entity {

    /**
     * Restores health to the entity.
     */
    void heal();

    /**
     * Reduces the entity's health by the specified amount.
     * @param amount The amount of damage to take.
     */
    void takeDamage(float amount);

    /**
     * Gets the current health of the entity.
     * @return The current health value.
     */
    float getHealth();

    /**
     * Sets the health of the entity.
     * @param health The new health value.
     */
    void setHealth(float health);

    /**
     * Gets the current position of the entity.
     * @return The position as a {@link Vector2}.
     */
    Vector2 getPosition();

    /**
     * Sets the position of the entity.
     * @param position The new position as a {@link Vector2}.
     */
    void setPosition(Vector2 position);

    /**
     * Checks if the entity is following another entity.
     * @return True if the entity is following, false otherwise.
     */
    boolean isFollowing();

    /**
     * Sets whether the entity is following another entity.
     * @param following True to make the entity follow, false otherwise.
     */
    void setFollowing(boolean following);

    /**
     * Gets the armor value of the entity.
     * @return The armor value.
     */
    int getArmor();

    /**
     * Sets the armor value of the entity.
     * @param armor The new armor value.
     */
    void setArmor(int armor);

    /**
     * Retrieves the list of power-ups that the entity possesses.
     * @return A list of power-ups.
     */
    List<Powerup> getPowerUps();

    /**
     * Sets the list of power-ups for the entity.
     * @param powerUps The new list of power-ups.
     */
    void setPowerUps(List<Powerup> powerUps);

    /**
     * Gets the amount of money the entity has.
     * @return The current money amount.
     */
    int getMoney();

    /**
     * Sets the money amount for the entity.
     * @param money The new money amount.
     */
    void setMoney(int money);

    /**
     * Saves the current state of the entity to a file.
     * @param filename The name of the file to save to.
     */
    void saveState(String filename);

    /**
     * Loads the entity's state from a file.
     * @param filename The name of the file to load from.
     */
    void loadState(String filename);
}
