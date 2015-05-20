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
public class TileMap implements Runnable{

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
                batch.draw(tile, x * tileSet.tileSize, y * tileSet.tileSize);
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
        resetGround(TileType.INTERIOR);

        // Android has a tiny stack on the main thread.  This fixes it =)
        Thread thread = new Thread(new ThreadGroup("Worker"), this, "LargerStack", 1000000);
        thread.start();
        try {
            while(thread.isAlive())
            {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (tiles[homeKeep.y][homeKeep.x].type != TileType.INTERIOR) gameLost = true;
    }

    public void run(){
        setInternal(0, 0);
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
     * @param x x position
     * @param y y position
     * return
     */
    public void setInternal(int x, int y){
        if (x < 0 || x >= width) return; // outside bounds
        if (y < 0 || y >= height) return; // outside bounds
        Tile currentTile = tiles[y][x];
        if (currentTile.type == TileType.WATER) return; // TODO this may be bad, may need to check water somehow later

        if (currentTile.type == TileType.GROUND) return;

        Building building = buildings.get(x + y * width);

        if (building != null && building instanceof Wall) return;

        currentTile.type = TileType.GROUND;
        setInternal(x - 1, y - 1);
        setInternal(x - 1, y);
        setInternal(x - 1, y + 1);
        setInternal(x, y - 1);
        setInternal(x, y + 1);
        setInternal(x + 1, y - 1);
        setInternal(x + 1, y);
        setInternal(x + 1, y + 1);


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
