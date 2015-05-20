package com.lando.systems.June15GAM;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lando.systems.June15GAM.accessors.*;
import com.lando.systems.June15GAM.screens.GameplayScreen;
import com.lando.systems.June15GAM.screens.TestScreen;

public class June15GAM extends Game {
    public static TweenManager tween;

    TestScreen testScreen;

    @Override
    public void create() {
        tween = new TweenManager();
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());

        // TODO make this main menu later
        setScreen(new GameplayScreen());
    }

    @Override
    public void render(){
        float dt = Gdx.graphics.getDeltaTime();
        tween.update(dt);

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
