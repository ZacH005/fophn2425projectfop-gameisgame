package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.abilities.Powerup;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;

/**
 * Utility class for saving and loading entity states to and from a file.
 */
public class EntityUtils {

    /**
     * Saves the specified entity's state to a file.
     *
     * @param entity   the entity whose state is to be saved
     * @param filename the name of the file where the state will be saved
     */
    public static void saveToFile(Entity entity, String filename) {
        File file = new File(filename);

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

        try (FileOutputStream fos = new FileOutputStream(file);
             FileChannel fileChannel = fos.getChannel();
             FileLock lock = fileChannel.lock();
             ObjectOutputStream out = new ObjectOutputStream(fos)) {

            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("health", entity.getHealth());
            dataToSave.put("position", entity.getPosition());
            dataToSave.put("armor", entity.getArmor());
            dataToSave.put("powerUps", entity.getPowerUps());
            dataToSave.put("money", entity.getMoney());
            dataToSave.put("isFollowing", entity.isFollowing());

            out.writeObject(dataToSave);
            System.out.println("Entity saved to " + filename);

        } catch (IOException e) {
            System.err.println("Error saving entity to file: " + e.getMessage());
        }
    }

    /**
     * Loads an entity's state from a file.
     *
     * @param filename the name of the file to load the entity state from
     * @param entity   the entity whose state is to be updated
     * @return the updated entity with the loaded state, or null if an error occurs
     */
    public static Entity loadFromFile(String filename, Entity entity) {
        File file = new File(filename);

        if (!file.exists()) {
            System.err.println("File not found: " + filename);
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {

            Map<String, Object> loadedData = (Map<String, Object>) in.readObject();

            if (loadedData != null) {
                entity.setHealth((float) loadedData.get("health"));
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
