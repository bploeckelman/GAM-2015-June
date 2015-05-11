package com.lando.systems.June15GAM;

import com.badlogic.gdx.Game;
import com.lando.systems.June15GAM.screens.TestScreen;

public class June15GAM extends Game {
    TestScreen testScreen;

    @Override
    public void create() {
        testScreen = new TestScreen(this);
        setScreen(testScreen);
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
