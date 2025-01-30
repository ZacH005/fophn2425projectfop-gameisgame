package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

/**
 * Represents a door entity in the game.
 * The door has a collider, detection area, and tile representation.
 * It can be opened and has a health attribute.
 */
public class Door {
    private RectangleMapObject colliderObject;
    private RectangleMapObject detectObject;
    private RectangleMapObject tileObject;
    private boolean open;
    private Texture closedTexture;
    private Texture openTexture;
    private Texture currentTexture;
    private int doorHealth;

    /**
     * Constructs a Door object with a detection area.
     *
     * @param detectObject The RectangleMapObject representing the detection area of the door.
     */
    public Door(RectangleMapObject detectObject) {
        this.detectObject = detectObject;
        this.colliderObject = (RectangleMapObject) detectObject.getProperties().get("doorCollider");
        this.tileObject = (RectangleMapObject) detectObject.getProperties().get("door");
        this.open = false;
        this.closedTexture = new Texture(Gdx.files.internal("icons/lockedDoor.png"));
        this.openTexture = new Texture(Gdx.files.internal("icons/openDoor.png"));
        this.currentTexture = closedTexture;
        this.doorHealth = 2;
    }

    /**
     * Gets the current texture of the door.
     *
     * @return The current texture.
     */
    public Texture getCurrentTexture() {
        return currentTexture;
    }

    /**
     * Sets the current texture of the door.
     *
     * @param texture The texture to set.
     */
    public void setCurrentTexture(Texture texture) {
        currentTexture = texture;
    }

    /**
     * Gets the collider object of the door.
     *
     * @return The collider object.
     */
    public RectangleMapObject getColliderObject() {
        return colliderObject;
    }

    /**
     * Sets the collider object of the door.
     *
     * @param collider The collider object to set.
     */
    public void setColliderObject(RectangleMapObject collider) {
        this.colliderObject = collider;
    }

    /**
     * Checks if the door is open.
     *
     * @return True if the door is open, false otherwise.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Sets the open state of the door.
     *
     * @param open True to open the door, false to close it.
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * Gets the detection object of the door.
     *
     * @return The detection object.
     */
    public RectangleMapObject getDetectObject() {
        return detectObject;
    }

    /**
     * Sets the detection object of the door.
     *
     * @param detectObject The detection object to set.
     */
    public void setDetectObject(RectangleMapObject detectObject) {
        this.detectObject = detectObject;
    }

    /**
     * Gets the tile object of the door.
     *
     * @return The tile object.
     */
    public RectangleMapObject getTileObject() {
        return tileObject;
    }

    /**
     * Sets the tile object of the door.
     *
     * @param tileObject The tile object to set.
     */
    public void setTileObject(RectangleMapObject tileObject) {
        this.tileObject = tileObject;
    }

    /**
     * Gets the closed texture of the door.
     *
     * @return The closed texture.
     */
    public Texture getClosedTexture() {
        return closedTexture;
    }

    /**
     * Gets the open texture of the door.
     *
     * @return The open texture.
     */
    public Texture getOpenTexture() {
        return openTexture;
    }

    /**
     * Gets the health of the door for the breaking animations to work after multiple hits.
     *
     * @return The door's health.
     */
    public int getDoorHealth() {
        return doorHealth;
    }

    /**
     * Sets the health of the door.
     *
     * @param doorHealth The health value to set.
     */
    public void setDoorHealth(int doorHealth) {
        this.doorHealth = doorHealth;
    }
}
