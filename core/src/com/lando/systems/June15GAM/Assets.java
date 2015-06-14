package com.lando.systems.June15GAM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lando.systems.June15GAM.effects.ExplosionGround;
import com.lando.systems.June15GAM.effects.ExplosionWater;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Assets {

    public static Texture weaponsTexture;
    public static Texture vehiclesTexture;
    public static Texture placeButtonTexture;
    public static Texture effectsTexture;
    public static Texture spritesheetTexture;

    public static TextureRegion[][] effectsRegions;
    public static TextureRegion[][] weaponRegions;
    public static TextureRegion[][] vehicleRegions;
    public static TextureRegion[][] spritesheetRegions;

    public static TextureRegion defaultProjectileTexture;

    public static Animation explosionWaterAnim;
    public static Animation explosionGroundAnim;

    public static void load() {
        effectsTexture = new Texture("oryx_16bit_scifi_FX_lg_trans.png");
        weaponsTexture = new Texture("fantasy-sprites.png");
        vehiclesTexture = new Texture("fantasy-sprites.png");
        placeButtonTexture = new Texture("place_button.png");
        spritesheetTexture = new Texture("spritesheet.png");

        effectsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        weaponsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        vehiclesTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        placeButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        spritesheetTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        effectsRegions = TextureRegion.split(effectsTexture, 32, 32);
        weaponRegions = TextureRegion.split(weaponsTexture, 16, 16);
        vehicleRegions = TextureRegion.split(vehiclesTexture, 16, 16);
        spritesheetRegions = TextureRegion.split(spritesheetTexture, 16, 16);

        defaultProjectileTexture = spritesheetRegions[0][0];

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

        explosionGroundAnim = new Animation(
                ExplosionGround.explosion_ground_time,
                effectsRegions[9][0],
                effectsRegions[9][1],
                effectsRegions[10][2],
                effectsRegions[10][3],
                effectsRegions[9][6],
                effectsRegions[9][7],
                effectsRegions[10][5],
                effectsRegions[2][5]);
    }

    public static void dispose() {
        effectsTexture.dispose();
        weaponsTexture.dispose();
        vehiclesTexture.dispose();
        placeButtonTexture.dispose();
        spritesheetTexture.dispose();
    }

}
