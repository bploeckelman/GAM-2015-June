package com.lando.systems.June15GAM.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.wallpiece.WallPiece;

/**
 * Created by Karla on 6/4/2015.
 */
public class MoveableObject {
    private float xFloat = 10 * 24;
    private float yFloat = 16 * 24;
    protected int offsetX = 0;
    protected int offsetY = 0;
    protected int offsetMaxX = 1;
    protected int offsetMaxY = 1;
    TileMap world;
    protected float animationTimer = 0;

    public MoveableObject(TileMap world){
        this.world = world;
        animationTimer = 0;
    }

    public void render(TileMap map, SpriteBatch batch){
        batch.draw(Assets.weaponsTexture, getTileX() * world.tileSet.tileSize, getTileY() * world.tileSet.tileSize, world.tileSet.tileSize, world.tileSet.tileSize);
    }

    public void update(float dt){
        animationTimer += dt;
    }

    public int getTileX(){
        return (int)(xFloat/world.tileSet.tileSize); // Todo: make this not a magic number?
    }

    public int getTileY(){
        return (int)(yFloat/world.tileSet.tileSize); // Todo: make this not a magic number?
    }

    public void addX(float x){
        xFloat = MathUtils.clamp(xFloat + x, -(offsetX*world.tileSet.tileSize), June15GAM.win_width - (offsetMaxX * world.tileSet.tileSize));
    }

    public void addY(float y){
        yFloat = MathUtils.clamp(yFloat + y, -(offsetY*world.tileSet.tileSize), June15GAM.win_height - (offsetMaxY * world.tileSet.tileSize));
    }

    public boolean place(TileMap map){
        return false;
    }

    public void rotate(WallPiece.R r){

    }
    
    public int getNumberLeft(){
        return 0;
    }
}
