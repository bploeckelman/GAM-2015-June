package com.lando.systems.June15GAM.effects;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Brian Ploeckelman created on 6/4/2015.
 */
public class Effect implements Pool.Poolable {

    public enum Type {
        EXPLOSION_WATER,
        EXPLOSION_GROUND
        // ...
    }

    public Vector2      position;
    public MutableFloat scale;

    TextureRegion keyframe;
    Animation     animation;
    float         stateTime;
    boolean       alive;

    public Effect() {
        position = new Vector2();
        scale = new MutableFloat(1);
        stateTime = 0;
        alive = false;
    }

    public void init(float x, float y) {
        position.set(x, y);
        alive = true;
        stateTime = 0;
    }

    @Override
    public void reset() {
        position.set(0, 0);
        alive = false;
        stateTime = 0;
    }

    public void update(float delta) {
        stateTime += delta;
        keyframe = animation.getKeyFrame(stateTime);
        alive = !animation.isAnimationFinished(stateTime);
    }

    public void render(SpriteBatch batch) {
        final float w = keyframe.getRegionWidth() * scale.floatValue();
        final float h = keyframe.getRegionHeight() * scale.floatValue();
        batch.draw(keyframe, position.x, position.y, w, h);
    }

    public boolean isAlive() { return alive; }

}
