package de.tum.cit.fop.maze.shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Light {
    public Vector2 position;
    public float radius;

    public Light(Vector2 position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

}

