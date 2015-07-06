package com.lando.systems.June15GAM.effects;

import com.badlogic.gdx.utils.Pool;
import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 6/5/2015.
 */
public class ExplosionGround extends Effect implements Pool.Poolable {

    public static final float explosion_ground_scale = 1.1f;
    public static final float explosion_ground_time  = 0.075f;

    public ExplosionGround() {
        super();
        animation = Assets.explosionGroundAnim;
        scale.setValue(explosion_ground_scale);
    }

    @Override
    public void reset() {
        position.set(0, 0);
        alive = false;
        stateTime = 0;
    }

}
