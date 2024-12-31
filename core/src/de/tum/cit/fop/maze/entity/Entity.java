package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public interface Entity {
    int getHealth();
    void setHealth(int health);

    Vector2 getPosition();
    void setPosition(Vector2 position);

    int getArmor();
    void setArmor(int armor);

    List<String> getPowerUps(); // we can do a list of powerups that based on the name of them we modify the variables of the player
    void setPowerUps(List<String> powerUps);

    int getMoney();
    void setMoney(int money);


    void saveState(String filename);  // Save state to file
    void loadState(String filename); // Load state from file
}
