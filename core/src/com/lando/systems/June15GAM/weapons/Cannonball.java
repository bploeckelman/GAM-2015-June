package com.lando.systems.June15GAM.weapons;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Cannonball extends Projectile {

    static final float MAX_SIZE = 20f;
    public static float frame_duration = 0.05f;

    // TODO: keep separate pools for different sources instead of this?
    public enum Source {
        UNKNOWN, TOWER, SHIP
    }

    public Source source;

    @Override
    public Projectile init(float x, float y,
                           float tx, float ty,
                           float w, float h,
                           float speed) {
        this.alive = true;
        this.bounds.set(x - w / 2f, y - h / 2f, w, h);
        this.position.set(x, y);
        this.speed = speed;
        setTarget(tx, ty);

        final Vector2 dist = new Vector2(tx - x, ty - y);
        final float lifetime = dist.len() / (2f * velocity.len());

        tweenSizeX.setValue(bounds.width);
        tweenSizeY.setValue(bounds.height);
        Timeline.createParallel()
                .push(Tween.to(tweenSizeX, -1, lifetime)
                           .target(MAX_SIZE)
                           .ease(Sine.INOUT)
                           .repeatYoyo(1, 0))
                .push(Tween.to(tweenSizeY, -1, lifetime)
                           .target(MAX_SIZE)
                           .ease(Sine.INOUT)
                           .repeatYoyo(1, 0))
                .start(June15GAM.tween);

        this.stateTime = 0f;
        this.animation = Assets.cannonballAnim;
        this.keyframe = animation.getKeyFrame(stateTime);

        return this;
    }

    public void render(SpriteBatch batch) {
        final float tile_size = 16f;
        final float half_tile_size = tile_size / 2f;
        batch.draw(Assets.effectsRegions[0][0],
                   target.x - half_tile_size,
                   target.y - half_tile_size,
                   tile_size, tile_size);
        super.render(batch);
    }

}
