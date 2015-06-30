package com.lando.systems.June15GAM.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Brian Ploeckelman created on 6/5/2015.
 */
public class EffectsManager {

    Pool<ExplosionWater> explosionWaterPool;
    Pool<ExplosionGround> explosionGroundPool;
    Pool<DecalCrater>     decalCraterPool;

    Array<ExplosionWater>  explosionsWater;
    Array<ExplosionGround> explosionsGround;
    Array<DecalCrater>     decalCraters;

    public EffectsManager() {
        explosionWaterPool = Pools.get(ExplosionWater.class);
        explosionGroundPool = Pools.get(ExplosionGround.class);
        decalCraterPool = Pools.get(DecalCrater.class);

        explosionsWater  = new Array<ExplosionWater>();
        explosionsGround = new Array<ExplosionGround>();
        decalCraters = new Array<DecalCrater>();
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
            case DECAL_CRATER: {
                DecalCrater decal = decalCraterPool.obtain();
                decal.init(x, y);
                decalCraters.add(decal);
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
        for (int i = decalCraters.size - 1; i >= 0; --i) {
            final DecalCrater decal = decalCraters.get(i);
            decal.update(delta);
            if (!decal.isAlive()) {
                decalCraterPool.free(decal);
                decalCraters.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (DecalCrater decal : decalCraters) {
            decal.render(batch);
        }
        for (ExplosionWater explosion : explosionsWater) {
            explosion.render(batch);
        }
        for (ExplosionGround explosion : explosionsGround) {
            explosion.render(batch);
        }
    }

    public void clearDecals() {
        for (DecalCrater decal : decalCraters) {
            decalCraterPool.free(decal);
        }
        decalCraters.clear();
    }

}
