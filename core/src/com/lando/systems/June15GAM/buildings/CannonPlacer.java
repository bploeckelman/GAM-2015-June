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
        if (isValidPlacement(map)) placementColor.set(0, 1, 0, .7f);
        else                       placementColor.set(1, 0, 0, .5f);

        batch.setColor(placementColor);
        batch.draw(Assets.spritesheetRegions[1][0],
                   getTileX() * map.tileSet.tileSize,
                   getTileY() * map.tileSet.tileSize,
                   map.tileSet.tileSize,
                   map.tileSet.tileSize);

        // TODO: number left shouldn't be drawn here
        final float SCALE_LINE_1 = 1.25f;
        final float SCALE_LINE_2 = 2f;
        final String line1 = "Cannons left:";
        final String line2 = "" + numberToPlace;

        Assets.font.getData().setScale(SCALE_LINE_1);
        layout.setText(Assets.font, line1);
        final float line1x = ((map.width * map.tileSet.tileSize) - layout.width) / 2f;
        final float line1y = (map.height * map.tileSet.tileSize) - layout.height - 10f;
        Assets.font.setColor(Color.BLACK);
        Assets.font.draw(batch, line1, line1x + 2f, line1y + 2f);
        Assets.font.setColor(Color.YELLOW);
        Assets.font.draw(batch, line1, line1x, line1y);

        Assets.font.getData().setScale(SCALE_LINE_2);
        layout.setText(Assets.font, line2);
        final float line2x = ((map.width * map.tileSet.tileSize) - layout.width) / 2f;
        final float line2y = line1y - layout.height - 2f;
        Assets.font.setColor(Color.BLACK);
        Assets.font.draw(batch, line2, line2x + 2f, line2y + 2f);
        Assets.font.setColor(placementColor);
        Assets.font.draw(batch, line2, line2x, line2y);

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
