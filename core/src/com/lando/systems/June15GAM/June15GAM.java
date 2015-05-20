package com.lando.systems.June15GAM;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.lando.systems.June15GAM.screens.GameplayScreen;
import com.lando.systems.June15GAM.screens.TestScreen;

public class June15GAM extends Game {
    TestScreen testScreen;

    @Override
    public void create() {
        // TODO make this main menu later
        setScreen(new GameplayScreen());
    }

    @Override
    public void render(){
        float dt = Gdx.graphics.getDeltaTime();
        // TODO add tween manager here.

        super.render();
    }

    /** -----------------------------------------------------------------------
     * Game Constants
     * ------------------------------------------------------------------------ */

    public static final int win_width = 720;
    public static final int win_height = 1080;
    public static final float win_aspect = (float) win_width / (float) win_height;
    public static final boolean win_resizeable = false;
    public static final String win_title = "GAM - June 2015";

}
