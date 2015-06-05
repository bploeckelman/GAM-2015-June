package com.lando.systems.June15GAM.tilemap;

/**
 * Brian Ploeckelman created on 5/13/2015.
 */
public class Tile {
    public TileType type;
    public TileTexture texture;
    public int x;
    public int y;

    public Tile(TileType type, TileTexture texture, int x, int y){
        this.type = type;
        this.texture = texture;
        this.x = x;
        this.y = y;
    }
}
