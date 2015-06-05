package com.lando.systems.June15GAM.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.buildings.Tower;
import com.lando.systems.June15GAM.enemies.Ship;
import com.lando.systems.June15GAM.tilemap.TileMap;
import com.lando.systems.June15GAM.tilemap.TileType;
import com.lando.systems.June15GAM.weapons.Cannonball;

/**
 * Created by Doug on 5/19/2015.
 */
public class GameplayScreen extends ScreenAdapter implements GestureDetector.GestureListener {

    final June15GAM    game;
    Vector3            mouseScreenPos;
    Vector3            mouseWorldPos;
    FrameBuffer        sceneFrameBuffer;
    SpriteBatch        batch;
    TextureRegion      sceneRegion;
    OrthographicCamera camera;
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

    Array<Cannonball> activeCannonballs;
    Pool<Cannonball>  cannonballPool;

    public GameplayScreen(June15GAM game) {
        this.game = game;

        mouseScreenPos = new Vector3();
        mouseWorldPos = new Vector3();
        sceneFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, June15GAM.win_width, June15GAM.win_height, false);
        batch = new SpriteBatch();
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, June15GAM.win_width, June15GAM.win_height);
        camera.update();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        phase = Gameplay.BUILD;
        turn = 0;

        // TODO: tilesize should come from tileset which is loaded in the tilemap, figure out a better way
        final float TILE_SIZE = 16;
        tileMap = new TileMap((int) (camera.viewportWidth  / TILE_SIZE),
                              (int) (camera.viewportHeight / TILE_SIZE));

        // TODO: generate ships randomly from water edges?
        ship = new Ship(2000, 2000);

        activeCannonballs = new Array<Cannonball>(20);
        cannonballPool = Pools.get(Cannonball.class);

        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);

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
            for (Cannonball cannonball : activeCannonballs) {
                cannonball.render(batch);
            }
        }
        font.draw(batch, "Turn #" + turn + ": " + phase.name(), 10, camera.viewportHeight - 10);
        float x = 10f;
        int i = 1;
        for (Tower tower : tileMap.getTowers()) {
            if (tower.canFire()) {
                font.draw(batch, "" + i, x, 32);
            }
            x += 32f;
            ++i;
        }
        batch.end();
        sceneFrameBuffer.end();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(sceneRegion, 0, 0, camera.viewportWidth, camera.viewportHeight); // TODO Something is messed up with how android is drawing this
        batch.end();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.exit();
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

        for (Tower tower : tileMap.getTowers()) {
            tower.update(delta);
        }

        for (int i = activeCannonballs.size - 1; i >= 0; --i) {
            Cannonball cannonball = activeCannonballs.get(i);
            if (cannonball.position.epsilonEquals(cannonball.target, tileMap.tileSet.tileSize / 2f)) {
                cannonballPool.free(cannonball);
                activeCannonballs.removeIndex(i);
                // TODO: kaboom
            } else {
                cannonball.update(delta);
            }
        }
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
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);

    }

    @Override
    public void dispose() {
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

    // ------------------------------------------------------------------------
    // InputProcessor Interface
    // ------------------------------------------------------------------------

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (button == 0) {
            switch (phase) {
                case BUILD:  tapBuild();  break;
                case ATTACK: tapAttack();  break;
            }
        }
        return false;
    }

    private void tapBuild(){
    //  TODO: this is debug
        int x = (int) (mouseWorldPos.x / tileMap.tileSet.tileSize);
        int y = (int) (mouseWorldPos.y / tileMap.tileSet.tileSize);
        if (tileMap.getTileType( x, y) != TileType.WATER){
            if (tileMap.getBuildingAt(x, y) == null) tileMap.setWall(x, y);
            else tileMap.destroyBuildingAt(x,y);
            tileMap.setInternal();
        }
    }

    private void tapAttack(){
        final float tile_size = tileMap.tileSet.tileSize;
        final Vector2 towerPos = new Vector2();
        for (Tower tower : tileMap.getTowers()) {
            if (tower.canFire()) {
                tower.fire();
                towerPos.set(tower.x * tile_size + tile_size * 0.5f, tower.y * tile_size + tile_size * 0.5f);
                Cannonball cannonball = cannonballPool.obtain();
                cannonball.init(towerPos.x, towerPos.y, mouseWorldPos.x, mouseWorldPos.y);
                activeCannonballs.add(cannonball);
                break;
            }
        }
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        float panScale = 1;
        if (tileMap.tetris != null){
            tileMap.tetris.xFloat += deltaX * panScale;
            tileMap.tetris.yFloat -= deltaY * panScale;
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }





}
