package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameoverScreen implements Screen {
    private ScreenViewport viewport;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public GameoverScreen(SpriteBatch sb) {
        this.spriteBatch = sb;
        viewport = new ScreenViewport();
        font = new BitmapFont();  // Default font
        font.setColor(Color.WHITE);  // Set text color to white
    }

    @Override
    public void show() {
        // Setup the screen when it is shown
    }

    @Override
    public void render(float delta) {
        // Clear the screen with black color
        spriteBatch.begin();
        ScreenUtils.clear(Color.BLACK);

        // Draw "GAME OVER" text in the center of the screen
        font.getData().setScale(2); // Increase font size
        String gameOverText = "GAME OVER";
        float textWidth = font.getRegion().getRegionWidth() * font.getScaleX();
        float textHeight = font.getRegion().getRegionHeight() * font.getScaleY();
        float x = (viewport.getWorldWidth() - textWidth) / 2;
        float y = (viewport.getWorldHeight() + textHeight) / 2;

        font.draw(spriteBatch, gameOverText, x, y);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport size if the window is resized
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Handle pause state
    }

    @Override
    public void resume() {
        // Handle resume state
    }

    @Override
    public void hide() {
        // Handle hiding the screen
    }

    @Override
    public void dispose() {
        // Dispose resources when done
        font.dispose();
    }
}
