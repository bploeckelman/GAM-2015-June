package com.lando.systems.June15GAM.tilemap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * Brian Ploeckelman created on 5/12/2015.
 */
public abstract class TileSet {

    public Map<TileTexture, TextureRegion> textures;
    public float tileSize;

    public TileSet() {
        textures = new HashMap<TileTexture, TextureRegion>();
    }

}
