package de.tum.cit.fop.maze.abilities;

import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.screens.GameScreen;

public class SpeedUp extends Powerup implements Collectable   {
    public SpeedUp(Player player, String name, String description, float x, float y) {
        super( player, name, description, x, y);
    }

    @Override
    public Object pickUp() {
        getPlayer().getPowerUps().add(this);
        this.applyEffect();
        System.out.println(getPlayer().getPowerUps());
        getPickUpSound().play();
        return this;
    }

    @Override
    public boolean checkPickUp() {
        if (super.getCollider().overlaps(getPlayer().collider)) {
            this.pickUp();
            setEquipped(true);
        }
        return isEquipped();
    }

    @Override
    public void applyEffect() {
        getPlayer().setSpeed(getPlayer().getSpeed()*1.50f);
        System.out.println("Picked up " + getName()+": " + getDescription());
    }
}
