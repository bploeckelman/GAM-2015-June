package com.lando.systems.June15GAM.buildings;

import com.lando.systems.June15GAM.buildings.Building;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class Keep extends Building {

    public Keep(int x, int y){
        super(x, y);
        texture = TileTexture.GROUND_CLAY;
    }
}
