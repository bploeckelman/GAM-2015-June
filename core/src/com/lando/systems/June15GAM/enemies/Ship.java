package com.lando.systems.June15GAM.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 5/19/2015.
 */
public class Ship {

    public static final float FRAME_DURATION = 0.2f;

    // TODO: different animations for different direction
    public Animation animation;
    public float     animTimer;
    public Vector2   position;
    public Vector2   velocity;
    public Vector2   size;

    public Ship(float x, float y) {
        this.animation = new Animation(Ship.FRAME_DURATION,
                                       Assets.vehicleRegions[1][2],
                                       Assets.vehicleRegions[1][3]);
        this.animation.setPlayMode(Animation.PlayMode.LOOP);
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.size = new Vector2(64, 64);
    }

    // TODO: collision checking and resolution

    public void update(float delta) {
        // TODO: switch animations based on movement direction
        animTimer += delta;
        position.add(velocity);
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(animTimer), position.x, position.y, size.x, size.y);
    }

}
