package com.lando.systems.June15GAM.buildings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileType;

/**
 * Created by Karla on 6/5/2015.
 */
public class CannonPlacer extends MoveableObject {

    private int numberToPlace = 0;

    public CannonPlacer(TileMap world){
        super(world);
        int internalTiles = world.numberOfInternalTiles();
        numberToPlace = 1 + (int)MathUtils.log(4, internalTiles);
    }

    public boolean isValidPlacement(TileMap tileMap) {
        if (tileMap.getBuildingAt(getTileX(), getTileY()) != null) return false;
        if (tileMap.getTileType(getTileX(), getTileY()) != TileType.INTERIOR) return false;
        return true;
    }

    GlyphLayout layout         = new GlyphLayout();
    Color       placementColor = new Color();

    public void render(TileMap map, SpriteBatch batch) {
        if (isValidPlacement(map)) placementColor.set(1, 1, 1, .7f);
        else                       placementColor.set(1, 0, 0, .5f);

        batch.setColor(placementColor);
        batch.draw(Assets.spritesheetRegions[1][0],
                   getTileX() * map.tileSet.tileSize,
                   getTileY() * map.tileSet.tileSize,
                   map.tileSet.tileSize,
                   map.tileSet.tileSize);

        // TODO: number left shouldn't be drawn here
        final String remaining = numberToPlace + " left...";
        Assets.font.setColor(placementColor);
        Assets.font.getData().setScale(1.5f);
        layout.setText(Assets.font, remaining);
        Assets.font.draw(batch, remaining,
                         ((map.width * map.tileSet.tileSize) - layout.width) / 2f,
                         map.height * map.tileSet.tileSize - layout.height - 10f);
        Assets.font.getData().setScale(1f);

        Assets.font.setColor(Color.WHITE);
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
