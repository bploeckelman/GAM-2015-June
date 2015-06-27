package com.lando.systems.June15GAM.weapons;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.accessors.ColorAccessor;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Cannonball extends Projectile {

    static final  float MAX_SIZE       = 20f;
    public static float frame_duration = 0.05f;

    float        initialWidth;
    float        initialHeight;
    Color        targetColor;
    MutableFloat targetBounce;

    public Source source;

    // TODO: keep separate pools for different sources instead of this?
    public enum Source {
        UNKNOWN, TOWER, SHIP
    }

    @Override
    public Projectile init(float x, float y,
                           float tx, float ty,
                           float w, float h,
                           float speed) {
        this.initialWidth = w;
        this.initialHeight = h;
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

        this.targetColor = new Color(1,1,1,1);
        Tween.to(targetColor, ColorAccessor.A, 2f * lifetime)
             .target(0.f)
             .ease(Circ.IN)
             .start(June15GAM.tween);
        this.targetBounce = new MutableFloat(10f);

        final int repeats = 3;
        final float duration = 2f * lifetime / repeats;
        Tween.to(targetBounce, -1, duration)
             .target(0f)
             .ease(Bounce.OUT)
             .repeat(repeats, 0f)
             .start(June15GAM.tween);

        return this;
    }

    @Override
    public void renderShadow(SpriteBatch batch) {
        final float tx = 4f * bounds.width  / MAX_SIZE - 1f;
        final float ty = 4f * bounds.height / MAX_SIZE - 1f;
        final float dx = 2f;
        final float dy = 8f;
        final float width  = initialWidth  / 5f + (MAX_SIZE - bounds.width);
        final float height = initialHeight / 5f + (MAX_SIZE - bounds.height);

        batch.draw(Assets.spritesheetRegions[0][4], bounds.x - dx * tx, bounds.y - dy * ty, width, height);
    }

    @Override
    public void render(SpriteBatch batch) {
        final float tile_size = 32f;
        final float half_tile_size = tile_size / 2f;
        batch.setColor(targetColor);
        batch.draw(Assets.spritesheetRegions[0][4],
                   target.x - half_tile_size / 2f,
                   target.y - half_tile_size / 2f,
                   half_tile_size, half_tile_size);
        batch.draw(Assets.spritesheetRegions[0][5],
                   target.x - half_tile_size / 2f,
                   target.y + targetBounce.floatValue(),
                   half_tile_size, half_tile_size);
        batch.setColor(Color.WHITE);
        super.render(batch);
    }

    public void setSource(Source source) {
        this.source = source;
        if (source == Source.TOWER) {
            this.targetColor.r = 1;
            this.targetColor.g = 1;
            this.targetColor.b = 1;
        } else if (source == Source.SHIP) {
            this.targetColor.r = 1;
            this.targetColor.g = 0;
            this.targetColor.b = 0;
        }
    }

}
