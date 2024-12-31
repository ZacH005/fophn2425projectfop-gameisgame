package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;


public class Enemy {
    public Vector3 position;
    private Texture texture;
    private Animation animation;
    public Rectangle scanRange;

    public Enemy(int x, int y){
        position = new Vector3(x,y,0);
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
}
