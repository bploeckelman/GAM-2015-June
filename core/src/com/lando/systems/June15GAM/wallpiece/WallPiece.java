package com.lando.systems.June15GAM.wallpiece;

import com.badlogic.gdx.Gdx;
import com.lando.systems.June15GAM.tilemap.TileMap;

public class WallPiece {

    public enum R {
        C, CC
    }

    private boolean[][] pieceMap;
    private int originX;
    private int originY;
    private WallPieceDirection dir;

    public WallPiece (boolean[][] pieceMap, int originX, int originY) {
        // Validate
        if (pieceMap.length == 0) {
            throw new RuntimeException("Invalid input; pieceMap must not be empty");
        }

        int pieceMapHeight = pieceMap[0].length;
        for (int i = 1; i < pieceMap.length; i++) {
            if (pieceMap[i].length != pieceMapHeight) {
                throw new RuntimeException("Invalid input; pieceMap columns must all be of same length");
            }
        }
        this.pieceMap = pieceMap;
        this.originX = originX;
        this.originY = originY;
    }

    public boolean isValidPlacement(TileMap tileMap, int mapX, int mapY) {

        return true;

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
