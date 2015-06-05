package com.lando.systems.June15GAM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lando.systems.June15GAM.effects.ExplosionWater;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Assets {

    public static Texture weaponsTexture;
    public static Texture vehiclesTexture;
    public static Texture placeButtonTexture;
    public static Texture effectsTexture;

    public static TextureRegion[][] effectsRegions;
    public static TextureRegion[][] weaponRegions;
    public static TextureRegion[][] vehicleRegions;

    public static Animation explosionWaterAnim;

    public static void load() {
        effectsTexture = new Texture("oryx_16bit_scifi_FX_lg_trans.png");
        weaponsTexture = new Texture("fantasy-sprites.png");
        vehiclesTexture = new Texture("fantasy-sprites.png");
        placeButtonTexture = new Texture("place_button.png");

        effectsRegions = TextureRegion.split(effectsTexture, 32, 32);
        weaponRegions = TextureRegion.split(weaponsTexture, 16, 16);
        vehicleRegions = TextureRegion.split(vehiclesTexture, 16, 16);

        explosionWaterAnim = new Animation(
                ExplosionWater.explosion_water_time,
                effectsRegions[0][3],
                effectsRegions[0][2],
                effectsRegions[0][5],
                effectsRegions[0][4],
                effectsRegions[9][3],
                effectsRegions[9][2],
                effectsRegions[2][4],
                effectsRegions[10][4],
                effectsRegions[10][5],
                effectsRegions[2][5]);
    }

    public static void dispose() {
        effectsTexture.dispose();
        weaponsTexture.dispose();
        vehiclesTexture.dispose();
    }

}
