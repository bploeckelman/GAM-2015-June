package com.lando.systems.June15GAM.buildings;

import com.lando.systems.June15GAM.buildings.Building;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class Tower extends Building {

    public Tower(int x, int y){
        super(x,y);
        texture = TileTexture.GROUND_CONCRETE;
    }
}
