package com.lando.systems.June15GAM.tilemap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Brian Ploeckelman created on 5/12/2015.
 */
public class TileSetOverhead extends TileSet {

    public TextureAtlas atlas;

    public TileSetOverhead() {
        super();
        atlas = new TextureAtlas("spritesheets/kenney-overhead-tiles.atlas");

        final Texture atlasTex = atlas.getTextures().first();
        textures.put(TileType.BLANK,                 new TextureRegion(atlasTex, atlasTex.getWidth() - 32, atlasTex.getHeight() - 32));
        textures.put(TileType.GROUND_CLAY,           atlas.findRegion("ground-clay"));
        textures.put(TileType.GROUND_CONCRETE,       atlas.findRegion("ground-concrete"));
        textures.put(TileType.GROUND_GRASS,          atlas.findRegion("ground-grass"));
        textures.put(TileType.GROUND_ROAD,           atlas.findRegion("ground-road"));
        textures.put(TileType.GROUND_SAND,           atlas.findRegion("ground-sand"));
        textures.put(TileType.GROUND_WATER,          atlas.findRegion("ground-water"));
        textures.put(TileType.ROAD_CORNER_CURVE_NE,  atlas.findRegion("road-corner-curve-ne"));
        textures.put(TileType.ROAD_CORNER_CURVE_SE,  atlas.findRegion("road-corner-curve-se"));
        textures.put(TileType.ROAD_CORNER_CURVE_NW,  atlas.findRegion("road-corner-curve-nw"));
        textures.put(TileType.ROAD_CORNER_CURVE_SW,  atlas.findRegion("road-corner-curve-sw"));
        textures.put(TileType.ROAD_CORNER_SQUARE_NE, atlas.findRegion("road-corner-square-ne"));
        textures.put(TileType.ROAD_CORNER_SQUARE_SE, atlas.findRegion("road-corner-square-se"));
        textures.put(TileType.ROAD_CORNER_SQUARE_NW, atlas.findRegion("road-corner-square-nw"));
        textures.put(TileType.ROAD_CORNER_SQUARE_SW, atlas.findRegion("road-corner-square-sw"));
        textures.put(TileType.ROAD_FOURWAY,          atlas.findRegion("road-fourway"));
        textures.put(TileType.ROAD_LINE_THICK_N,     atlas.findRegion("road-line-thick-n"));
        textures.put(TileType.ROAD_LINE_THICK_E,     atlas.findRegion("road-line-thick-e"));
        textures.put(TileType.ROAD_LINE_THICK_S,     atlas.findRegion("road-line-thick-s"));
        textures.put(TileType.ROAD_LINE_THICK_W,     atlas.findRegion("road-line-thick-w"));
        textures.put(TileType.ROAD_LINE_THIN_END_N,  atlas.findRegion("road-line-thin-end_n"));
        textures.put(TileType.ROAD_LINE_THIN_END_E,  atlas.findRegion("road-line-thin-end_e"));
        textures.put(TileType.ROAD_LINE_THIN_END_S,  atlas.findRegion("road-line-thin-end_s"));
        textures.put(TileType.ROAD_LINE_THIN_END_W,  atlas.findRegion("road-line-thin-end_w"));
        textures.put(TileType.ROAD_LINE_THIN_H,      atlas.findRegion("road-line-thin-h"));
        textures.put(TileType.ROAD_LINE_THIN_V,      atlas.findRegion("road-line-thin-v"));
        textures.put(TileType.ROAD_TEE_THICK_N,      atlas.findRegion("road-tee-thick-n"));
        textures.put(TileType.ROAD_TEE_THICK_E,      atlas.findRegion("road-tee-thick-e"));
        textures.put(TileType.ROAD_TEE_THICK_S,      atlas.findRegion("road-tee-thick-s"));
        textures.put(TileType.ROAD_TEE_THICK_W,      atlas.findRegion("road-tee-thick-w"));
        textures.put(TileType.ROAD_TEE_THIN_N,       atlas.findRegion("road-tee-thin-n"));
        textures.put(TileType.ROAD_TEE_THIN_E,       atlas.findRegion("road-tee-thin-e"));
        textures.put(TileType.ROAD_TEE_THIN_S,       atlas.findRegion("road-tee-thin-s"));
        textures.put(TileType.ROAD_TEE_THIN_W,       atlas.findRegion("road-tee-thin-w"));
    }

}
