package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.entity.Player;

public class Key extends Powerup    {
    public Key(float x, float y) {
        super("Key",
                "Opens Doors",
                "icons/key.png",
                x, y,
                Gdx.audio.newSound(Gdx.files.internal("music/pickupCoin.wav")));
    }

    @Override
    public void applyEffect(Player player) {
        player.setKeys(player.getKeys()+1);
    }
}
