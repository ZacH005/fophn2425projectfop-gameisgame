package de.tum.cit.fop.maze.abilities;

import com.badlogic.gdx.graphics.Texture;
import de.tum.cit.fop.maze.entity.Player;

/**
 * The GameEffect class represents an abstract game effect that can be applied to the player.
 * It contains a name, description, and texture for the effect.
 */
public abstract class GameEffect {
    private String name;
    private String description;
    private Texture texture;

    /**
     * Constructs a new GameEffect with the specified name, description, and texture path.
     *
     * @param name        The name of the effect.
     * @param description The description of the effect.
     * @param texturePath The path to the texture representing the effect.
     */
    public GameEffect(String name, String description, String texturePath) {
        this.name = name;
        this.description = description;
        this.texture = new Texture(texturePath);
    }

    /**
     * Gets the name of the effect.
     *
     * @return The name of the effect.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the effect.
     *
     * @return The description of the effect.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the texture representing the effect.
     *
     * @return The texture of the effect.
     */
    public Texture getTexture() {
        return texture;
    }
}