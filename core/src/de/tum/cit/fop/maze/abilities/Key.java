package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.entity.Player;

/**
 * The Key class represents a power-up that allows the player to open doors.
 * It extends the Powerup class and increases the player's key count when collected.
 */
public class Key extends Powerup {
    private SoundManager soundManager;

    /**
     * Constructs a new Key power-up at the specified position.
     *
     * @param x            The x-coordinate of the power-up's position.
     * @param y            The y-coordinate of the power-up's position.
     * @param soundManager The sound manager for playing sound effects.
     */
    public Key(float x, float y, SoundManager soundManager) {
        super("Key",
                "Opens Doors",
                "icons/caraxe.png",
                x, y);
        this.soundManager = soundManager;
    }

    /**
     * Applies the effect of the Key power-up to the player.
     * This method plays a sound effect and increases the player's key count by one.
     *
     * @param player The player to whom the effect is applied.
     */
    @Override
    public void applyEffect(Player player) {
        soundManager.playSound("mcCollectKey_sfx");
        player.setKeys(player.getKeys() + 1);
    }
}