package com.lando.systems.June15GAM.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class SpriteSheetPacker {
    public static void main(String[] args) {
        final TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.filterMin = Texture.TextureFilter.MipMapNearestNearest;
        settings.filterMag = Texture.TextureFilter.MipMapNearestNearest;
        settings.maxWidth  = 1024;
        settings.maxHeight = 1024;

        // NOTE: change these to pack a different set of images into a different spritesheet
        final String srcImageDir = "/Users/ploeckelman/Dev/Source/GAM/Assets/overhead-tiles/PNG";
        final String dstImageDir = "/Users/ploeckelman/Dev/Source/GAM/GAM-2015-June/android/assets/spritesheets";
        final String dstPackFileName = "kenny-overhead-tiles";
        TexturePacker.process(settings, srcImageDir, dstImageDir, dstPackFileName);
    }
}
