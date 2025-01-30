package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages enemy animations for skeleton characters.
 */
public class EnemyManager {

    private Map<String, Animation<TextureRegion>> skeletonAnimations = new HashMap<>();

    /**
     * Constructs an EnemyManager and loads skeleton animations.
     */
    public EnemyManager() {
        this.loadSkeletonAnimation();
    }

    /**
     * Loads skeleton animations from a sprite sheet.
     */
    private void loadSkeletonAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("animations/player/skeletonadjusted.png"));

        int frameWidth = 64, frameHeight = 64;
        int walkAnimationFrames = 9, idleAnimationFrames = 2;
        int y = 0;

        for (int i = 0; i <= 59; i++) {
            Array<TextureRegion> walkFrames = new Array<>();
            Array<TextureRegion> idleFrames = new Array<>();
            Array<TextureRegion> attackFrames = new Array<>();
            Array<TextureRegion> knockbackFrames = new Array<>();
            Array<TextureRegion> deathFrames = new Array<>();

            for (int col = 0; col < walkAnimationFrames; col++) {
                walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            for (int col = 0; col < idleAnimationFrames; col++) {
                idleFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 6; col++) {
                attackFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 3; col++) {
                if (col == 2)
                    knockbackFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 6; col++) {
                deathFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }

            switch (y) {
                case 640:
                    skeletonAnimations.put("downWalk", new Animation<>(0.05f, walkFrames));
                    break;
                case 704:
                    skeletonAnimations.put("rightWalk", new Animation<>(0.05f, walkFrames));
                    break;
                case 576:
                    skeletonAnimations.put("leftWalk", new Animation<>(0.05f, walkFrames));
                    break;
                case 512:
                    skeletonAnimations.put("upWalk", new Animation<>(0.05f, walkFrames));
                    break;
                case 128:
                    skeletonAnimations.put("downIdle", new Animation<>(0.25f, idleFrames));
                    break;
                case 0:
                    skeletonAnimations.put("upIdle", new Animation<>(0.25f, idleFrames));
                    break;
                case 64:
                    skeletonAnimations.put("leftIdle", new Animation<>(0.25f, idleFrames));
                    break;
                case 192:
                    skeletonAnimations.put("rightIdle", new Animation<>(0.25f, idleFrames));
                    break;
                case 55 * 64:
                    skeletonAnimations.put("rightAttack", new Animation<>(0.05f, attackFrames, Animation.PlayMode.NORMAL));
                    break;
                case 56 * 64:
                    skeletonAnimations.put("downAttack", new Animation<>(0.07f, attackFrames));
                    break;
                case 57 * 64:
                    skeletonAnimations.put("leftAttack", new Animation<>(0.05f, attackFrames));
                    break;
                case 58 * 64:
                    skeletonAnimations.put("upAttack", new Animation<>(0.07f, attackFrames));
                    break;
                case 36 * 64:
                    skeletonAnimations.put("upKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 37 * 64:
                    skeletonAnimations.put("leftKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 38 * 64:
                    skeletonAnimations.put("downKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 39 * 64:
                    skeletonAnimations.put("rightKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 20 * 64:
                    skeletonAnimations.put("death", new Animation<>(0.15f, deathFrames));
                    break;
            }
            y += frameHeight;
        }
    }

    /**
     * Retrieves the skeleton animations.
     *
     * @return a map of animation names to Animation<TextureRegion> objects
     */
    public Map<String, Animation<TextureRegion>> getSkeletonAnimations() {
        return skeletonAnimations;
    }

    /**
     * Sets the skeleton animations.
     *
     * @param skeletonAnimations a map containing animation names and their respective animations
     */
    public void setSkeletonAnimations(Map<String, Animation<TextureRegion>> skeletonAnimations) {
        this.skeletonAnimations = skeletonAnimations;
    }
}