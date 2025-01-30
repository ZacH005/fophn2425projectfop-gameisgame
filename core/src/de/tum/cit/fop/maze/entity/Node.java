package de.tum.cit.fop.maze.entity;

import java.util.Objects;

/**
 * The Node class represents a point in the grid used for pathfinding algorithms such as A*.
 * It contains information about the node's coordinates, parent node, and costs associated with the pathfinding process.
 */
public class Node {
    public int x, y;
    public Node parent;
    public double gCost, hCost, fCost;

    /**
     * Constructs a new Node with specified coordinates, parent, and costs.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param parent The parent node from which this node was reached.
     * @param gCost The cost from the start node to this node.
     * @param hCost The heuristic cost estimate from this node to the goal.
     */
    Node(int x, int y, Node parent, double gCost, double hCost) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    /**
     * Checks if this node is equal to another object.
     * Two nodes are considered equal if they have the same x and y coordinates.
     *
     * @param obj The object to compare this node with.
     * @return True if the nodes are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    /**
     * Returns a hash code for this node based on its x and y coordinates.
     *
     * @return The hash code for the node.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
