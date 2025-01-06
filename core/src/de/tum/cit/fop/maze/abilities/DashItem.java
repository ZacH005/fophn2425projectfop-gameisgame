package de.tum.cit.fop.maze.abilities;

import de.tum.cit.fop.maze.entity.Player;

public class DashItem extends Item  {

    public DashItem(String name, String description, String texturePath) {
        super(name, description, texturePath);
    }

    @Override
    public Item pickUp() {
        return null;
    }

    @Override
    public boolean checkPickUp(Player player) {
        return false;
    }

    @Override
    public void applyEffect(Player player) {
        
    }
}
