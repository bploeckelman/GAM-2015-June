package com.lando.systems.June15GAM.tilemap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Brian Ploeckelman created on 5/12/2015.
 */
public class TileMap {

    public TileSet  tileSet;
    public Tile[][] tiles;



    public TileMap(int xTiles, int yTiles) {
        tileSet = new TileSetOverhead();
        tiles = new Tile[yTiles][xTiles];
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                tiles[y][x] = new Tile();

                tiles[y][x].type = TileType.GROUND;
                int r = MathUtils.random(1, 100);
                if      (r >= 30) tiles[y][x].texture = TileTexture.GROUND_GRASS;
                else if (r >= 20) tiles[y][x].texture = TileTexture.GROUND_CONCRETE;
                else {
                    tiles[y][x].texture = TileTexture.GROUND_WATER;
                    tiles[y][x].type = TileType.WATER;
                }
            }
        }

        makeStarterCastle(15, 15);
    }

    public void makeStarterCastle(int x, int y){
        tiles[y][x].texture = TileTexture.GROUND_CLAY;
        tiles[y][x].type = TileType.KEEP;


        for (int i = -5; i <= 5; i++){
            setWall(x - 5, y + i);
            setWall(x + 5, y + i);
            setWall(x + i, y + 5);
            setWall(x + i, y - 5);
        }

    }

    public void setWall(int x, int y){
        tiles[y][x].texture = TileTexture.ROAD_FOURWAY;
        tiles[y][x].type = TileType.WALL;
    }

    public void render(SpriteBatch batch) {
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                final TextureRegion tile = tileSet.textures.get(tiles[y][x].texture);
                batch.draw(tile, x * tile.getRegionWidth(), y * tile.getRegionHeight());
            }
        }

    }

}
