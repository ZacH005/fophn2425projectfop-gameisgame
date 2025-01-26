package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.screens.GameScreen;

public class SpeedUp extends Powerup  {
    private SoundManager soundManager;
    public SpeedUp(float x, float y, SoundManager soundManager) {
        super(
                "SpeedUp",
                "fast powerup that makes you go reallly fast...",
                "icons/speedUp.png",
                x, y
        );
        this.soundManager = soundManager;
    }

    public void applyEffect(Player player) {
        player.setSpeed(player.getSpeed()*1.50f);
        soundManager.playSound("mcCollectSpeedUp_sfx");
        System.out.println("Picked up " + getName()+": " + getDescription());
    }
}