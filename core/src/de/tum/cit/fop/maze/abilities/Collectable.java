package de.tum.cit.fop.maze.abilities;

import de.tum.cit.fop.maze.entity.Player;

public interface Collectable <T> {
    T pickUp();
    boolean checkPickUp(Player player);
    void applyEffect(Player player);
}
