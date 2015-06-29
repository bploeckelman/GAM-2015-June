package com.lando.systems.June15GAM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lando.systems.June15GAM.buildings.Keep;
import com.lando.systems.June15GAM.effects.ExplosionGround;
import com.lando.systems.June15GAM.effects.ExplosionWater;
import com.lando.systems.June15GAM.weapons.Cannonball;

/**
 * Brian Ploeckelman created on 5/21/2015.
 */
public class Assets {

    public static BitmapFont font;

    public static Texture weaponsTexture;
    public static Texture vehiclesTexture;
    public static Texture placeButtonTexture;
    public static Texture effectsTexture;
    public static Texture spritesheetTexture;

    public static ShaderProgram menuBackgroundShader;

    public static TextureRegion[][] effectsRegions;
    public static TextureRegion[][] weaponRegions;
    public static TextureRegion[][] vehicleRegions;
    public static TextureRegion[][] spritesheetRegions;

    public static TextureRegion defaultProjectileTexture;

    public static Animation explosionWaterAnim;
    public static Animation explosionGroundAnim;
    public static Animation cannonballAnim;
    public static Animation keepAnim;

    public static void load() {
        font = new BitmapFont(Gdx.files.internal("fonts/zorque.fnt"));
        font.getData().markupEnabled = true;

        menuBackgroundShader = compileShaderProgram(Gdx.files.internal("shaders/default.vert"),
                                                    Gdx.files.internal("shaders/menu.frag"));

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

        cannonballAnim = new Animation(
                Cannonball.frame_duration,
                spritesheetRegions[0][0],
                spritesheetRegions[0][1],
                spritesheetRegions[0][2],
                spritesheetRegions[0][3]);
        cannonballAnim.setPlayMode(Animation.PlayMode.LOOP);

        keepAnim = new Animation(
                Keep.frameDuration,
                spritesheetRegions[3][0],
                spritesheetRegions[3][1],
                spritesheetRegions[3][2],
                spritesheetRegions[3][3]);
        keepAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    public static void dispose() {
        font.dispose();
        menuBackgroundShader.dispose();
        effectsTexture.dispose();
        weaponsTexture.dispose();
        vehiclesTexture.dispose();
        placeButtonTexture.dispose();
        spritesheetTexture.dispose();
    }

    private static ShaderProgram compileShaderProgram(FileHandle vertSource, FileHandle fragSource) {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(vertSource, fragSource);
        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Failed to compile shader program:\n" + shader.getLog());
        }
        else if (shader.getLog().length() > 0) {
            Gdx.app.error("SHADER", "ShaderProgram compilation log:\n" + shader.getLog());
        }
        return shader;
    }

}
