package com.lando.systems.June15GAM.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lando.systems.June15GAM.Assets;

/**
 * Created by Karla on 6/4/2015.
 */
public class MoveableObject {
    public float xFloat = 15 * 16;
    public float yFloat = 6 * 16;


    public void render(SpriteBatch batch){
        batch.draw(Assets.weaponsTexture, (int)(xFloat / 16) * 16, (int)(yFloat / 16) * 16, 16, 16);
    }
}
