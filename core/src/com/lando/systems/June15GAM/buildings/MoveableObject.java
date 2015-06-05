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
    private float xFloat = 15 * 16;
    private float yFloat = 6 * 16;
    protected int offsetX = 0;
    protected int offsetY = 0;
    protected int offsetMaxX = 1;
    protected int offsetMaxY = 1;

    public void render(TileMap map, SpriteBatch batch){
        batch.draw(Assets.weaponsTexture, getTileX() * 16, getTileY() * 16, 16, 16);
    }

    public int getTileX(){
        return (int)(xFloat/16); // Todo: make this not a magic number?
    }

    public int getTileY(){
        return (int)(yFloat/16); // Todo: make this not a magic number?
    }

    public void addX(float x){
        xFloat = MathUtils.clamp(xFloat + x, -(offsetX*16), June15GAM.win_width - (offsetMaxX * 16));
    }

    public void addY(float y){
        yFloat = MathUtils.clamp(yFloat + y, -(offsetY*16), June15GAM.win_height - (offsetMaxY * 16));
    }

    public boolean place(TileMap map){
        return false;
    }

    public void rotate(WallPiece.R r){

    }
}
