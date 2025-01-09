package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.abilities.Powerup;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;

public class EntityUtils {

    //  /save only the specific variables to the file
    public static void saveToFile(Entity entity, String filename) {
        File file = new File(filename);

        // Check if the file exists; create it if not
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Save file created: " + filename);
                }
            } catch (IOException e) {
                System.err.println("Error creating save file: " + e.getMessage());
                return;
            }
        }



        // Save the entity's state to the file with locking
        try (FileOutputStream fos = new FileOutputStream(file);
             FileChannel fileChannel = fos.getChannel();
             FileLock lock = fileChannel.lock();
             ObjectOutputStream out = new ObjectOutputStream(fos)) {

            // Create a map to hold only the relevant variables
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("health", entity.getHealth());
            dataToSave.put("position", entity.getPosition());
            dataToSave.put("armor", entity.getArmor());
            dataToSave.put("powerUps", entity.getPowerUps());
            dataToSave.put("money", entity.getMoney());

            dataToSave.put("isFollowing", entity.isFollowing());

            // Write the map to file
            out.writeObject(dataToSave);
            System.out.println("Entity saved to " + filename);

        } catch (IOException e) {
            System.err.println("Error saving entity to file: " + e.getMessage());

        }
    }
    // load only the specific variables from the file
    public static Entity loadFromFile(String filename, Entity entity) {
        File file = new File(filename);

        // check if the file exists before attempting to load
        if (!file.exists()) {
            System.err.println("File not found: " + filename);
            return null;
        }

        // Load the entity's state from the file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {

            Map<String, Object> loadedData = (Map<String, Object>) in.readObject();

            // Update the entity's state based on the loaded data
            if (loadedData != null) {
                entity.setHealth((int) loadedData.get("health"));
                //commented out because after gameover it would respawn at level
                //entity.setPosition((Vector2) loadedData.get("position"));
                entity.setArmor((int) loadedData.get("armor"));
                entity.setPowerUps((List<Powerup>) loadedData.get("powerUps"));
                entity.setMoney((int) loadedData.get("money"));
                entity.setFollowing((boolean) loadedData.get("isFollowing"));

                System.out.println("Entity loaded from " + filename);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading entity from file: " + e.getMessage());
            return null;
        }
        return entity;
    }
}
