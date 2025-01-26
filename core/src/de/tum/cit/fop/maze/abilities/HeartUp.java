package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.entity.Player;

public class HeartUp extends Powerup    {
    private SoundManager soundManager;
    public HeartUp(float x, float y, SoundManager soundManager) {
        super("HeartUp",
                "Gives you one extra life",
                "icons/heartPickUp.png",
                x, y);
        this.soundManager = soundManager;
    }

    @Override
    public void applyEffect(Player player) {
        soundManager.playSound("mcCollectHeart_sfx");
        player.heal();
        System.out.println("Picked up " + getName()+": " + getDescription());
    }
}
