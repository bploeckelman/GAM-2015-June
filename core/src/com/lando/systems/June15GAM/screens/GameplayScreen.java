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
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.buildings.Building;
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

    // TODO: move to June15GAM class as static globals?
    public static Vector3 mouseScreenPos;
    public static Vector3 mouseWorldPos;

    final June15GAM    game;
    FrameBuffer        sceneFrameBuffer;
    SpriteBatch        batch;
    TextureRegion      sceneRegion;
    OrthographicCamera camera;
    BitmapFont         font;
    EffectsManager     effectsManager;
    Rectangle          intersection;

    Rectangle          placeButtonRect;
    Rectangle          rotateButtonRect;

    int                numShips;
    boolean            phaseActive;
    float              phaseTimer;
    float              phaseEntryTimer;
    float              noActionTimer;
    final float        cannonTimer = 15;
    final float        attackTimer = 30;
    final float        buildTimer = 25;
    final float        phaseEntryDelayTime = 2;
    final int          numShipsToAdd = 3;

    GlyphLayout layout = new GlyphLayout();

    float              deviceScaleX;
    float              deviceScaleY;
    public int         score;

    public enum Gameplay {
        BUILD,
        CANNON,
        ATTACK,
        GAMEOVER
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
        score = 0;
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
        intersection = new Rectangle();

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

        deviceScaleX =  camera.viewportWidth / Gdx.graphics.getWidth();
        deviceScaleY =  camera.viewportHeight / Gdx.graphics.getHeight();
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
                cannonball.renderShadow(batch);
            }
            for (Cannonball cannonball : activeCannonballs) {
                cannonball.render(batch);
            }
        }

        // Draw user interface overlays
//        font.draw(batch, "Turn #" + turn + ": " + phase.name(), 10, camera.viewportHeight - 10);
        String timerString = "Time Left: " + (int)Math.max(Math.ceil(phaseTimer), 0);
        layout.setText(font, timerString);
        font.draw(batch, timerString, camera.viewportWidth - (layout.width + 30), camera.viewportHeight - 10);

        String scoreString = "Score: " + score;
        //layout.setText(font, scoreString);
        font.draw(batch, scoreString, 30, camera.viewportHeight - 10);

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

        if (phase == Gameplay.GAMEOVER) {
            font.getData().setScale(3);
            final String text = "Game Over, You're Bad at Life!";
            layout.setText(font, text);
            font.draw(batch, text, (camera.viewportWidth - layout.width) / 2f, (camera.viewportHeight + layout.height) / 2f);
            font.getData().setScale(2);
        }

        if (!phaseActive && phase != Gameplay.GAMEOVER){
            font.getData().setScale(5);
            layout.setText(font, phase.name() + " Phase!");
            font.draw(batch, phase.name() + " Phase!", (camera.viewportWidth - layout.width) / 2, (camera.viewportHeight + layout.height) /2);
            font.getData().setScale(2);
            if (phaseEntryTimer <= 0) {
                layout.setText(font, "Touch to Start");
                font.draw(batch, "Touch to Start", (camera.viewportWidth - layout.width) / 2, (camera.viewportHeight + layout.height) / 2 - 50);
            }
        }
        batch.end();
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.exit();
        }
        noActionTimer -= delta;

        if (phaseActive){
            phaseTimer -= delta;
            switch (phase) {
                case BUILD:  updateBuildPhase(delta);  break;
                case CANNON: updateCannonPhase(delta); break;
                case ATTACK: updateAttackPhase(delta); break;
            }
        } else {
            phaseEntryTimer -= delta;
            if (phaseEntryTimer <= 0 && Gdx.input.justTouched()) {
                phaseActive = true;
                noActionTimer = .2f;
            }
        }

        effectsManager.update(delta);

        camera.update();
        updateMouseVectors(camera);
    }

    private void updateBuildPhase(float delta) {
        // TODO: switch to attack phase based on some condition (timer? done placing wall sections?)
        if (phaseTimer <= 0){
            tileMap.setInternal();
            if (!tileMap.isGameLost()) {
                phase = Gameplay.CANNON;
                phaseTimer = cannonTimer;
                tileMap.tetris = new CannonPlacer(tileMap); // TODO: should be based on number of interior tiles
                clearCannonballs();
                resetCannons();
            } else {
                phase = Gameplay.GAMEOVER;
                // TODO: maybe do some other things here?
            }
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
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
        if (phaseTimer <= 0 || tileMap.tetris.getNumberLeft() <= 0){
            spawnShips();
            phase = Gameplay.ATTACK;
            phaseTimer = attackTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
            clearCannonballs();
            resetCannons();
        }
    }

    private void updateScene(float delta) {
        // Update ships
        for (Ship ship : ships) {
            ship.update(delta);


            if (ship.canShoot() && phaseTimer > 0f) {
                ship.shoot(tileMap, cannonballPool, activeCannonballs);
            }
        }

        // Update buildings
        for (Building building : tileMap.buildings.values()) {
            if (building == null) continue;
            building.update(delta);
        }

        // Update cannonballs
        for (Cannonball cannonball : activeCannonballs) {
            cannonball.update(delta);
        }
    }

    private void updateGamePhase() {
        // Update gameplay phase
        if ((phaseTimer <= 0 && activeCannonballs.size == 0) || ships.size == 0){
            tileMap.setInternal();
            phase = Gameplay.BUILD;
            phaseTimer = buildTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
            turn++;
            tileMap.tetris = new WallPiece();
            clearCannonballs();
            resetCannons();
        }
    }

    private void handleCollisions() {
        for (int i = activeCannonballs.size - 1; i >= 0; --i) {
            final Cannonball cannonball = activeCannonballs.get(i);
            // Only check the cannonball if it is alive and has reached its target
            if (!cannonball.alive || !cannonball.didHitTarget()) {
                continue;
            }

            // Get the cannonballs tile position
            final int tx = (int) (cannonball.position.x / tileMap.tileSet.tileSize);
            final int ty = (int) (cannonball.position.y / tileMap.tileSet.tileSize);

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
                    if (Intersector.intersectRectangles(cannonball.bounds, ship.bounds, intersection)) {
                        shipGotHit = true;
                        // TODO: moar kaboom
                        ships.removeIndex(s);
                        score += ship.score;
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
            tileMap.tiles[tileHitY][tileHitX].texture = TileTexture.GROUND_GRASS;
        }
        effectsManager.newEffect(effectType, cannonball.target.x, cannonball.target.y);
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
            float w = tile_size * 1.5f;
            float h = tile_size * 1.5f;
            Ship ship = new Ship(tileMap, tx * tile_size, ty * tile_size, w, h);
            ships.add(ship);
        }
    }

    private void clearCannonballs() {
        for (Cannonball cannonball : activeCannonballs) {
            cannonballPool.free(cannonball);
        }
        activeCannonballs.clear();
    }

    private void resetCannons() {
        for (Tower tower : tileMap.getTowers()) {
            tower.enableShot();
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
        if (phaseTimer <= 0 || !phaseActive || noActionTimer > 0) return true;
        // TODO: pass button input values into phase tap handlers and do conditional checks on button there instead
        if (button == 0) {
            switch (phase) {
                case BUILD:  tapBuild();  break;
                case CANNON: tapCannon(); break;
                case ATTACK: tapAttack();  break;
            }
        } else if (button == 1 && phase == Gameplay.BUILD) {
            tileMap.tetris.rotate(WallPiece.R.C);
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
        // Disable attacking if phase is over
        if (phaseTimer <= 0f) {
            return;
        }

        final float tile_size = tileMap.tileSet.tileSize;
        final float half_tile_size = tile_size / 2f;
        final float speed = 75f;

        for (Tower tower : tileMap.getTowers()) {
            if (tower.canFire()) {
                tower.fire();
                tileMap.getTowers().remove(tower);
                tileMap.getTowers().addLast(tower);

                Cannonball cannonball = cannonballPool.obtain();
                cannonball.init(tower.x * tile_size + half_tile_size / 2f,
                                tower.y * tile_size + half_tile_size / 2f,
                                mouseWorldPos.x,
                                mouseWorldPos.y,
                                half_tile_size, half_tile_size,
                                speed);
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
        if (!phaseActive) return false;
        if (phase == Gameplay.ATTACK) return false;
        float panScale = 1;
        if (tileMap.tetris != null){
            tileMap.tetris.addX(deltaX * deviceScaleX * panScale);
            tileMap.tetris.addY(-deltaY * deviceScaleY * panScale);
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
