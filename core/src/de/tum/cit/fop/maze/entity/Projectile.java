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
    private Rectangle bounds;
    private float speed;
    private boolean active;

    public Projectile(float x, float y, Vector2 direction, float speed) {
        this.position = new Vector2(x, y);
        this.velocity = direction.nor().scl(speed);
        this.bounds = new Rectangle(x, y, 8, 8); // Adjust size as needed
        this.speed = speed;
        this.active = true;
    }

    public void update(float delta) {
        if (!active) return;
        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position.x, position.y);

        // Deactivate if out of bounds
        if (position.x < 0 || position.y < 0 || position.x > 1920 || position.y > 1080) { // Adjust based on your map
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        Texture texture = new Texture("white.png");
        TextureRegion textureRegion = new TextureRegion(texture);
        if (active) {
            batch.draw(textureRegion, position.x, position.y, 8, 8);
        }
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void deactivate() {
        this.active = false;
    }
}
