package com.lando.systems.June15GAM.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.lando.systems.June15GAM.June15GAM;

/**
 * Brian Ploeckelman created on 5/10/2015.
 */
public class TestScreen extends ScreenAdapter {

    FrameBuffer        sceneFrameBuffer;
    SpriteBatch        batch;
    Texture            img;
    TextureAtlas       atlas;
    TextureRegion      sceneRegion;
    OrthographicCamera camera;

    public TestScreen(June15GAM game) {
        sceneFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, June15GAM.win_width, June15GAM.win_height, false);
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        atlas = new TextureAtlas("spritesheets/kenney-overhead-tiles.atlas");
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, June15GAM.win_width, June15GAM.win_height);
        camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        sceneFrameBuffer.begin();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = 0; i < 70; ++i) {
            final TextureRegion region = atlas.getRegions().get(i);
            batch.draw(region,
                       region.getRegionWidth() * (i % 10),
                       region.getRegionHeight() * MathUtils.floor(i / 10) + region.getRegionHeight() / 4f);
        }
        batch.end();
        sceneFrameBuffer.end();

        batch.begin();
        batch.draw(sceneRegion, 0, 0);
        batch.end();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause() {
        // TODO: disable input
    }

    @Override
    public void resume() {
        // TODO: enable input
    }

    @Override
    public void dispose() {
        atlas.dispose();
        img.dispose();
        batch.dispose();
        sceneFrameBuffer.dispose();
    }

}
