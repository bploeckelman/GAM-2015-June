package com.lando.systems.June15GAM.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Cannonball extends Projectile {

    // TODO: keep separate pools for different sources instead of this?
    public enum Source { UNKNOWN, TOWER, SHIP }
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

        // TODO: using a single frame for now, create an animation later
//        this.stateTime = 0f;
//        this.animation = Assets.cannonballAnimation;
//        this.keyframe = animation.getKeyFrame(stateTime);

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
