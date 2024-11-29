package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;

public class RenderMap  {
    private SpriteBatch batch;
    private final Texture wallTexture, pathTexture, entryTexture, enemyTexture, exitTexture, keyTexture, trapTexture;
    private HashMap<String, Integer> mapData;
    private int tileSize;

    public RenderMap() {
        this.tileSize = 32;
        batch = new SpriteBatch();
        wallTexture = new Texture("wallTile.png");
        pathTexture = new Texture("pathTile.png");
        enemyTexture = new Texture("enemyTile.png");
        entryTexture = new Texture("entryTile.png");
        exitTexture = new Texture("exitTile.png");
        keyTexture = new Texture("keyTile.png");
        trapTexture = new Texture("trapTile.png");

        //get the map data
        mapData = MapParser.parseMap("maps/level-3.properties");
    }

    public void render() {
        batch.begin();

        Set<Integer> xMax = new HashSet<>();
        Set<Integer> yMax = new HashSet<>();

        for (String element : mapData.keySet()) {
            String[] coords = element.split(","); // split xy
            if (coords.length <= 1)
                continue;
            int x = Integer.parseInt(coords[0]); //x value
            int y = Integer.parseInt(coords[1]); //y value

            xMax.add(x);
            yMax.add(x);
        }

        for (int x = Collections.min(xMax); x <= Collections.max(xMax); x++) {
            for (int y = Collections.min(yMax); y <= Collections.max(yMax); y++) {
                batch.draw(pathTexture, x * tileSize, y * tileSize);
            }
        }

        //keysets cause they're important https://www.geeksforgeeks.org/hashmap-keyset-method-in-java/
        for (String element : mapData.keySet()) {
            //,element is the x,y. keyset is the set of all elements, then the elements are mapped to the 0 or 1
            String[] coords = element.split(","); // split xy
            if (coords.length <= 1)
                continue;
            int x = Integer.parseInt(coords[0]); //x value
            int y = Integer.parseInt(coords[1]); //y value
            int key = mapData.get(element);

            Texture textureToDraw = switch (key) {
                case 0 -> wallTexture;
                case 1 -> entryTexture;
                case 2 -> exitTexture;
                case 3 -> trapTexture;
                case 4 -> enemyTexture;
                case 5 -> keyTexture;
                default -> pathTexture;
            };

            //should draw it using the SpriteBatch, on each coordinate for the tile size
            batch.draw(textureToDraw, x*tileSize, y*tileSize);
        }
        batch.end();
    }

    //basically just removes the assets

    public void dispose() {
        batch.dispose();
        wallTexture.dispose();
        pathTexture.dispose();
        entryTexture.dispose();
        enemyTexture.dispose();
        keyTexture.dispose();
        exitTexture.dispose();
    }
}

