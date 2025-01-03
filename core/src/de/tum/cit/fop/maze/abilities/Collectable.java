package de.tum.cit.fop.maze.abilities;

public interface Collectable <T> {
    T pickUp();
    boolean checkPickUp();
    void applyEffect();
}
