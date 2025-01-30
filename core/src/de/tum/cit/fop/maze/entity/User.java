package de.tum.cit.fop.maze.entity;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The User class represents a game user with properties like username, completed levels,
 * preferences, and high score. It provides methods to save and load user data to and from a file.
 */
public class User {
    private String username;
    private List<String> completedLevels;
    private Map<String, Object> preferences;
    private int highScore;

    /**
     * Constructs a new User with the specified username and default values for completed levels,
     * preferences, and high score.
     *
     * @param username The username of the user.
     */
    public User(String username) {
        this.username = username;
        this.completedLevels = new ArrayList<>();
        this.preferences = new HashMap<>();
        this.highScore = 0;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the list of completed levels.
     *
     * @return The list of completed levels.
     */
    public List<String> getCompletedLevels() {
        return completedLevels;
    }

    /**
     * Sets the list of completed levels.
     *
     * @param completedLevels The list of completed levels to set.
     */
    public void setCompletedLevels(List<String> completedLevels) {
        this.completedLevels = completedLevels;
    }

    /**
     * Gets the user preferences.
     *
     * @return The user preferences.
     */
    public Map<String, Object> getPreferences() {
        return preferences;
    }

    /**
     * Sets the user preferences.
     *
     * @param preferences The preferences to set.
     */
    public void setPreferences(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    /**
     * Gets the high score of the user.
     *
     * @return The high score of the user.
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Sets the high score of the user.
     *
     * @param highScore The high score to set.
     */
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    /**
     * Adds a completed level by its TMX file name to the list of completed levels.
     *
     * @param levelFileName The filename of the completed level.
     */
    public void addCompletedLevel(String levelFileName) {
        if (!completedLevels.contains(levelFileName)) {
            completedLevels.add(levelFileName);
        }
    }

    /**
     * Saves the user data to a file.
     *
     * @param filename The name of the file to save the user data to.
     */
    public void saveUserData(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("completedLevels", completedLevels);
            userData.put("preferences", preferences);
            userData.put("highScore", highScore);

            out.writeObject(userData);
            System.out.println("User data saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    /**
     * Loads user data from a file. If no data is found, creates a new user with default values.
     *
     * @param filename The name of the file to load the user data from.
     * @return The loaded User object, or a new User if no data was found.
     */
    public static User loadUserData(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("No user data found. Creating new user data file.");
            User newUser = new User("Player1");
            newUser.saveUserData(filename);
            return newUser;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Map<String, Object> loadedData = (Map<String, Object>) in.readObject();

            String username = (String) loadedData.get("username");
            List<String> completedLevels = (List<String>) loadedData.get("completedLevels");
            Map<String, Object> preferences = (Map<String, Object>) loadedData.get("preferences");
            int highScore = (int) loadedData.get("highScore");

            User user = new User(username);
            user.setCompletedLevels(completedLevels);
            user.setPreferences(preferences);
            user.setHighScore(highScore);

            System.out.println("User data loaded from " + filename);
            return user;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Resets the list of completed levels to an empty state.
     */
    public void resetCompletedLevels() {
        completedLevels.clear();
    }
}
