package com.lando.systems.June15GAM.enemies;

import com.badlogic.gdx.math.Vector2;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileType;

import java.util.ArrayList;

/**
 * Created by Karla on 6/25/2015.
 */
public class Pathfinder {

    private static ArrayList<BreadCrumb> availableBreadCrumbs = new ArrayList<BreadCrumb>();


    public static BreadCrumb FindPath(TileMap world, int startX, int startY, int endX, int endY)
    {
        //note we just flip start and end here so you don't have to.
        return FindPathReversed(world, endX, endY, startX, startY);
    }

    private static BreadCrumb FindPathReversed(TileMap world, int startX, int startY, int endX, int endY)
    {
        MinHeap<BreadCrumb> openList = new MinHeap<BreadCrumb>(256);
        ArrayList<BreadCrumb> closedList = new ArrayList<BreadCrumb>();
        BreadCrumb node;

        BreadCrumb current = getBreadCrumb(startX, startY);

        if (startX == endX && startY == endY) return current;
        current.cost = 0;
        current.gScore = 0;

        BreadCrumb finish = getBreadCrumb(endX, endY);

        openList.add(current);

        while (openList.count() > 0)
        {
            //Find best item and switch it to the 'closedList'
            current = openList.extractFirst();

            closedList.add(current);

            for (int i = 0; i < 9; i++)
            {
                //if (i == 4) continue; // don't check center;

                int tempX = current.x + (i / 3) -1;
                int tempY = current.y + (i % 3) -1;

                if (tempX < 0 || tempX >= world.width || tempY < 0 || tempY >= world.height) continue;

                node = getBreadCrumb(tempX, tempY); // May be overkill but it is GAM
                availableBreadCrumbs.add(node);
                if (closedList.contains(node))
                {
                    continue; // already checked move on
                }

                if (world.getTileType(tempX, tempY) != TileType.WATER){
                    closedList.add(node);
                    continue; // Add it to unwalkable and move on
                }

                // Check for corner cutting
                if (i == 0 && world.getTileType(tempX + 1, tempY) != TileType.WATER && world.getTileType(tempX, tempY + 1) != TileType.WATER){
                    continue; // can't move diagonal here
                }
                if (i == 2 && world.getTileType(tempX - 1, tempY) != TileType.WATER && world.getTileType(tempX, tempY + 1) != TileType.WATER){
                    continue; // can't move diagonal here
                }
                if (i == 6 && world.getTileType(tempX + 1, tempY) != TileType.WATER && world.getTileType(tempX, tempY - 1) != TileType.WATER){
                    continue; // can't move diagonal here
                }
                if (i == 8 && world.getTileType(tempX - 1, tempY) != TileType.WATER && world.getTileType(tempX, tempY - 1) != TileType.WATER){
                    continue; // can't move diagonal here
                }

                double movementCost = 10;
                if (i == 0 || i == 2 || i == 6 || i == 8){
                    movementCost = 14; // Root of 2
                }
                double tentativeGScore = current.gScore + movementCost;

                node = openList.find(node);
                if (node == null || tentativeGScore < node.gScore)
                {
                    if (node == null) node = getBreadCrumb(tempX, tempY);
                    node.next = current;
                    node.gScore = tentativeGScore;
                    node.cost = tentativeGScore + GetHeuristic(node, finish);

                    if (node.equals(finish))
                    {
                        return node;
                    }
                    if (!openList.contains(node))
                    {
                        openList.add(node);
                    }
                }
            }

        }
        return null; //no path found
    }

    private static BreadCrumb getBreadCrumb(int x, int y)
    {
        if (availableBreadCrumbs.size() > 0){
            BreadCrumb tmpBreadCrumb = availableBreadCrumbs.remove(0);
            tmpBreadCrumb.reset(x, y);
            return tmpBreadCrumb;
        }
        else {
            return new BreadCrumb(x, y);
        }
    }


    private static double GetHeuristic(BreadCrumb start, BreadCrumb end)
    {
        return Math.abs(start.x - end.x) * 10 + Math.abs(start.y - end.y) * 10;
//    	return ((start.position.x - end.position.x)*(start.position.x - end.position.x)) +
//    		   ((start.position.y - end.position.y)*(start.position.y - end.position.y));
    }
}
