package com.lando.systems.June15GAM.buildings;

import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class Building {

    public TileTexture texture = TileTexture.SELECTION;
    public int x;
    public int y;

    public Building(int x, int y){
        this.x = x;
        this.y = y;
    }
}
