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

    public Projectile(float x, float y, Vector2 direction, float speed) {
        this.position = new Vector2(x, y);
        this.velocity = direction.nor().scl(speed);
        this.collider = new Rectangle(x, y, 8, 8);
        this.speed = speed;
        this.active = true;
    }

    public void update(float delta) {
        if (!active) return;
        position.add(velocity.x * delta, velocity.y * delta);
        collider.setPosition(position.x, position.y);

        if (position.x < 0 || position.y < 0 || position.x > 1920 || position.y > 1080) {
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

    public Rectangle getCollider() {
        return collider;
    }

    public void deactivate() {
        this.active = false;
    }
}
