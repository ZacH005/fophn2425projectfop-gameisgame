package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.abilities.Powerup;

import java.util.List;

public interface Entity {
    void heal();
    void takeDamage();

    int getHealth();
    void setHealth(int health);

    Vector2 getPosition();
    void setPosition(Vector2 position);

    boolean isFollowing();
    void setFollowing(boolean following);

    int getArmor();
    void setArmor(int armor);

    List<Powerup> getPowerUps(); // we can do a list of powerups that based on the name of them we modify the variables of the player
    void setPowerUps(List<Powerup> powerUps);

    int getMoney();
    void setMoney(int money);

    void saveState(String filename);  // Save state to file
    void loadState(String filename); // Load state from file
}
