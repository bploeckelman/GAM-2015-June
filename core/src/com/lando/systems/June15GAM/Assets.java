package com.lando.systems.June15GAM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Assets {

    public static Texture weaponsTexture;
    public static Texture vehiclesTexture;
    public static Texture placeButtonTexture;

    public static TextureRegion[][] weaponRegions;
    public static TextureRegion[][] vehicleRegions;


    public static void load() {
        weaponsTexture = new Texture("fantasy-sprites.png");
        vehiclesTexture = new Texture("fantasy-sprites.png");
        placeButtonTexture = new Texture("place_button.png");

        weaponRegions = TextureRegion.split(weaponsTexture, 16, 16);
        vehicleRegions = TextureRegion.split(vehiclesTexture, 16, 16);
    }

    public static void dispose() {
        weaponsTexture.dispose();
        vehiclesTexture.dispose();
    }

}
