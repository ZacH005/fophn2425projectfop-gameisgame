package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.graphics.Texture;
import de.tum.cit.fop.maze.entity.Player;

public abstract class GameEffect {
    private String name;
    private String description;
    private Texture texture;

    public GameEffect(String name, String description, String texturePath) {
        this.name = name;
        this.description = description;
        this.texture = new Texture(texturePath);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Texture getTexture() {
        return texture;
    }
}

