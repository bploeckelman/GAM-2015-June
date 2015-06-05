package com.lando.systems.June15GAM.buildings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileTexture;
import com.lando.systems.June15GAM.tilemap.TileType;

/**
 * Created by Karla on 6/5/2015.
 */
public class CannonPlacer extends MoveableObject {

    private int numberToPlace = 0;

    public CannonPlacer(int count){
        numberToPlace = count;
    }

    public boolean isValidPlacement(TileMap tileMap) {
        if (tileMap.getBuildingAt(getTileX(), getTileY()) != null) return false;
        if (tileMap.getTileType(getTileX(), getTileY()) != TileType.INTERIOR) return false;
        return true;
    }

    public void render(TileMap map, SpriteBatch batch){
        if (isValidPlacement(map)) batch.setColor(1,1,1,.7f);
        else batch.setColor(1,0,0,.5f);

        final TextureRegion tile = map.tileSet.textures.get(TileTexture.ROAD_FOURWAY);
        batch.draw(tile, getTileX() * map.tileSet.tileSize, getTileY() * map.tileSet.tileSize, map.tileSet.tileSize, map.tileSet.tileSize);

        // TODO: display how many are left to place
        batch.setColor(Color.WHITE);
    }

    public boolean place(TileMap map){
        if (isValidPlacement(map) && numberToPlace > 0){
            numberToPlace--;
            map.setTower(getTileX(), getTileY());
            return true;
        }
        return false;
    }

    public int getNumberLeft(){
        return numberToPlace;
    }
}
