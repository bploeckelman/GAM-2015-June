package com.lando.systems.June15GAM;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class June15GAM extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;

    @Override
    public void create () {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    /** -----------------------------------------------------------------------
     * Game Constants
     * ------------------------------------------------------------------------ */

    public static final int win_width = 640;
    public static final int win_height = 480;
    public static final float win_aspect = (float) win_width / (float) win_height;
    public static final boolean win_resizeable = false;
    public static final String win_title = "GAM - June 2015";

}
