package com.lando.systems.June15GAM.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.lando.systems.June15GAM.Assets;

/**
 * Brian Ploeckelman created on 6/4/2015.
 */
public class ExplosionWater extends Effect {

    public static Pool<ExplosionWater> explosionWaterPool = new Pool<ExplosionWater>() {
        @Override
        protected ExplosionWater newObject() {
            return new ExplosionWater();
        }
    };

    public static Array<ExplosionWater> explosions = new Array<ExplosionWater>();
    public static void newExplosion(float x, float y) {
        ExplosionWater explosion = explosionWaterPool.obtain();
        explosion.init(x, y);
        explosions.add(explosion);
    }
    public static void updateExplosions(float delta) {
        for (int i = explosions.size - 1; i >= 0; --i) {
            final ExplosionWater explosion = explosions.get(i);
            explosion.update(delta);
            if (!explosion.isAlive()) {
                explosionWaterPool.free(explosion);
                explosions.removeIndex(i);
            }
        }
    }
    public static void renderExplosions(SpriteBatch batch) {
        for (ExplosionWater explosion : explosions) {
            explosion.render(batch);
        }
    }

    public static final float explosion_water_scale = 1.1f;
    public static final float explosion_water_time  = 0.075f;


    public ExplosionWater() {
        super();
        animation = Assets.explosionWaterAnim;
        scale.setValue(explosion_water_scale);
    }

}
