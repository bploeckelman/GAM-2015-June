package com.lando.systems.June15GAM.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lando.systems.June15GAM.June15GAM;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = June15GAM.win_width/2;
        config.height = June15GAM.win_height/2;
        config.resizable = June15GAM.win_resizeable;
        config.title = June15GAM.win_title;
        new LwjglApplication(new June15GAM(), config);
    }
}
