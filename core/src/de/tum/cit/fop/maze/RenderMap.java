package de.tum.cit.fop.maze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class RenderMap extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture wallTexture, pathTexture;
    private HashMap<String, Integer> mapData;
    private int tileSize;

    public RenderMap() {
        this.tileSize = 32;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        wallTexture = new Texture("wallTile.png");
        pathTexture = new Texture("pathTile.png");

        //get the map data
        mapData = MapParser.parseMap("maps/level-1.properties");
    }

    @Override
    public void render() {
        batch.begin();

        //keysets cause they're important https://www.geeksforgeeks.org/hashmap-keyset-method-in-java/
        for (String element : mapData.keySet()) {
            //,element is the x,y. keyset is the set of all elements, then the elements are mapped to the 0 or 1
            String[] coords = element.split(","); // split xy
            int x = Integer.parseInt(coords[0]); //x value
            int y = Integer.parseInt(coords[1]); //y value
            int key = mapData.get(element);

            Texture textureToDraw;

            switch (key)    {
                case 0:
                    textureToDraw = wallTexture;
                    break;
                case 1:
                    textureToDraw = pathTexture;
                    break;
                default:
                    textureToDraw = pathTexture;
            }
            //should draw it using the SpriteBatch, on each coordinate for the tile size
            batch.draw(textureToDraw, x*tileSize, y*tileSize);
        }
        batch.end();
    }

    //basically just removes the assets
    @Override
    public void dispose() {
        batch.dispose();
        wallTexture.dispose();
        pathTexture.dispose();
    }
}

