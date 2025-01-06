package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.screens.GameScreen;

public class SpeedUp extends Powerup  {
    public SpeedUp(float x, float y) {
        super(
                "SpeedUp",
                "fast powerup that makes you go reallly fast...",
                "icons/speedUp.png",
                x, y,
                Gdx.audio.newSound(Gdx.files.internal("music/powerUp.wav"))
        );
    }

    @Override
    public Powerup pickUp() {
        getPickUpSound().play();
        return this;
    }

    public boolean checkPickUp(Player player) {
        if (super.getCollider().overlaps(player.collider))
            setEquipped(true);
        return isEquipped();
    }

    public void applyEffect(Player player) {
        player.setSpeed(player.getSpeed()*1.50f);
        System.out.println("Picked up " + getName()+": " + getDescription());
    }
}