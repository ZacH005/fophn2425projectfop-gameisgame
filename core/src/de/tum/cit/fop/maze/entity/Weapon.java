package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Weapon {
    private Texture texture;
    private Animation<TextureRegion> animation;
    private Vector2 position;
    private float rotationAngle; // Angle around the player
    private float range;         // Range of attack
    private Rectangle attackArea;
    private float sectorAngle = 60; // Sector size in degrees
    private float stateTime; // Time tracking for animation playback


    private Vector2 direction;
    private float pickaxeRotation;
    private float pickaxePivotX;
    private float pickaxePivotY;

    public Weapon(String texturePath, float range) {
        this.texture = new Texture(texturePath);
        this.animation = null;
        this.position = new Vector2();
        this.range = range;
        this.attackArea = new Rectangle();
        this.stateTime = 0f;
        loadWeaponAnimation();
        direction = new Vector2();
        pickaxeRotation = 0f;
    }

    //make width and height, texture,  parameters
    public void loadWeaponAnimation()    {
        Texture swipeSheet = new Texture(Gdx.files.internal("animations/player/pickaxeSwipe_real.png"));
        int frameWidth = 57, frameHeight = 32, swipeAnimationFrames = 5, y = 0;

        Array<TextureRegion> swipeFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < swipeAnimationFrames; col++) {
            swipeFrames.add(new TextureRegion(swipeSheet, col*(frameWidth), y, frameWidth, frameHeight));
        }

        this.animation = new com.badlogic.gdx.graphics.g2d.Animation<>(0.05f, swipeFrames);
    }

    //tangent on the right and left of the player
    //swap sides smoothly when the player attacks

    public void update(Vector2 playerPosition, Vector2 mousePosition, OrthographicCamera camera) {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
        Vector3 worldMouse = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));
        mousePosition.set(worldMouse.x, worldMouse.y);


        direction.set(mousePosition.x - playerPosition.x, mousePosition.y - playerPosition.y).nor();
        pickaxeRotation = direction.angleDeg() - 90f;

        pickaxePivotX = ((32 * 0.33f) / 2f);
        pickaxePivotY = 0f;

        position.x = position.x + (playerPosition.x + pickaxePivotX + direction.x - position.x);
        position.y = position.y + (playerPosition.y + pickaxePivotY + direction.y - position.y);
//        position.x += playerPosition.x - pickaxePivotX;
//        position.y += playerPosition.y - pickaxePivotY;

//        System.out.println(position);
    }

    public void render(SpriteBatch batch, float deltaTime, Vector2 playerPosition) {
        // Update state time for animation
        stateTime += deltaTime;

        // Get the current frame of the animation
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        // Calculate the position of the weapon relative to the player
//        position.x = playerPosition.x + (float) Math.cos(rotationAngle) * range;
//        position.y = playerPosition.y + (float) Math.sin(rotationAngle) * range;

        // Calculate rotation in degrees
        float rotation = (float) Math.toDegrees(rotationAngle);

        // Draw the current animation frame
//        batch.draw(currentFrame,
//                position.x - currentFrame.getRegionWidth() / 2f, position.y - currentFrame.getRegionHeight() / 2f, // Position
//                currentFrame.getRegionWidth() / 2f, currentFrame.getRegionHeight() / 2f,                         // Origin
//                currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),                                 // Size
//                1f, 1f, rotation);                                                                            // Scale and Rotation
        //pivot

        if (!currentFrame.isFlipX()) {
            currentFrame.flip(true, true); // Flip on X-axis
        }

        float centeredX = position.x - currentFrame.getRegionWidth() / 2f;
        float centeredY = position.y - currentFrame.getRegionHeight() / 2f;
//        batch.draw(
//                currentFrame,
//                position.x, position.y, // Sword position
//                pickaxePivotX, pickaxePivotY,
//                57, 32, // Sword size
//                1f, 1f, // Scaling
//                pickaxeRotation // Rotation in degrees
//        );

//        batch.draw(
//                currentFrame,
//                position.x, position.y, // Sword position
//                pickaxePivotX, pickaxePivotY,
//                57, 32, // Sword size
//                1f, 1f, // Scaling
//                pickaxeRotation // Rotation in degrees
//        );
    }


    public boolean isInSector(Vector2 enemyPosition, Vector2 playerPosition) {
        float angleToEnemy = (float) Math.atan2(enemyPosition.y - playerPosition.y, enemyPosition.x - playerPosition.x);
        float angleDiff = Math.abs((float) Math.toDegrees(angleToEnemy - rotationAngle));

        // Normalize angle difference to [0, 360)
        if (angleDiff > 180) angleDiff = 360 - angleDiff;

        return angleDiff <= sectorAngle / 2 && playerPosition.dst(enemyPosition) <= range;
    }

    public void dispose() {
        texture.dispose();
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public Rectangle getAttackArea() {
        return attackArea;
    }

    public void setAttackArea(Rectangle attackArea) {
        this.attackArea = attackArea;
    }

    public float getSectorAngle() {
        return sectorAngle;
    }

    public void setSectorAngle(float sectorAngle) {
        this.sectorAngle = sectorAngle;
    }

}

