package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.screens.GameScreen;

/**
 * The SpeedUp class represents a power-up that increases the player's speed.
 * It extends the Powerup class and applies a speed boost effect to the player when collected.
 */
public class SpeedUp extends Powerup {
    private SoundManager soundManager;

    /**
     * Constructs a new SpeedUp power-up at the specified position.
     *
     * @param x            The x-coordinate of the power-up's position.
     * @param y            The y-coordinate of the power-up's position.
     * @param soundManager The sound manager for playing sound effects.
     */
    public SpeedUp(float x, float y, SoundManager soundManager) {
        super(
                "SpeedUp",
                "fast powerup that makes you go really fast...",
                "icons/speedUp.png",
                x, y
        );
        this.soundManager = soundManager;
    }

    /**
     * Applies the effect of the SpeedUp power-up to the player.
     * This method increases the player's speed by 50% and plays a sound effect.
     *
     * @param player The player to whom the effect is applied.
     */
    @Override
    public void applyEffect(Player player) {
        player.setSpeed(player.getSpeed() * 1.50f);
        soundManager.playSound("mcCollectSpeedUp_sfx");
        System.out.println("Picked up " + getName() + ": " + getDescription());
    }
}