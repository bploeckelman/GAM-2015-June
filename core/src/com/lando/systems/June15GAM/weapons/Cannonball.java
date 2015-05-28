package com.lando.systems.June15GAM.weapons;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Cannonball implements Pool.Poolable {

    public static final float SPEED = 128f;
    public static final float MAX_SIZE = 128f;
    public static final float MIN_SIZE = 32f;

    public TextureRegion texture;
    public Vector2       position;
    public Vector2       target;
    public Vector2       velocity;
    public MutableFloat  size;
    public boolean       alive;

    public Cannonball() {
        texture = new TextureRegion(Assets.weaponRegions[0][0]);
        position = new Vector2();
        velocity = new Vector2();
        target = new Vector2();
        size = new MutableFloat(MIN_SIZE);
        alive = false;
    }

    public void init(float x, float y, float tx, float ty) {
        position.set(x, y);
        target.set(tx, ty);
        velocity.set(tx - x, ty - y);
        velocity.nor().scl(SPEED);
        size.setValue(MIN_SIZE);
        alive = true;

        final Vector2 dist = new Vector2(tx - x, ty - y);
        final float duration = dist.len() / (2f * velocity.len());
        Tween.to(size, -1, duration)
             .target(MAX_SIZE)
             .ease(Sine.INOUT)
             .repeatYoyo(1, 0)
             .start(June15GAM.tween);
    }

    @Override
    public void reset() {
        position.set(0, 0);
        velocity.set(0, 0);
        alive = false;
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, size.floatValue(), size.floatValue());
    }

}
