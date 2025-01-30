package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.Random;

/**
 * Manages screen shake effects for the game.
 * This class simulates a screen shake by applying random offsets to the camera's position.
 */
public class ScreenShake {
    private float duration;
    private float intensity;
    private Random random;
    private float originalX;
    private float originalY;

    /**
     * Constructs a new ScreenShake instance and initializes the random number generator.
     */
    public ScreenShake() {
        random = new Random();
    }

    /**
     * Starts the screen shake effect with the specified duration and intensity.
     *
     * @param duration  the duration of the shake effect in seconds.
     * @param intensity the intensity of the shake effect.
     */
    public void startShake(float duration, float intensity) {
        this.duration = duration;
        this.intensity = intensity;
    }

    /**
     * Updates the screen shake effect and applies the shake to the camera.
     *
     * @param delta   the time elapsed since the last frame in seconds.
     * @param camera  the camera to which the shake effect is applied.
     */
    public void update(float delta, OrthographicCamera camera) {
        if (duration > 0) {
            duration -= delta;

            float offsetX = (random.nextFloat() - 0.5f) * 2 * intensity;
            float offsetY = (random.nextFloat() - 0.5f) * 2 * intensity;

            camera.position.set(originalX + offsetX, originalY + offsetY, 0);
            camera.update();
        } else {
            camera.position.set(originalX, originalY, 0);
            camera.update();
        }
    }

    /**
     * Sets the original position of the camera before the shake effect is applied.
     *
     * @param x the x-coordinate of the original position.
     * @param y the y-coordinate of the original position.
     */
    public void setOriginalPosition(float x, float y) {
        this.originalX = x;
        this.originalY = y;
    }
}