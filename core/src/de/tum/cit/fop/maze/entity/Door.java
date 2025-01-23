package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

public class Door {
    private RectangleMapObject colliderObject;
    private RectangleMapObject detectObject;
    private RectangleMapObject tileObject;
    private boolean open;
    private Texture closedTexture;
    private Texture openTexture;
    private Texture currentTexture;
    private int doorHealth;

    public Door(RectangleMapObject detectObject) {
        this.detectObject = detectObject;
        this.colliderObject = (RectangleMapObject) detectObject.getProperties().get("doorCollider");
        this.tileObject = (RectangleMapObject) detectObject.getProperties().get("door");
        this.open = false;
        this.closedTexture = new Texture(Gdx.files.internal("icons/lockedDoor.png"));
        this.openTexture = new Texture(Gdx.files.internal("icons/openDoor.png"));
        currentTexture = closedTexture;
        doorHealth = 2;
    }

    public Texture getCurrentTexture()  {
        return currentTexture;
    }

    public void setCurrentTexture(Texture texture)  {
        currentTexture = texture;
    }

    public RectangleMapObject getColliderObject() {
        return colliderObject;
    }

    public void setColliderObject(RectangleMapObject collider) {
        this.colliderObject = collider;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public RectangleMapObject getDetectObject() {
        return detectObject;
    }

    public void setDetectObject(RectangleMapObject detectObject) {
        this.detectObject = detectObject;
    }

    public RectangleMapObject getTileObject() {
        return tileObject;
    }

    public void setTileObject(RectangleMapObject tileObject) {
        this.tileObject = tileObject;
    }

    public Texture getClosedTexture() {
        return closedTexture;
    }

    public Texture getOpenTexture() {
        return openTexture;
    }

    public int getDoorHealth() {
        return doorHealth;
    }

    public void setDoorHealth(int doorHealth) {
        this.doorHealth = doorHealth;
    }
}
