package com.lando.systems.June15GAM.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.buildings.CannonPlacer;
import com.lando.systems.June15GAM.buildings.Tower;
import com.lando.systems.June15GAM.effects.Effect;
import com.lando.systems.June15GAM.effects.EffectsManager;
import com.lando.systems.June15GAM.enemies.Ship;
import com.lando.systems.June15GAM.tilemap.*;
import com.lando.systems.June15GAM.wallpiece.WallPiece;
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
    EffectsManager     effectsManager;

    Rectangle          placeButtonRect;
    Rectangle          rotateButtonRect;

    int                numShips;
    boolean            phaseActive;
    float              phaseTimer;
    float              phaseEntryTimer;
    final float        cannonTimer = 15;
    final float        attackTimer = 30;
    final float        buildTimer = 25;
    final float        phaseEntryDelayTime = 1;
    final int          numShipsToAdd = 3;
    GlyphLayout layout = new GlyphLayout();

    public enum Gameplay {
        BUILD,
        CANNON,
        ATTACK
        // ???
        // PROFIT
    }

    Gameplay phase;
    int      turn;

    TileMap tileMap;
    Array<Ship> ships;

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
        camera = new OrthographicCamera(June15GAM.win_width, June15GAM.win_height);
        camera.translate(June15GAM.win_width / 2f, June15GAM.win_height / 2f);
        camera.update();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        effectsManager = new EffectsManager();

        phaseEntryTimer = 1;
        phase = Gameplay.CANNON;
        phaseTimer = cannonTimer;
        phaseActive = false;
        turn = 0;

        final TileSet tileSet = new TileSetOverhead();
        tileMap = new TileMap(tileSet,
                              (int) (camera.viewportWidth  / tileSet.tileSize),
                              (int) (camera.viewportHeight / tileSet.tileSize));

        activeCannonballs = new Array<Cannonball>(20);
        cannonballPool = Pools.get(Cannonball.class);

        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);

        Gdx.gl.glClearColor(0, 0, 0, 1);

        placeButtonRect = new Rectangle(camera.viewportWidth - 94, 30, 64, 64 );
        rotateButtonRect = new Rectangle(camera.viewportWidth - 94, 124, 64, 64 );
    }

    @Override
    public void render(float delta) {
        update(delta);

        sceneFrameBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw scene stuff
        tileMap.render(batch);
        effectsManager.render(batch);
        if (phase == Gameplay.ATTACK) {
            for (Ship ship : ships) {
                ship.render(batch);
            }
            for (Cannonball cannonball : activeCannonballs) {
                cannonball.render(batch);
            }
        }

        // Draw user interface overlays
        font.draw(batch, "Turn #" + turn + ": " + phase.name(), 10, camera.viewportHeight - 10);
        String timerString = "Time Left: " + (int)Math.max(Math.ceil(phaseTimer), 0);
        layout.setText(font, timerString);
        font.draw(batch, timerString, camera.viewportWidth - (layout.width + 30), camera.viewportHeight - 10);
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
        batch.draw(sceneRegion, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.end();

        // UI stuff
        batch.begin();
        if (phase != Gameplay.ATTACK) {
            batch.draw(Assets.placeButtonTexture, placeButtonRect.x, placeButtonRect.y, placeButtonRect.width, placeButtonRect.height);
            tileMap.tetris.render(tileMap, batch);
        }
        if (phase == Gameplay.BUILD)
            batch.draw(Assets.placeButtonTexture, rotateButtonRect.x, rotateButtonRect.y, rotateButtonRect.width, rotateButtonRect.height);

        if (!phaseActive){
            font.getData().setScale(5);
            layout.setText(font, phase.name() + " Phase!");
            font.draw(batch, phase.name() + " Phase!", (camera.viewportWidth - layout.width) / 2, (camera.viewportHeight + layout.height) /2);
            font.getData().setScale(2);
        }
        batch.end();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.exit();
        }

        if (phaseActive){
            phaseTimer -= delta;
            switch (phase) {
                case BUILD:  updateBuildPhase(delta);  break;
                case CANNON: updateCannonPhase(delta); break;
                case ATTACK: updateAttackPhase(delta); break;
            }
        } else {
            phaseEntryTimer -= delta;
            if (phaseEntryTimer <= 0) phaseActive = true;
        }

        effectsManager.update(delta);

        camera.update();
        updateMouseVectors(camera);
    }

    private void updateBuildPhase(float delta) {
        // TODO: switch to attack phase based on some condition (timer? done placing wall sections?)
        if (phaseTimer <= 0){
            phase = Gameplay.CANNON;
            phaseTimer = cannonTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
            tileMap.tetris = new CannonPlacer(3); // TODO: This should be based on something
        }
    }

    private void updateAttackPhase(float delta) {
        updateScene(delta);
        handleCollisions();
        updateGamePhase();
    }

    @Override
    public void resize(int width, int height) {
//        camera.setToOrtho(false, width, height);
//        camera.update();
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

    private void updateCannonPhase(float delta){
        if (!tileMap.hasInternalTiles()) {
            phaseTimer = 0;
        }
        if (phaseTimer <= 0 || tileMap.tetris.getNumberLeft() <= 0){
            spawnShips();
            phase = Gameplay.ATTACK;
            phaseTimer = attackTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
        }
    }

    private void updateScene(float delta) {
        // Update ships
        for (Ship ship : ships) {
            ship.update(delta);

            if (ship.reachedTarget()) {
                ship.setNewTarget(tileMap);
            }

            if (ship.canShoot()) {
                ship.shoot(tileMap, cannonballPool, activeCannonballs);
            }
        }

        // Update towers
        for (Tower tower : tileMap.getTowers()) {
            tower.update(delta);
        }

        // Update cannonballs
        for (Cannonball cannonball : activeCannonballs) {
            cannonball.update(delta);
        }
    }

    private void updateGamePhase() {
        // Update gameplay phase
        if (phaseTimer <= 0 && activeCannonballs.size == 0){
            phase = Gameplay.BUILD;
            phaseTimer = buildTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
            turn++;
            tileMap.tetris = new WallPiece();
        }
    }

    private void handleCollisions() {
        for (int i = activeCannonballs.size - 1; i >= 0; --i) {
            final Cannonball cannonball = activeCannonballs.get(i);
            // Only check the cannonball if it is alive and has reached its target
            if (!cannonball.alive || !cannonball.position.epsilonEquals(cannonball.target, tileMap.tileSet.tileSize / 4f)) {
                continue;
            }

            // Get the cannonballs tile position
            final int tx = (int) ((cannonball.position.x + cannonball.size.floatValue() / 2f) / tileMap.tileSet.tileSize);
            final int ty = (int) ((cannonball.position.y + cannonball.size.floatValue() / 2f) / tileMap.tileSet.tileSize);

            TileType hitTileType = tileMap.getTileType(tx, ty);
            if (hitTileType == null) {
                hitTileType = TileType.WATER;
            }

            // TODO: add different effect types for walls and towers and such
            Effect.Type effectType;
            if (hitTileType == TileType.GROUND) {
                effectType = Effect.Type.EXPLOSION_GROUND;
            } else {
                effectType = Effect.Type.EXPLOSION_WATER;
            }

            // Act differently based on the source of the cannonball
            if (cannonball.source == Cannonball.Source.SHIP) {
                handleGroundHit(cannonball, tx, ty, hitTileType, effectType);
            }
            else if (cannonball.source == Cannonball.Source.TOWER) {
                // Check for ship collisions
                boolean shipGotHit = false;
                for (int s = ships.size - 1; s >= 0; --s) {
                    final Ship ship = ships.get(s);
                    if (cannonball.position.epsilonEquals(ship.position, tileMap.tileSet.tileSize / 2f)) {
                        shipGotHit = true;
                        // TODO: moar kaboom
                        ships.removeIndex(s);
                        effectsManager.newEffect(effectType, cannonball.position.x, cannonball.position.y);
                    }
                }

                // Otherwise handle a ground collision
                if (!shipGotHit) {
                    handleGroundHit(cannonball, tx, ty, hitTileType, effectType);
                }
            }

            // Remove the cannonball
            cannonballPool.free(cannonball);
            activeCannonballs.removeIndex(i);
        }
    }

    private void handleGroundHit(Cannonball cannonball,
                                 int tileHitX,
                                 int tileHitY,
                                 TileType hitTileType,
                                 Effect.Type effectType) {
        tileMap.destroyBuildingAt(tileHitX, tileHitY);
        if (hitTileType != TileType.WATER) {
            tileMap.tiles[tileHitY][tileHitX].type = TileType.GROUND;
            tileMap.tiles[tileHitY][tileHitX].texture = TileTexture.GROUND_SAND;
        }
        effectsManager.newEffect(effectType, cannonball.position.x, cannonball.position.y);
    }

    private void spawnShips() {
        // NOTE: for now, assume that the north edge of the screen is always a body of water, spawn from any tile along that edge
        // TODO: generate spawn points on the tilemap and randomly pick from those when spawning
        if (ships == null) {
            ships = new Array<Ship>();
        } else {
            ships.clear();
        }

        numShips += numShipsToAdd;

        final float tile_size = tileMap.tileSet.tileSize;
        for (int i = 0; i < numShips; ++i) {
            int tx = MathUtils.random(1, tileMap.tiles[0].length - 2);
            int ty = tileMap.tiles.length - 1;
            Ship ship = new Ship(tx * tile_size, ty * tile_size, tile_size, tile_size);
            ships.add(ship);
        }
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
        if (phaseTimer <= 0) return true;
        if (button == 0) {
            switch (phase) {
                case BUILD:  tapBuild();  break;
                case CANNON: tapCannon(); break;
                case ATTACK: tapAttack();  break;
            }
        }
        return false;
    }

    private void tapBuild(){

        if (placeButtonRect.contains(mouseWorldPos.x, mouseWorldPos.y)){
            tileMap.tetris.place(tileMap);
            return;
        }
        if (rotateButtonRect.contains(mouseWorldPos.x, mouseWorldPos.y)){
            tileMap.tetris.rotate(WallPiece.R.C);
            return;
        }
        tileMap.tetris.place(tileMap);
    }

    private void tapCannon(){
        if (placeButtonRect.contains(mouseWorldPos.x, mouseWorldPos.y)){
            tileMap.tetris.place(tileMap);
            return;
        }
        tileMap.tetris.place(tileMap);
    }

    private void tapAttack(){
        final float tile_size = tileMap.tileSet.tileSize;
        final Vector2 towerPos = new Vector2();
        for (Tower tower : tileMap.getTowers()) {
            if (tower.canFire()) {
                tower.fire();
                towerPos.set(tower.x * tile_size + tile_size * 0.5f, tower.y * tile_size + tile_size * 0.5f);
                Cannonball cannonball = cannonballPool.obtain();
                cannonball.init(towerPos.x, towerPos.y, mouseWorldPos.x - tile_size, mouseWorldPos.y - tile_size);
                cannonball.source = Cannonball.Source.TOWER;
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
        if (phase == Gameplay.ATTACK) return false;
        float panScale = 1;
        if (tileMap.tetris != null){
            tileMap.tetris.addX(deltaX * panScale);
            tileMap.tetris.addY(-deltaY * panScale);
        }
        return true;
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
