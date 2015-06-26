package com.lando.systems.June15GAM.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lando.systems.June15GAM.buildings.Building;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class Tower extends Building {

    static final float default_shot_delay = 4f;

    float shotDelay;
    float shotTimer;
    public boolean onInternal;

    public Tower(int x, int y){
        super(x,y);
        onInternal = true;
        texture = TileTexture.CANNON_READY;
        shotDelay = default_shot_delay;
        shotTimer = 0f;
    }

    @Override
    public void update(float delta) {
        shotTimer -= delta;
        if (shotTimer < 0f) {
            shotTimer = 0f;
        }
        // TODO: display countdown timer overlay on top of cannons that are reloading
        texture = canFire() ? TileTexture.CANNON_READY: TileTexture.CANNON_LOADING;
    }

    public boolean canFire() {
        return (shotTimer <= 0f && onInternal);
    }

    public void enableShot() {
        shotTimer = 0f;
    }

    public void fire() {
        shotTimer = shotDelay;
    }

}
