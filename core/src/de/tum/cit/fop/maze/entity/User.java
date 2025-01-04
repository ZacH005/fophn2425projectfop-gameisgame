package de.tum.cit.fop.maze.entity;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String username;
    private List<String> completedLevels; // List of completed levels (TMX filenames)
    private Map<String, Object> preferences; // Preferences like sound settings, difficulty, etc.
    private int highScore; // High score or any other game data

    // Constructor to initialize a new user with default values
    public User(String username) {
        this.username = username;
        this.completedLevels = new ArrayList<>();
        this.preferences = new HashMap<>();
        this.highScore = 0;
    }

    // Getters and Setters for the user properties
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(List<String> completedLevels) {
        this.completedLevels = completedLevels;
    }

    public Map<String, Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    // Add a completed level by its TMX file name
    public void addCompletedLevel(String levelFileName) {
        if (!completedLevels.contains(levelFileName)) {
            completedLevels.add(levelFileName);
        }
    }

    // Save user data to a file
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

    // Load user data from a file
    public static User loadUserData(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            // If the file doesn't exist, create it with default data
            System.out.println("No user data found. Creating new user data file.");
            User newUser = new User("Player1");
            newUser.saveUserData(filename);  // Save the new user data
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
    public void resetCompletedLevels() {
        completedLevels.clear();
    }
}
