package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.List;


public class Enemy implements Entity {
    public Vector2 position;
    private Texture texture;
    private Animation animation;
    public Rectangle scanRange;

    public Enemy(int x, int y){
        position = new Vector2(x,y);
        texture=new Texture("TiledMaps/SlimeA.png");
        animation=new Animation(new TextureRegion(texture),16,3f);
        int scanrangewidth = 64;
        int scanrangeheight = 64;
        scanRange = new Rectangle(position.x-24,position.y-30,64,64);
    }
    public void update(float delta){
        animation.update(delta);
    }
    public TextureRegion getEnemy(){
        return animation.getFrame();
    }

    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public void setHealth(int health) {

    }

    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public void setPosition(Vector2 position) {

    }

    @Override
    public int getArmor() {
        return 0;
    }

    @Override
    public void setArmor(int armor) {

    }

    @Override
    public List<String> getPowerUps() {
        return List.of();
    }

    @Override
    public void setPowerUps(List<String> powerUps) {

    }

    @Override
    public int getMoney() {
        return 0;
    }

    @Override
    public void setMoney(int money) {

    }

    @Override
    public void saveState(String filename) {
        EntityUtils.saveToFile(this, filename);
    }

    @Override
    public void loadState(String filename) {
        Entity loaded = EntityUtils.loadFromFile(filename,this);
        if (loaded instanceof Enemy loadedEnemy) {
            this.position = loadedEnemy.position;
        }
    }
}
