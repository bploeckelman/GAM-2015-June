package com.lando.systems.June15GAM.wallpiece;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.lando.systems.June15GAM.buildings.MoveableObject;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileTexture;

public class WallPiece extends MoveableObject{

    public enum R {
        C, CC
    }

    private boolean[][] pieceMap;

    public WallPiece () {
        createNewPiece();

    }

    public boolean isValidPlacement(TileMap tileMap) {
        for (int row = 0; row < pieceMap.length; row++){
            for (int col = 0; col < pieceMap[0].length; col++){
                if (pieceMap[row][col]){
                    if (tileMap.getBuildingAt(getTileX() + row, getTileY() + col) != null) return false;
                }
            }
        }
        return true;

    }

    public boolean place(TileMap map){
        if (isValidPlacement(map)){
            for (int row = 0; row < pieceMap.length; row++){
                for (int col = 0; col < pieceMap[0].length; col++){
                    if (pieceMap[row][col]){
                        map.setWall(getTileX() + row, getTileY() + col);
                    }
                }
            }
            createNewPiece();
            return true;
        }
        return false;
    }

    public void render(TileMap map, SpriteBatch batch){
        if (isValidPlacement(map)) batch.setColor(1,1,1,.5f);
        else batch.setColor(1,0,0,.5f);
        for (int row = 0; row < pieceMap.length; row++){
            for (int col = 0; col < pieceMap[0].length; col++){
                if (pieceMap[row][col]){
                    final TextureRegion tile = map.tileSet.textures.get(TileTexture.GROUND_CLAY);
                    batch.draw(tile, (getTileX() + row) * map.tileSet.tileSize, (getTileY() + col) * map.tileSet.tileSize, map.tileSet.tileSize, map.tileSet.tileSize);
                }
            }
        }
        batch.setColor(Color.WHITE);
    }

//    public void rotate(R r) {
//        int cW = this.pieceMap.length;
//        int cH = this.pieceMap[0].length;
//        if (r == R.C) {
//            boolean[][] rotatedPieceMap = new boolean[cH][cW];
//            int col, row;
//            for (col = 0; col < cW; col++) {
//                for (row = 0; row < cH; row++) {
//                    rotatedPieceMap[row][cW - col - 1]  = this.pieceMap[col][row];
//                }
//            }
//            this.pieceMap = rotatedPieceMap;
//        } else {
//            rotate(R.C);
//            rotate(R.C);
//            rotate(R.C);
//        }
//    }

    public void rotate(R r) {
        int cols = this.pieceMap.length;
        int rows = this.pieceMap[0].length;
        boolean[][] rotatedPieceMap = new boolean[rows][cols];
        int row, col;
        if (r == R.C) {
            for (col = 0; col < cols; col++) {
                for (row = 0; row < rows; row++) {
                    rotatedPieceMap[rows - row - 1][col] = this.pieceMap[col][row];
                }
            }
        } else {
            for (col = 0; col < cols; col++) {
                for (row = 0; row < rows; row++) {
                    rotatedPieceMap[row][col] = this.pieceMap[col][row];
                }
            }
        }
        this.pieceMap = rotatedPieceMap;
    }

    private void createNewPiece(){
        switch (MathUtils.random(2)){
            case 0: makeFourLine(); break;
            case 1: makeTwoLine(); break;
            case 2: makeT(); break;
        }
    }

    private void makeFourLine(){
        pieceMap = new boolean[4][4];
        pieceMap[1][0] = true;
        pieceMap[1][1] = true;
        pieceMap[1][2] = true;
        pieceMap[1][3] = true;
    }

    private void makeTwoLine(){
        pieceMap = new boolean[4][4];
        pieceMap[1][1] = true;
        pieceMap[1][2] = true;
    }

    private void makeT(){
        pieceMap = new boolean[4][4];
        pieceMap[1][1] = true;
        pieceMap[2][1] = true;
        pieceMap[3][1] = true;
        pieceMap[2][2] = true;
    }

    /*

    XXX
    X00
    X00
    X00

    X000
    X000
    XXXX

    XXXX
    000X
    000X

     */

    public void printDebug() {
        int cols = this.pieceMap.length;
        int rows = this.pieceMap[0].length;
        String d = "";
//        for (int col = 0; col < cols; col++) {
//            for (int row = rows - 1; row >= 0; row--) {
//                d += this.pieceMap[col][row] ? 'X' : '0';
//            }
//            d += "\n";
//        }
        int row, col;
        for (row = 0; row < rows; row++) {
            for (col = 0; col < cols; col++) {
                d += this.pieceMap[col][row] ? 'X' : '0';
            }
            d += "\n";
        }
        Gdx.app.log("WallPiece Print Debug:", "\n" + d);
    }

}
