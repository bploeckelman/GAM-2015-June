package com.lando.systems.June15GAM.tilemap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lando.systems.June15GAM.buildings.Building;
import com.lando.systems.June15GAM.buildings.Keep;
import com.lando.systems.June15GAM.buildings.Tower;
import com.lando.systems.June15GAM.buildings.Wall;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Brian Ploeckelman created on 5/12/2015.
 */
public class TileMap {

    public TileSet  tileSet;
    public Tile[][] tiles;
    public boolean gameLost;

    private HashMap<Integer, Building> buildings;
    private int castleRadius = 3;
    private int width;
    private int height;
    private Keep homeKeep;



    public TileMap(int xTiles, int yTiles) {
        gameLost = false;
        buildings = new HashMap<Integer, Building>();
        width = xTiles;
        height = yTiles;
        tileSet = new TileSetOverhead();
        tiles = new Tile[yTiles][xTiles];
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {

                if (x + y > 50) {
                    tiles[y][x] = new Tile(TileType.WATER, TileTexture.GROUND_WATER, x, y);
                } else {
                    tiles[y][x] = new Tile(TileType.GROUND, TileTexture.GROUND_GRASS, x, y);

                }
            }
        }

        makeStarterCastle(15, 15);
        setInternal();
        reconcileWalls();
    }



    public void render(SpriteBatch batch) {
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                final TextureRegion tile = tileSet.textures.get(tiles[y][x].texture);
                if (tiles[y][x].type == TileType.INTERIOR){
                    batch.setColor(Color.LIGHT_GRAY);
                }
                batch.draw(tile, x * tileSet.tileSize, y * tileSet.tileSize);
                batch.setColor(Color.WHITE);
            }
        }
        for (Building building : buildings.values()){
            if (building == null) continue;
            final TextureRegion tile = tileSet.textures.get(building.texture);
            batch.draw(tile, building.x * tileSet.tileSize, building.y * tileSet.tileSize, tileSet.tileSize, tileSet.tileSize);
        }
    }


    public Building getBuildingAt(int x, int y){
            return buildings.get(x + y * width);
    }

    /**
     * Call if you need to get the walls in the world
     * @return a list of walls
     */
    public ArrayList<Wall> getWalls(){
        ArrayList<Wall> walls = new ArrayList<Wall>();
        for (Building building : buildings.values()){
            if (building instanceof Wall){
                walls.add((Wall)building);
            }
        }
        return walls;
    }

    public ArrayList<Tower> getTowers(){
        ArrayList<Tower> towers = new ArrayList<Tower>();
        for (Building building : buildings.values()){
            if (building instanceof Tower){
                towers.add((Tower)building);
            }
        }
        return towers;
    }

    public void setWall(int x, int y){
        buildings.put(x + y * width, new Wall(x, y));
        reconcileWalls();
        setInternal();
    }

    public void setTower(int x, int y){
        buildings.put(x + y * width, new Tower(x, y));
        reconcileWalls();
        setInternal();
    }

    public TileType getTileType(int x, int y){
        if (inBounds(x, y)){
            return tiles[y][x].type;
        }
        return null;
    }

    public void destroyBuildingAt(int x, int y){
        buildings.put(x + y * width, null);
        reconcileWalls();
    }

    public void reconcileWalls(){
        for (Building building : buildings.values()){
            if (building instanceof Wall){
                int neighbors = 0;
                if (getBuildingAt(building.x, building.y + 1) instanceof  Wall) neighbors += 1;
                if (getBuildingAt(building.x + 1, building.y) instanceof  Wall) neighbors += 2;
                if (getBuildingAt(building.x, building.y - 1) instanceof  Wall) neighbors += 4;
                if (getBuildingAt(building.x -1, building.y) instanceof  Wall) neighbors += 8;
                ((Wall) building).setTexture(neighbors);
            }
        }
    }




    private void resetGround(TileType type){
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                if (tiles[y][x].type == TileType.INTERIOR || tiles[y][x].type == TileType.GROUND) tiles[y][x].type = type;
            }
        }
    }


    /**
     * Walk the map and set tiles that are not surrounded to ground
     * return
     */
    public void setInternal(){
        resetGround(TileType.INTERIOR);

        ArrayList<Tile> tilesToCheck = new ArrayList<Tile>();
        ArrayList<Tile> checkedTiles = new ArrayList<Tile>(); // TODO reuse these if GC is an issue

        // Add all edge tiles
        for (int i = 0; i < width; i++){
            Tile checkedTile = tiles[0][i];
            tilesToCheck.add(checkedTile);
            checkedTiles.add(checkedTile);

            checkedTile = tiles[height -1][i];
            tilesToCheck.add(checkedTile);
            checkedTiles.add(checkedTile);
        }
        for (int i = 0; i < height; i++){
            Tile checkedTile = tiles[i][0];
            tilesToCheck.add(checkedTile);
            checkedTiles.add(checkedTile);

            checkedTile = tiles[i][width -1];
            tilesToCheck.add(checkedTile);
            checkedTiles.add(checkedTile);
        }

        while(!tilesToCheck.isEmpty()){
            Tile currentTile = tilesToCheck.remove(0);

            Building building = buildings.get(currentTile.x + currentTile.y * width);
            if (building != null && building instanceof Wall) continue;

            // not wall lets keep walking
            if (currentTile.type == TileType.INTERIOR) currentTile.type = TileType.GROUND;
            for (int i = -1; i <= 1; i++){
                for (int j = -1; j <= 1; j++){
                    int testX = currentTile.x + i;
                    int testY = currentTile.y + j;
                    if (inBounds(testX, testY)){
                        if (!checkedTiles.contains(tiles[testY][testX])){
                            Tile nextTile = tiles[testY][testX];
                            tilesToCheck.add(nextTile);
                            checkedTiles.add(nextTile);
                        }
                    }
                }
            }

        }

    }

    private boolean inBounds(int x, int y){
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private void makeStarterCastle(int x, int y){
        homeKeep = new Keep(x, y);
        buildings.put(x + y * width, homeKeep);

        for (int i = -castleRadius; i <= castleRadius; i++){
            setWall(x - castleRadius, y + i);
            setWall(x + castleRadius, y + i);
            setWall(x + i, y + castleRadius);
            setWall(x + i, y - castleRadius);
        }

        // TODO this is debug
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                setWall(x + castleRadius + i, y + j);
            }
        }


        setTower(x - 1, y - 1);
        setTower(x + 1, y - 1);
        setTower(x - 1, y + 1);
        setTower(x + 1, y + 1);
    }


}
