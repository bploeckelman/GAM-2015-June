package com.lando.systems.June15GAM.effects;

import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 6/4/2015.
 */
public class ExplosionWater extends Effect {

    public static final float explosion_water_scale = 1.1f;
    public static final float explosion_water_time  = 0.075f;

    public ExplosionWater() {
        super();
        animation = Assets.explosionWaterAnim;
        scale.setValue(explosion_water_scale);
    }

}
