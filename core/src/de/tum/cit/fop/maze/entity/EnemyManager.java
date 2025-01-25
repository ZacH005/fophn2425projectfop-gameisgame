package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.*;

public class EnemyManager {

    //skeleton animations
    private Animation<TextureRegion> skeletonDownAnimation;
    private Animation<TextureRegion> skeletonUpAnimation;
    private Animation<TextureRegion> skeletonRightAnimation;
    private Animation<TextureRegion> skeletonLeftAnimation;
    private Animation<TextureRegion> skeletonUpIdleAnimation;
    private Animation<TextureRegion> skeletonDownIdleAnimation;
    private Animation<TextureRegion> skeletonLeftIdleAnimation;
    private Animation<TextureRegion> skeletonRightIdleAnimation;
    private Animation<TextureRegion> skeletonLeftAttackAnimation;
    private Animation<TextureRegion> skeletonRightAttackAnimation;
    private Animation<TextureRegion> skeletonDownAttackAnimation;
    private Animation<TextureRegion> skeletonUpAttackAnimation;
    private Animation<TextureRegion> skeletonRunUpAnimation;
    private Animation<TextureRegion> skeletonRunDownAnimation;
    private Animation<TextureRegion> skeletonRunLeftAnimation;
    private Animation<TextureRegion> skeletonRunRightAnimation;

    public EnemyManager()   {
        this.loadSkeletonAnimation();
    }

    private Map<String, Animation<TextureRegion>> skeletonAnimations = new HashMap<>();

    private void loadSkeletonAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("animations/player/skeletonadjusted.png"));

        int frameWidth = 64, frameHeight = 64, walkAnimationFrames = 9, idleAnimationFrames = 2, y = 0, knockbackAnimFrames = 1;

        for (int i = 0; i <= 50; i++) {
            Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> idleFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> attackLeftFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> runningFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> knockbackFrames = new Array<>(TextureRegion.class);
            Array<TextureRegion> deathFrames = new Array<>(TextureRegion.class);

            for (int col = 0; col < walkAnimationFrames; col++) {
                walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            for (int col = 0; col < idleAnimationFrames; col++) {
                idleFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 6; col++) {
                attackLeftFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
            }
            for (int col = 0; col < 8; col++) {
                runningFrames.add(new TextureRegion(walkSheet, col * frameWidth, y, frameWidth, frameHeight));
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
                case 3008:
                    skeletonAnimations.put("rightAttack", new Animation<>(0.05f, attackLeftFrames, Animation.PlayMode.NORMAL));
                    break;
                case 3072:
                    skeletonAnimations.put("downAttack", new Animation<>(0.07f, attackLeftFrames));
                    break;
                case 3136:
                    skeletonAnimations.put("leftAttack", new Animation<>(0.05f, attackLeftFrames));
                    break;
                case 3200:
                    skeletonAnimations.put("upAttack", new Animation<>(0.07f, attackLeftFrames));
                    break;
//                case 2176:
//                    skeletonAnimations.put("upRun", new Animation<>(0.05f, runningFrames));
//                    break;
//                case 2240:
//                    skeletonAnimations.put("leftRun", new Animation<>(0.05f, runningFrames));
//                    break;
//                case 2304:
//                    skeletonAnimations.put("downRun", new Animation<>(0.05f, runningFrames));
//                    break;
//                case 2368:
//                    skeletonAnimations.put("rightRun", new Animation<>(0.05f, runningFrames));
//                    break;
                case 36*64:
                    skeletonAnimations.put("upKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 37*64:
                    skeletonAnimations.put("leftKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 38*64:
                    skeletonAnimations.put("downKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 39*64:
                    skeletonAnimations.put("rightKnock", new Animation<>(0.05f, knockbackFrames));
                    break;
                case 20*64:
                    skeletonAnimations.put("death", new Animation<>(0.15f, deathFrames));
                    break;
                }
            y += frameHeight;
        }
    }

    public Map<String, Animation<TextureRegion>> getSkeletonAnimations() {
        return skeletonAnimations;
    }

    public void setSkeletonAnimations(Map<String, Animation<TextureRegion>> skeletonAnimations) {
        this.skeletonAnimations = skeletonAnimations;
    }
}
