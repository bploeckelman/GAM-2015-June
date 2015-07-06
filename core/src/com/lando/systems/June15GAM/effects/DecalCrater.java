package com.lando.systems.June15GAM.effects;

import com.badlogic.gdx.utils.Pool;
import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 6/29/2015.
 */
public class DecalCrater extends Effect implements Pool.Poolable {

    public static final float decal_crater_scale = 1.1f;
    public static final float frameDuration = 0.075f;

    public DecalCrater() {
        super();
        animation = Assets.decalCraterAnim;
        scale.setValue(decal_crater_scale);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        keyframe = animation.getKeyFrame(stateTime);
    }

    @Override
    public void reset() {
        position.set(0, 0);
        alive = false;
        stateTime = 0;
    }

}
