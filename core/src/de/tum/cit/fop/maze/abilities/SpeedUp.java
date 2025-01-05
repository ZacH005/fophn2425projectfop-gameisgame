package de.tum.cit.fop.maze.abilities;

import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.screens.GameScreen;

public class SpeedUp extends Powerup implements Collectable <Powerup>  {
    public SpeedUp(Player player, String name, String description, float x, float y) {
        super( player, name, description, x, y);
    }

    @Override
    public Powerup pickUp() {
        getPickUpSound().play();
        return this;
    }

    @Override
    public boolean checkPickUp() {
        if (super.getCollider().overlaps(getPlayer().collider))
            setEquipped(true);
        return isEquipped();
    }

    @Override
    public void applyEffect() {
        getPlayer().setSpeed(getPlayer().getSpeed()*1.50f);
        System.out.println("Picked up " + getName()+": " + getDescription());
    }
}