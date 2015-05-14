package com.lando.systems.June15GAM.tilemap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Brian Ploeckelman created on 5/12/2015.
 */
public class TileMap {

    public TileSet         tileSet;
    public TileTexture[][] tiles;

    public TileMap(int xTiles, int yTiles) {
        tileSet = new TileSetOverhead();
        tiles = new TileTexture[yTiles][xTiles];
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                int r = MathUtils.random(1, 100);
                if      (r >= 50) tiles[y][x] = TileTexture.GROUND_GRASS;
                else if (r >= 40) tiles[y][x] = TileTexture.GROUND_CLAY;
                else if (r >= 30) tiles[y][x] = TileTexture.GROUND_SAND;
                else if (r >= 20) tiles[y][x] = TileTexture.GROUND_WATER;
                else              tiles[y][x] = TileTexture.GROUND_CONCRETE;
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                final TextureRegion tile = tileSet.textures.get(tiles[y][x]);
                batch.draw(tile, x * tile.getRegionWidth(), y * tile.getRegionHeight());
            }
        }

    }

}
