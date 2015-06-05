package com.lando.systems.June15GAM.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Brian Ploeckelman created on 6/5/2015.
 */
public class EffectsManager {

    Pool<ExplosionWater> explosionWaterPool;
    Pool<ExplosionGround> explosionGroundPool;

    Array<ExplosionWater>  explosionsWater;
    Array<ExplosionGround> explosionsGround;

    public EffectsManager() {
        explosionWaterPool = Pools.get(ExplosionWater.class);
        explosionGroundPool = Pools.get(ExplosionGround.class);

        explosionsWater  = new Array<ExplosionWater>();
        explosionsGround = new Array<ExplosionGround>();
    }

    public void newEffect(Effect.Type type, float x, float y) {
        switch (type) {
            case EXPLOSION_WATER: {
                ExplosionWater explosion = explosionWaterPool.obtain();
                explosion.init(x, y);
                explosionsWater.add(explosion);
                break;
            }
            case EXPLOSION_GROUND: {
                ExplosionGround explosion = explosionGroundPool.obtain();
                explosion.init(x, y);
                explosionsGround.add(explosion);
                break;
            }
            default:
                Gdx.app.error("EFFECTS", "Unable to create new effect, unknown effect type");
        }
    }

    public void update(float delta) {
        for (int i = explosionsWater.size - 1; i >= 0; --i) {
            final ExplosionWater explosion = explosionsWater.get(i);
            explosion.update(delta);
            if (!explosion.isAlive()) {
                explosionWaterPool.free(explosion);
                explosionsWater.removeIndex(i);
            }
        }
        for (int i = explosionsGround.size - 1; i >= 0; --i) {
            final ExplosionGround explosion = explosionsGround.get(i);
            explosion.update(delta);
            if (!explosion.isAlive()) {
                explosionGroundPool.free(explosion);
                explosionsGround.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (ExplosionWater explosion : explosionsWater) {
            explosion.render(batch);
        }
        for (ExplosionGround explosion : explosionsGround) {
            explosion.render(batch);
        }
    }

}
