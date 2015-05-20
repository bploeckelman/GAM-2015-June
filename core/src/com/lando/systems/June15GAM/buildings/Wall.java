package com.lando.systems.June15GAM.buildings;

import com.lando.systems.June15GAM.buildings.Building;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class Wall extends Building {

    public Wall(int x, int y){
        super(x, y);
        texture = TileTexture.GROUND_SAND;
    }

    public void setTexture(int neighbors){

        switch (neighbors){
            case 0:
                texture = TileTexture.GROUND_CONCRETE;
                break;
            case 1: // N
                texture = TileTexture.ROAD_LINE_THIN_END_S;
                break;
            case 2: // E
                texture = TileTexture.ROAD_LINE_THIN_END_W;
                break;
            case 3: // NE
                texture = TileTexture.ROAD_CORNER_CURVE_NE;
                break;
            case 4: // S
                texture = TileTexture.ROAD_LINE_THIN_END_S;
                break;
            case 5: // N S
                texture = TileTexture.ROAD_LINE_THIN_V;
                break;
            case 6: // E S
                texture = TileTexture.ROAD_CORNER_CURVE_SE;
                break;
            case 7: // N E S
                texture = TileTexture.ROAD_TEE_THIN_E;
                break;
            case 8: // W
                texture = TileTexture.ROAD_LINE_THIN_END_E;
                break;
            case 9: // N W
                texture = TileTexture.ROAD_CORNER_CURVE_NW;
                break;
            case 10: // E W
                texture = TileTexture.ROAD_LINE_THIN_H;
                break;
            case 11: // N E W
                texture = TileTexture.ROAD_TEE_THIN_N;
                break;
            case 12: // S W
                texture = TileTexture.ROAD_CORNER_CURVE_SW;
                break;
            case 13: // N S W
                texture = TileTexture.ROAD_TEE_THIN_W;
                break;
            case 14: // E S W
                texture = TileTexture.ROAD_TEE_THIN_S;
                break;
            case 15: // N E S W
                texture = TileTexture.ROAD_FOURWAY;
                break;
        }

    }
}
