package com.lando.systems.June15GAM.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.cameras.OrthoCamController;
import com.lando.systems.June15GAM.enemies.Ship;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileTexture;

/**
 * Created by Doug on 5/19/2015.
 */
public class GameplayScreen extends ScreenAdapter {

    Vector3            mouseScreenPos;
    Vector3            mouseWorldPos;
    FrameBuffer        sceneFrameBuffer;
    SpriteBatch        batch;
    TextureRegion      sceneRegion;
    OrthographicCamera camera;
    OrthographicCamera screenCamera;
    OrthoCamController camController;
    //UserInterface      userInterface;
    BitmapFont         font;

    public enum Gameplay {
        BUILD,
        ATTACK
        // ???
        // PROFIT
    }

    Gameplay phase;
    int      turn;

    TileMap tileMap;
    Ship    ship;
    Texture shipTexture; // TODO: move to an assets class

    public GameplayScreen() {
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
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        phase = Gameplay.BUILD;
        turn = 0;

        tileMap = new TileMap(50, 50);

        // TODO: move to assets class
        shipTexture = new Texture("fantasy-sprites.png");
        TextureRegion[][] regions = TextureRegion.split(shipTexture, 16, 16);

        // TODO: generate ships randomly from water edges?
        ship = new Ship(2000, 2000);
        ship.animation = new Animation(Ship.FRAME_DURATION,
                                       regions[1][2],
                                       regions[1][3]);
        ship.animation.setPlayMode(Animation.PlayMode.LOOP);

        final InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(camController);
        Gdx.input.setInputProcessor(mux);

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
        if (phase == Gameplay.ATTACK) {
            ship.render(batch);
        }
        batch.end();
        sceneFrameBuffer.end();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(screenCamera.combined);
        batch.begin();
        batch.draw(sceneRegion, 0, 0);
        font.draw(batch, "Turn #" + turn + ": " + phase.name(), 10, screenCamera.viewportHeight - 10);
        batch.end();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        switch (phase) {
            case BUILD:  updateBuild(delta);  break;
            case ATTACK: updateAttack(delta); break;
        }

        camera.update();
        updateMouseVectors(camera);
    }

    private void updateBuild(float delta) {
        // TODO: switch to attack phase based on some condition (timer? done placing wall sections?)
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            phase = Gameplay.ATTACK;
        }
    }

    private void updateAttack(float delta) {
        // TODO: switch to build phase based on some condition (timer? out of ammunition?)
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            phase = Gameplay.BUILD;
            ++turn;
        }

        ship.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause() {
        // Disable input
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resume() {
        // Enable input
        final InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(camController);
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void dispose() {
        shipTexture.dispose();
        font.dispose();
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
