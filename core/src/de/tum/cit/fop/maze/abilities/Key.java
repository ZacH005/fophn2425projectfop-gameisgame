package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.entity.Player;

public class Key extends Powerup    {
    private SoundManager soundManager;
    public Key(float x, float y, SoundManager soundManager) {
        super("Key",
                "Opens Doors",
                "icons/caraxe.png",
                x, y);
        this.soundManager = soundManager;
    }

    @Override
    public void applyEffect(Player player) {
        soundManager.playSound("mcCollectKey_sfx");
        player.setKeys(player.getKeys()+1);
    }
}
