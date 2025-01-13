package de.tum.cit.fop.maze.entity;

import java.util.Objects;

public class Node {
    public int x, y;
    public Node parent;
    public double gCost, hCost, fCost;

    Node(int x, int y, Node parent, double gCost, double hCost) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
