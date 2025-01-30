package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.entity.Player;

/**
 * The HeartUp class represents a power-up that gives the player an extra life.
 * It extends the Powerup class and applies a healing effect to the player when collected.
 */
public class HeartUp extends Powerup {
    private SoundManager soundManager;

    /**
     * Constructs a new HeartUp power-up at the specified position.
     *
     * @param x            The x-coordinate of the power-up's position.
     * @param y            The y-coordinate of the power-up's position.
     * @param soundManager The sound manager for playing sound effects.
     */
    public HeartUp(float x, float y, SoundManager soundManager) {
        super("HeartUp",
                "Gives you one extra life",
                "icons/heartPickUp.png",
                x, y);
        this.soundManager = soundManager;
    }

    /**
     * Applies the effect of the HeartUp power-up to the player.
     * This method plays a sound effect and heals the player by one life.
     *
     * @param player The player to whom the effect is applied.
     */
    @Override
    public void applyEffect(Player player) {
        soundManager.playSound("mcCollectHeart_sfx");
        player.heal();
        System.out.println("Picked up " + getName() + ": " + getDescription());
    }
}