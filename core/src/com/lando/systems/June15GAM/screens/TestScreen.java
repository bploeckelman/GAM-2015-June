package com.lando.systems.June15GAM.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.cameras.OrthoCamController;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileSet;
import com.lando.systems.June15GAM.tilemap.TileSetOverhead;
import com.lando.systems.June15GAM.tilemap.TileType;

/**
 * Brian Ploeckelman created on 5/10/2015.
 */
public class TestScreen extends ScreenAdapter {

    Vector3            mouseScreenPos;
    Vector3            mouseWorldPos;
    FrameBuffer        sceneFrameBuffer;
    SpriteBatch        batch;
    TextureRegion      sceneRegion;
    OrthographicCamera camera;
    OrthographicCamera screenCamera;
    OrthoCamController camController;

    TileMap tileMap;

    public TestScreen(June15GAM game) {
        mouseScreenPos = new Vector3();
        mouseWorldPos = new Vector3();
        sceneFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, June15GAM.win_width, June15GAM.win_height, false);
        batch = new SpriteBatch();
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, June15GAM.win_width, June15GAM.win_height);
        camera.update();
        screenCamera = new OrthographicCamera();
        screenCamera.setToOrtho(false, June15GAM.win_width, June15GAM.win_height);
        screenCamera.update();

        camController = new OrthoCamController(camera);
        Gdx.input.setInputProcessor(camController);

        tileMap = new TileMap(50, 50);

        Gdx.gl.glClearColor(0, 0, 0, 1);
    }

    @Override
    public void render(float delta) {
        update(delta);

        sceneFrameBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        tileMap.render(batch);
        final TextureRegion selectTex = tileMap.tileSet.textures.get(TileType.ROAD_FOURWAY);
        batch.draw(selectTex, MathUtils.floor(mouseWorldPos.x / 64) * 64, MathUtils.floor(mouseWorldPos.y / 64) * 64);
        batch.end();
        sceneFrameBuffer.end();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(screenCamera.combined);
        batch.begin();
        batch.draw(sceneRegion, 0, 0);
        batch.end();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int x = MathUtils.floor(mouseWorldPos.x / 64);
            int y = MathUtils.floor(mouseWorldPos.y / 64);
            if (x >= 0 && x < tileMap.tiles[0].length
             && y >= 0 && y < tileMap.tiles.length) {
                tileMap.tiles[y][x] = TileType.GROUND_WATER;
            }
        }
        camera.update();
        updateMouseVectors(camera);
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
        batch.dispose();
        sceneFrameBuffer.dispose();
    }

    private void updateMouseVectors(Camera camera) {
        float mx = Gdx.input.getX();
        float my = Gdx.input.getY();
        mouseScreenPos.set(mx, my, 0);
        mouseWorldPos.set(mx, my, 0);
        camera.unproject(mouseWorldPos);
    }

}
