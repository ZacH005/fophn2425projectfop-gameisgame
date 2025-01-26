package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.Random;

public class ScreenShake {
    private float duration; // Remaining time for the shake
    private float intensity; // Intensity of the shake
    private Random random;
    private float originalX;
    private float originalY;

    public ScreenShake() {
        random = new Random();
    }

    public void startShake(float duration, float intensity) {
        this.duration = duration;
        this.intensity = intensity;
    }

    public void update(float delta, OrthographicCamera camera) {
        if (duration > 0) {
            duration -= delta;

            // Generate random offsets for the shake
            float offsetX = (random.nextFloat() - 0.5f) * 2 * intensity;
            float offsetY = (random.nextFloat() - 0.5f) * 2 * intensity;

            // Apply the shake to the camera
            camera.position.set(originalX + offsetX, originalY + offsetY, 0);
            camera.update();
        } else {
            // Reset camera position after the shake
            camera.position.set(originalX, originalY, 0);
            camera.update();
        }
    }

    public void setOriginalPosition(float x, float y) {
        this.originalX = x;
        this.originalY = y;
    }
}

