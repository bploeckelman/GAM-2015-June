package com.lando.systems.June15GAM.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.lando.systems.June15GAM.June15GAM;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(June15GAM.win_width, June15GAM.win_height);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new June15GAM();
        }
}