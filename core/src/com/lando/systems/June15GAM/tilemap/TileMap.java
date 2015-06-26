package com.lando.systems.June15GAM.tilemap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.June15GAM.buildings.*;
import com.lando.systems.June15GAM.screens.GameplayScreen;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Brian Ploeckelman created on 5/12/2015.
 */
public class TileMap {

    public static final int NUM_STARTER_CANNONS = 3;

    public TileSet  tileSet;
    public Tile[][] tiles;
    public boolean gameLost;

    public HashMap<Integer, Building> buildings;
    private int castleRadius = 2;
    private int width;
    private int height;
    private Keep homeKeep;
    public MoveableObject tetris;
    private ArrayList<Tower> towers;
    private int mapSeed;


    public TileMap(TileSet tileSet, int xTiles, int yTiles) {
        this.tileSet = tileSet;
        tetris = new CannonPlacer(NUM_STARTER_CANNONS);
        mapSeed = MathUtils.random(1000);
        gameLost = false;
        buildings = new HashMap<Integer, Building>();
        width = xTiles;
        height = yTiles;
        tileSet = new TileSetOverhead();
        tiles = new Tile[yTiles][xTiles];
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                double noise1 = SimplexNoise.noise((mapSeed +x)/10.0, y/10.0) * .5f;
                double noise2 = SimplexNoise.noise((mapSeed +x)/5.0, y/5.0) * .3f;
                double noise3 = SimplexNoise.noise((mapSeed +x)*1, y*1) * .2f;
                double noise = noise1 + noise2 + noise3;
                float weight = 2.5f * (MathUtils.clamp(((height - y) - 2)/(float)((height+2)/2),0 , 1) - .5f);
                if (noise + weight < 0) {
                    tiles[y][x] = new Tile(TileType.WATER, TileTexture.GROUND_WATER, x, y);
                } else if (noise + weight < .4f){
                    tiles[y][x] = new Tile(TileType.GROUND, TileTexture.GROUND_SAND, x, y);
                } else {
                    tiles[y][x] = new Tile(TileType.GROUND, TileTexture.GROUND_GRASS, x, y);

                }
            }
        }
        towers = new ArrayList<Tower>();

        makeStarterCastle(5, 5);
        setInternal();
        reconcileWalls();
    }


    // TODO: move me
    Vector2 mouseDir = new Vector2();

    public void render(SpriteBatch batch) {
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[y].length; ++x) {
                final TextureRegion tile = tileSet.textures.get(tiles[y][x].texture);
                if (tiles[y][x].type == TileType.INTERIOR) {
                    batch.setColor(Color.LIGHT_GRAY);
                }
                batch.draw(tile, x * tileSet.tileSize, y * tileSet.tileSize);
                batch.setColor(Color.WHITE);
            }
        }

        final float tile_size = tileSet.tileSize;
        final float originX = tile_size / 2f;
        final float originY = tile_size / 2f;
        final float scaleX = 1f;
        final float scaleY = 1f;
        for (Building building : buildings.values()) {
            if (building == null) continue;

            TextureRegion tile = tileSet.textures.get(building.texture);
            final float positionX = building.x * tile_size;
            final float positionY = building.y * tile_size;
            final float angle_offset = -90f;
            float rotation = 0f;

            // TODO: refactor Building to handle their own drawing to avoid this sort of specialization
            if (building instanceof Tower) {
                rotation = MathUtils.atan2(GameplayScreen.mouseWorldPos.y - positionY,
                                           GameplayScreen.mouseWorldPos.x - positionX)
                         * MathUtils.radiansToDegrees + angle_offset;
            } else if (building instanceof Keep) {
                tile = ((Keep) building).keyframe;
            }

            batch.draw(tile, positionX, positionY, originX, originY, tile_size, tile_size, scaleX, scaleY, rotation);
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
        return towers;
    }

    public void setWall(int x, int y){
        buildings.put(x + y * width, new Wall(x, y));
    }

    public void setTower(int x, int y){
        final Tower tower = new Tower(x, y);
        towers.add(tower);
        buildings.put(x + y * width, tower);
    }

    public TileType getTileType(int x, int y){
        if (inBounds(x, y)){
            return tiles[y][x].type;
        }
        return null;
    }

    public void destroyBuildingAt(int x, int y){
        Building building = buildings.get(x + y * width);
        if (building != null) {
            buildings.put(x + y * width, null);
            if (building instanceof Tower) {
                towers.remove(building);
            }
        }
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
    ArrayList<Tile> tilesToCheck = new ArrayList<Tile>();
    ArrayList<Tile> checkedTiles = new ArrayList<Tile>();
    public void setInternal(){
        resetGround(TileType.INTERIOR);

        tilesToCheck.clear();
        checkedTiles.clear();

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

    // TODO: this could be made more efficient
    public boolean hasInternalTiles() {
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[0].length; ++x) {
                if (tiles[y][x].type == TileType.INTERIOR && getBuildingAt(x, y) == null) {
                    return true;
                }
            }
        }
        return false;
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


//
//        setTower(x - 1, y - 1);
//        setTower(x + 1, y - 1);
//        setTower(x - 1, y + 1);
//        setTower(x + 1, y + 1);
    }


}
