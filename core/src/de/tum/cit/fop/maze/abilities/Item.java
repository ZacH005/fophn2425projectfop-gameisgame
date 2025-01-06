package de.tum.cit.fop.maze.abilities;

import de.tum.cit.fop.maze.entity.Player;

public abstract class Item extends GameEffect implements Collectable<Item>  {
    private boolean isUnlocked;

    public Item(String name, String description, String texturePath) {
        super(name, description, texturePath);
        isUnlocked = false;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void unlock()    {
        this.isUnlocked = true;
    }
}
