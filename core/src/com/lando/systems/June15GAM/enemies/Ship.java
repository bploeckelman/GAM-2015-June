package com.lando.systems.June15GAM.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 5/19/2015.
 */
public class Ship {

    public static final float FRAME_DURATION = 0.2f;
    public static final float SPEED = 32f;
    public static final float SHOT_COOLDOWN = 2f;

    // TODO: different animations for different direction
    public Animation animation;
    public float     animTimer;
    public float     shotTimer;
    public Vector2   position;
    public Vector2   velocity;
    public Vector2   size;
    public Vector2   moveTarget;
    public Vector2   shotTarget;

    final TextureRegion targetTexture;

    public Ship(float x, float y, float w, float h) {
        this.animation = new Animation(Ship.FRAME_DURATION,
                                       Assets.vehicleRegions[1][2],
                                       Assets.vehicleRegions[1][3]);
        this.animation.setPlayMode(Animation.PlayMode.LOOP);
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.size = new Vector2(w, h);
        this.moveTarget = new Vector2(x, y);
        this.shotTarget = new Vector2(0, 0);
        this.animTimer = 0;

        targetTexture = Assets.effectsRegions[1][2];
    }

    // TODO: collision checking and resolution

    public void update(float delta) {
        shotTimer += delta;
        if (shotTimer > SHOT_COOLDOWN) {
            shotTimer = SHOT_COOLDOWN;
        }

        // TODO: switch animations based on movement direction
        animTimer += delta;
        position.add(velocity.x * delta, velocity.y * delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(animTimer), position.x, position.y, size.x, size.y);
        batch.draw(targetTexture,
                   moveTarget.x - targetTexture.getRegionWidth()  / 2f,
                   moveTarget.y - targetTexture.getRegionHeight() / 2f);
    }

    public boolean reachedTarget() {
        return (position.epsilonEquals(moveTarget, (size.x + size.y) / 4f));
    }

    public boolean canShoot() {
        return shotTimer >= SHOT_COOLDOWN;
    }

    public void shoot() {
        shotTimer = 0f;
    }

}
