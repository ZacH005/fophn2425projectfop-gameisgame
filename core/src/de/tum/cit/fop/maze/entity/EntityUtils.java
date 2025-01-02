package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.io.*;
import java.util.*;

public class EntityUtils {

    //  /save only the specific variables to the file
    public static void saveToFile(Entity entity, String filename) {
        File file = new File(filename);

        // /check if the file exists; create it if not
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

        /// Save the entity's state to the file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            // Create a map to hold only the relevant variables
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("health", entity.getHealth());
            dataToSave.put("position", entity.getPosition());
            dataToSave.put("armor", entity.getArmor());
            dataToSave.put("powerUps", entity.getPowerUps());
            dataToSave.put("money", entity.getMoney());



            // Write the map to file
            out.writeObject(dataToSave);
            System.out.println("Entity saved to " + filename );
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
                entity.setPosition((Vector2) loadedData.get("position"));
                entity.setArmor((int) loadedData.get("armor"));
                entity.setPowerUps((List<String>) loadedData.get("powerUps"));
                entity.setMoney((int) loadedData.get("money"));

                System.out.println("Entity loaded from " + filename);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading entity from file: " + e.getMessage());
            return null;
        }
        return entity;
    }
}
