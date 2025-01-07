package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.entity.Player;

public class HeartUp extends Powerup    {
    public HeartUp(float x, float y) {
        super("HeartUp",
                "Gives you one extra life",
                "icons/heartPickUp.png",
                x, y,
                Gdx.audio.newSound(Gdx.files.internal("music/game_sfx/MC_sfx/power up 2.wav")));
    }

    @Override
    public void applyEffect(Player player) {
        player.heal();
        System.out.println("Picked up " + getName()+": " + getDescription());
    }
}
