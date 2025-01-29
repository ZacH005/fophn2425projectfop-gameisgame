package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import de.tum.cit.fop.maze.SoundManager;
import de.tum.cit.fop.maze.abilities.Powerup;
import de.tum.cit.fop.maze.arbitrarymap.CollisionManager;

import java.util.*;

public abstract class Enemy implements Entity {
    // Common properties
    protected boolean isDead = false;
    protected Vector2 position;
    public Rectangle scanRange;
    protected Player player;
    protected boolean following;
    protected boolean roaming;
    protected Animation<TextureRegion> currentAnimation;
    protected float animationTime;
    public Rectangle damageCollider;
    protected SoundManager soundManager;
    protected Map<String, Integer> chaseState = new HashMap<>();
    protected Map<String, Integer> mainState = new HashMap<>();
    protected float lastDamageTime;
    protected float cooldownTime = 2f;
    protected HUD hud;
    protected int scanrangewidth;
    protected int scanrangeheight;
    protected int health;
    protected float movementSpeed;
    protected List<Node> currentPath;
    protected Vector2 knockbackVelocity = new Vector2(0, 0);
    protected float knockbackDuration = 0;
    protected float knockbackTimeElapsed = 0;
    protected Map<String, Animation<TextureRegion>> animations;
    protected Vector2 lastDirection;
    protected ParticleEffect hurtParticle;
    protected boolean hurting;
    protected boolean attacking = false;
    protected float attackDuration = 0.3f;
    protected float attackTimeElapsed = 0f;


    public Enemy(float x, float y, Player player, HUD hud, SoundManager soundManager, Map<String, Animation<TextureRegion>> animations, int health) {
        this.player = player;
        this.position = new Vector2(x, y);
        this.scanrangewidth = 100;
        this.scanrangeheight = 100;
        this.scanRange = new Rectangle(position.x - scanrangewidth / 2f + 8, position.y - scanrangeheight / 2f + 4, scanrangeheight, scanrangewidth);
        this.damageCollider = new Rectangle(position.x - 2, position.y - 5, 20, 20);
        this.health = health;
        this.movementSpeed = 3.5f;
        this.soundManager = soundManager;
        this.hud = hud;
        this.roaming = true;
        this.hurting = false;
        this.animations = animations;
        this.currentAnimation = animations.get("downWalk");
        this.hurtParticle = new ParticleEffect();
        this.hurtParticle.load(Gdx.files.internal("particles/effects/Particle Park Blood.p"), Gdx.files.internal("particles/images"));

        // Initialize chase and main states
        chaseState.put("crackles", 0);
        chaseState.put("wind", 1);
        chaseState.put("piano", 1);
        chaseState.put("strings", 0);
        chaseState.put("pad", 0);
        chaseState.put("drums", 1);
        chaseState.put("bass", 1);
        chaseState.put("slowerDrums", 0);

        mainState.put("crackles", 1);
        mainState.put("wind", 1);
        mainState.put("piano", 1);
        mainState.put("strings", 0);
        mainState.put("pad", 1);
        mainState.put("drums", 0);
        mainState.put("bass", 1);
        mainState.put("slowerDrums", 1);
    }

    // Common methods
    public void update(float delta, CollisionManager colManager) {
        hurtParticle.update(delta);
        animationTime += delta;

        if (isDead) return;


        if (knockbackDuration > 0) {
            position.add(knockbackVelocity.x * delta, knockbackVelocity.y * delta);

            knockbackTimeElapsed += delta;
            if (knockbackTimeElapsed >= knockbackDuration) {
                knockbackVelocity.set(0, 0);
                knockbackDuration = 0;
                knockbackTimeElapsed = 0;
                following = true;
            }
            hurting = true;
            attacking = false;
            updateColliders();
        } else if (attacking) {
            attackTimeElapsed += delta;

            if (attackTimeElapsed >= attackDuration) {
                attacking = false;
                following = true;
                attackTimeElapsed = 0f;
            }
        } else {
            updateMovement(colManager);
            hurting = false;
        }

        checkDamaging();
        updateProjectiles(delta);
    }

    public void render(SpriteBatch batch) {
        hurtParticle.draw(batch);

        if (currentAnimation != null) {
            TextureRegion frame;

            if (isDead) {
                currentAnimation = animations.get("death");
                frame = currentAnimation.getKeyFrame(animationTime, false);
            } else if (hurting) {
                currentAnimation = animations.get("upKnock");
                batch.setColor(0.7f, 0, 0, 1);
                frame = currentAnimation.getKeyFrame(animationTime, true);
            } else if (attacking) {
                frame = currentAnimation.getKeyFrame(animationTime, false);
            } else {
                frame = currentAnimation.getKeyFrame(animationTime, true);
            }

            batch.draw(frame, position.x - ((float) 16 / 2), position.y - ((float) 16 / 2), 16 * 2.0f, 16 * 2.0f);
            batch.setColor(1, 1, 1, 1);
        }
        renderProjectiles(batch);
    }

    public void updateColliders() {
        this.scanRange.setX(this.position.x - scanRange.getWidth() / 2f + 8);
        this.scanRange.setY(this.position.y - scanRange.getHeight() / 2f + 4);

        this.damageCollider.setX(this.position.x - 2);
        this.damageCollider.setY(this.position.y - 5);
    }

    // Abstract methods for subclasses to implement
    protected abstract void attack();

    protected abstract void updateMovement(CollisionManager colManager);

    protected abstract void checkDamaging();

    public void applyKnockback(Vector2 sourcePosition, float strength) {
        Vector2 knockbackDirection = new Vector2(position.x - sourcePosition.x, position.y - sourcePosition.y).nor();

        knockbackVelocity.set(knockbackDirection.scl(strength));
        knockbackDuration = 0.095f;
        knockbackTimeElapsed = 0;
    }

    protected abstract void renderProjectiles(SpriteBatch batch);
    protected abstract void updateProjectiles(float delta);

        // Other common methods (e.g., applyKnockback, takeDamage, etc.) can remain here
}