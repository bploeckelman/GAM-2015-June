package com.lando.systems.June15GAM.buildings;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class Keep extends Building {

    public static final float frameDuration = 0.1f;

    public TextureRegion keyframe;
    public Animation animation;
    public float     stateTime;

    public Keep(int x, int y) {
        super(x, y);
        texture = TileTexture.GROUND_CLAY;
        animation = Assets.keepAnim;
        stateTime = 0f;
        keyframe = animation.getKeyFrame(stateTime);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        keyframe = animation.getKeyFrame(stateTime);
    }

}
