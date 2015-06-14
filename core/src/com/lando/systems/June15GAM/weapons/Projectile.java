package com.lando.systems.June15GAM.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 6/13/2015.
 */
public abstract class Projectile implements Pool.Poolable {

    static final float HIT_EPSILON = 1f;

    public Rectangle bounds;
    public Vector2   position;
    public Vector2   target;
    public Vector2   velocity;
    public float     speed;

    public TextureRegion keyframe;
    public Animation     animation;
    public float         stateTime;

    public boolean alive;

    public Projectile() {
        bounds = new Rectangle();
        position = new Vector2();
        target = new Vector2();
        velocity = new Vector2();
        speed = 1f;
        animation = null; // NOTE: set in subclasses
        keyframe = Assets.defaultProjectileTexture;
        stateTime = 0f;
        alive = false;
    }

    /**
     * Initialize a newly obtained instance of Projectile from a Pool
     * @param x x position
     * @param y y position
     * @param tx target x position
     * @param ty target y position
     * @param w width of keyframe
     * @param h height of keyframe
     * @param speed speed cap for velocity
     * @return this for chaining
     */
    public abstract Projectile init(float x, float y,
                                    float tx, float ty,
                                    float w, float h,
                                    float speed);

    public void reset() {
        alive = false;
        stateTime = 0f;
        velocity.set(0f, 0f);
    }

    public void setTarget(float targetX, float targetY) {
        target.set(targetX, targetY);
        bounds.getCenter(position);
        velocity.set(target.x - position.x, target.y - position.y)
                .nor().scl(speed);
    }

    public void setTarget(Vector2 targetPosition) {
        setTarget(targetPosition.x, targetPosition.y);
    }

    public boolean didHitTarget() {
        return position.epsilonEquals(target, HIT_EPSILON);
    }

    public void update(float delta) {
        // Update the animation if there is one
        if (animation != null) {
            stateTime += delta;
            keyframe = animation.getKeyFrame(stateTime);
        }

        // Move until target is hit, then stop
        if (!position.epsilonEquals(target, HIT_EPSILON)) {
            bounds.x += velocity.x * delta;
            bounds.y += velocity.y * delta;
            bounds.getCenter(position);
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
