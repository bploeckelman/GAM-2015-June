package com.lando.systems.June15GAM.enemies;

import com.badlogic.gdx.math.Vector2;

public class BreadCrumb {
    public int x;
    public int y;
    public Vector2 pos;
    public BreadCrumb next;
    public double cost = Double.MAX_VALUE;
    public double gScore = 0;



    public BreadCrumb(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.pos = new Vector2(x, y);
    }

    public void reset(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.pos = new Vector2(x, y);
        this.next = null;
        this.cost = Double.MAX_VALUE;
        this.gScore = 0;
    }

    public int GetLength(){
        int dist =0;
        BreadCrumb current = this;
        while (current.next != null)
        {
            dist++;
            current = current.next;
        }
        return dist;
    }

    public boolean equals(Object obj)
    {
        return (obj.getClass().equals(BreadCrumb.class)) && this.equals((BreadCrumb)obj);
    }

    public boolean equals(BreadCrumb breadcrumb)
    {
        return (breadcrumb.x == this.x && breadcrumb.y == this.y);
    }

    public int compareTo(BreadCrumb another) {
        return Double.compare(cost, another.cost) ;
    }
}
