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


    private HashMap<Integer, Building> buildings;
    private int castleRadius = 3;
    private int width;
    private int height;
    private Keep homeKeep;



    public TileMap(int xTiles, int yTiles) {
        buildings = new HashMap<Integer, Building>();
        width = xTiles;
        height = yTiles;
        tileSet = new TileSetOverhead();
        tiles = new Tile[yTiles][xTiles];
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                tiles[y][x] = new Tile();
                if (x + y > 50) {
                    tiles[y][x].texture = TileTexture.GROUND_WATER;
                    tiles[y][x].type = TileType.WATER;
                } else {
                    tiles[y][x].texture = TileTexture.GROUND_GRASS;
                    tiles[y][x].type = TileType.GROUND;
                }
            }
        }

        makeStarterCastle(15, 15);
        findInternals();
        reconcileWalls();
    }



    public void render(SpriteBatch batch) {
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                final TextureRegion tile = tileSet.textures.get(tiles[y][x].texture);
                if (tiles[y][x].type == TileType.INTERIOR){
                    batch.setColor(Color.LIGHT_GRAY);
                }
                batch.draw(tile, x * tile.getRegionWidth(), y * tile.getRegionHeight());
                batch.setColor(Color.WHITE);
            }
        }
        for (Building building : buildings.values()){
            final TextureRegion tile = tileSet.textures.get(building.texture);
            batch.draw(tile, building.x * tile.getRegionWidth(), building.y * tile.getRegionHeight());
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
    }

    public void setTower(int x, int y){
        buildings.put(x + y * width, new Tower(x, y));
    }

    public void destroyBuildingAt(int x, int y){
        buildings.put(x + y * width, null);
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

    private void findInternals(){
        resetGround();
        if (setInternal(homeKeep.x, homeKeep.y)){
            resetGround();
        }
    }

    private void resetGround(){
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                if (tiles[y][x].type == TileType.INTERIOR) tiles[y][x].type = TileType.GROUND;
            }
        }
    }

    /**
     * Walk the map and set internals
     * @param x x position
     * @param y y position
     * @return true if the keep isn't contained
     */
    public boolean setInternal(int x, int y){
        if (x < 0 || x >= width) return true; // outside bounds
        if (y < 0 || y >= height) return true; // outside bounds
        Tile currentTile = tiles[y][x];
        if (currentTile.type == TileType.WATER) return true;

        // If it is already set, no need to check
        if (currentTile.type == TileType.INTERIOR) return false;
        currentTile.type = TileType.INTERIOR;
        Building building = buildings.get(x + y * width);

        if (building != null && building instanceof Wall) return false;

        if (setInternal(x - 1, y)) return true;
        if (setInternal(x + 1, y)) return true;
        if (setInternal(x, y - 1)) return true;
        if (setInternal(x, y + 1)) return true;

        return false;
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

        setTower(x - (castleRadius -1), y - (castleRadius -1));
        setTower(x + (castleRadius -1), y - (castleRadius -1));
        setTower(x - (castleRadius -1), y + (castleRadius -1));
        setTower(x + (castleRadius -1), y + (castleRadius -1));
    }


}
