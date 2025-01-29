package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle collider;
    private float speed;
    private boolean active;

    /**
     * Constructs a new Projectile with the specified position, direction, and speed.
     *
     * @param x         The initial x-coordinate of the projectile.
     * @param y         The initial y-coordinate of the projectile.
     * @param direction The direction vector of the projectile.
     * @param speed     The speed of the projectile.
     */
    public Projectile(float x, float y, Vector2 direction, float speed) {
        this.position = new Vector2(x, y);
        this.velocity = direction.nor().scl(speed);
        this.collider = new Rectangle(x, y, 8, 8);
        this.speed = speed;
        this.active = true;
    }

    /**
     * Updates the projectile's position and checks if it is out of bounds.
     *
     * @param delta The time elapsed since the last update.
     */
    public void update(float delta) {
        if (!active) return;
        position.add(velocity.x * delta, velocity.y * delta);
        collider.setPosition(position.x, position.y);

        if (position.x < 0 || position.y < 0 || position.x > 1920 || position.y > 1080) {
            active = false;
        }
    }

    /**
     * Renders the projectile on the screen if it is active.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    public void render(SpriteBatch batch) {
        Texture texture = new Texture("white.png");
        TextureRegion textureRegion = new TextureRegion(texture);
        if (active) {
            batch.draw(textureRegion, position.x, position.y, 8, 8);
        }
    }

    /**
     * Checks if the projectile is active.
     *
     * @return True if the projectile is active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the collider of the projectile.
     *
     * @return The Rectangle representing the projectile's collider.
     */
    public Rectangle getCollider() {
        return collider;
    }

    /**
     * Deactivates the projectile.
     */
    public void deactivate() {
        this.active = false;
    }
}