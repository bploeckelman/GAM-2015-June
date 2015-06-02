package com.lando.systems.June15GAM.buildings;

import com.lando.systems.June15GAM.buildings.Building;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class Tower extends Building {

    static final float default_shot_delay = 2f;

    float shotDelay;
    float shotTimer;

    public Tower(int x, int y){
        super(x,y);
        texture = TileTexture.GROUND_CONCRETE;
        shotDelay = default_shot_delay;
        shotTimer = 0f;
    }

    @Override
    public void update(float delta) {
        shotTimer -= delta;
        if (shotTimer < 0f) {
            shotTimer = 0f;
        }
        texture = canFire() ? TileTexture.GROUND_ROAD : TileTexture.GROUND_CONCRETE;
    }

    public boolean canFire() {
        return (shotTimer <= 0f);
    }

    public void fire() {
        shotTimer = shotDelay;
    }

}
