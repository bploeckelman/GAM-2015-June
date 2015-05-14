package com.lando.systems.June15GAM.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.lando.systems.June15GAM.tilemap.TileSet;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Brian Ploeckelman created on 5/12/2015.
 */
public class UserInterface implements InputProcessor {

    public TileTexture[] tileTextures;
    public TileSet       tileSet;
    public int           selected;

    public UserInterface() {
        int i = 0;
        tileTextures = new TileTexture[TileTexture.values().length];
        for (TileTexture type : TileTexture.values()) {
            tileTextures[i++] = type;
        }
        selected = 0;
    }

    public void update(float delta) {

    }

    public void render(SpriteBatch batch) {
        final int num_tiles_on_screen = 11;
        final float tile_size = 32;
        final float margin = 10;
        final float offset = 10;

        // Draw a background for the tile selection tray
        batch.setColor(0.9f, 0.9f, 0.9f, 0.85f);
        batch.draw(tileSet.textures.get(TileTexture.ROAD_LINE_THICK_E), 0, 0, 4 * margin + tile_size, 480);

        // Draw a highlight for the currently selected tile texture in the middle of the tray
        batch.setColor(1, 1, 1, 1);
        batch.draw(tileSet.textures.get(TileTexture.GROUND_CLAY),
                   0,
                   offset / 2f + (num_tiles_on_screen / 2) * (tile_size + margin),
                   2.5f * margin + tile_size,
                   offset / 2f + tile_size + margin / 2f);

        // Draw all the tile textures in the selection tray
        batch.setColor(1, 1, 1, 1);
        final int iStart = MathUtils.clamp(selected - num_tiles_on_screen / 2 - 1, 0, tileTextures.length - 1);
        for (int i = iStart; i < tileTextures.length && i < selected + num_tiles_on_screen / 2 + 1; ++i) {
            final int yIndex = MathUtils.clamp(i - selected + num_tiles_on_screen / 2, 0, tileTextures.length - 1);
            final TextureRegion tile = tileSet.textures.get(tileTextures[i]);
            batch.draw(tile, margin, yIndex * (tile_size + margin) + offset, tile_size, tile_size);
        }
    }

    public TextureRegion getSelectedTileTextureRegion() {
        return tileSet.textures.get(tileTextures[selected]);
    }

    public TileTexture getSelectedTileTexture() {
        return tileTextures[selected];
    }

    // ------------------------------------------------------------------------
    // InputProcessor interface
    // ------------------------------------------------------------------------

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // Change the currently selected tile texture with ctrl+scroll
        if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            return false;
        }
        if      (amount > 0) ++selected;
        else if (amount < 0) --selected;
        selected = MathUtils.clamp(selected, 0, tileTextures.length - 1);

        return true;
    }

}
