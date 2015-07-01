package com.lando.systems.June15GAM.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.primitives.MutableFloat;
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
import com.lando.systems.June15GAM.cameras.Shake;
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

    private static final float SHAKE_AMOUNT_SHIP_DESTROYED = 0.4f;
    private static final float SHAKE_AMOUNT_GROUND_HIT     = 0.2f;
    private static final float SHAKE_AMOUNT_PLAYER_FIRES   = 0.15f;
    private static final float SHAKE_AMOUNT_PLACE_WALL     = 0.2f;
    private static final float SHAKE_AMOUNT_PLACE_CANNON   = 0.3f;

    private static final float FONT_SCALE_DEFAULT  = 1f;
    private static final float FONT_SCALE_TIMER    = 0.9f;
    private static final float FONT_SCALE_POINTS   = 0.8f;
    private static final float FONT_SCALE_PHASE    = 1.5f;
    private static final float FONT_SCALE_GAMEOVER = 1.5f;
    private static final float FONT_SCALE_TOUCH    = 0.5f;

    private static final float PHASE_OFFSET_MIN = -June15GAM.win_width;

    // TODO: move to June15GAM class as static globals?
    public static Vector3 mouseScreenPos;
    public static Vector3 mouseWorldPos;

    final June15GAM    game;
    FrameBuffer        sceneFrameBuffer;
    SpriteBatch        batch;
    TextureRegion      sceneRegion;
    OrthographicCamera camera;
    Shake              shake;
    BitmapFont         font;
    EffectsManager     effectsManager;
    Rectangle          intersection;
    Rectangle          placeButtonRect;
    Rectangle          rotateButtonRect;
    MutableFloat       phaseTextOffsetX;

    int                numShips;
    boolean            phaseActive;
    float              phaseTimer;
    float              phaseEntryTimer;
    float              noActionTimer;
    final float        cannonTimer = 15;
    final float        attackTimer = 30;
    final float        buildTimer = 25;
    final float        phaseEntryDelayTime = 1.5f;
    int                numShipsToAdd = 2;

    GlyphLayout layout = new GlyphLayout();
    float              phaseStringX;
    float              phaseStringY;
    float              touchStringX;
    float              touchStringY;
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
        numShips = 1;
        numShipsToAdd = 2;
        mouseScreenPos = new Vector3();
        mouseWorldPos = new Vector3();
        sceneFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, June15GAM.win_width, June15GAM.win_height, false);
        batch = new SpriteBatch();
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);
        camera = new OrthographicCamera(June15GAM.win_width, June15GAM.win_height);
        camera.translate(June15GAM.win_width / 2f, June15GAM.win_height / 2f);
        camera.update();
        shake = new Shake();
        font = Assets.font;
        font.setColor(Color.WHITE);
        font.getData().setScale(FONT_SCALE_DEFAULT);
        effectsManager = new EffectsManager();
        intersection = new Rectangle();

        phaseEntryTimer = 1;
        phase = Gameplay.CANNON;
        phaseTimer = cannonTimer;
        phaseActive = false;
        phaseTextOffsetX = new MutableFloat(PHASE_OFFSET_MIN);
        Tween.to(phaseTextOffsetX, -1, 1f)
             .target(0)
             .ease(Quint.OUT)
             .start(June15GAM.tween);
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
        shake.update(delta, camera, camera.viewportWidth / 2f, camera.viewportHeight / 2f);

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
        final String timerStringNoMarkup = "Time Left: " + (int)Math.max(Math.ceil(phaseTimer), 0);
        final String timerString = "[YELLOW]Time Left:[] [RED]" + (int)Math.max(Math.ceil(phaseTimer), 0) + "[]";
        font.getData().setScale(FONT_SCALE_TIMER);
        layout.setText(font, timerString);
        font.setColor(Color.BLACK);
        font.draw(batch, timerStringNoMarkup, camera.viewportWidth - (layout.width + 5f) + 2f, camera.viewportHeight - 5f + 2f);
        font.setColor(Color.WHITE);
        font.draw(batch, timerString, camera.viewportWidth - (layout.width + 5f), camera.viewportHeight - 5f);

        if (phase == Gameplay.ATTACK || phase == Gameplay.GAMEOVER) {
            final String scoreStringNoMarkup = "Points: " + score;
            final String scoreString = "[YELLOW]Points:[] [GREEN]" + score + "[]";
            font.getData().setScale(FONT_SCALE_POINTS);
            layout.setText(font, scoreString);
            font.setColor(Color.BLACK);
            font.draw(batch, scoreStringNoMarkup, 10 + 2f, layout.height + 10f + 2f);
            font.setColor(Color.WHITE);
            font.draw(batch, scoreString, 10, layout.height + 10f);
        }

        final float y = camera.viewportHeight - Assets.spritesheetRegions[2][3].getRegionHeight() - 10f;
        float x = 10f;
        for (Tower tower : tileMap.getTowers()) {
            final Color color = tower.canFire() ? Color.GREEN : Color.RED;
            batch.setColor(color);
            batch.draw(Assets.spritesheetRegions[2][3], x, y);
            x += Assets.spritesheetRegions[2][3].getRegionWidth() + 5f;
        }
        batch.setColor(Color.WHITE);
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
            batch.draw(Assets.spritesheetRegions[2][0], placeButtonRect.x, placeButtonRect.y, placeButtonRect.width, placeButtonRect.height);
            tileMap.tetris.render(tileMap, batch);
        }
        if (phase == Gameplay.BUILD)
            batch.draw(Assets.spritesheetRegions[2][2], rotateButtonRect.x, rotateButtonRect.y, rotateButtonRect.width, rotateButtonRect.height);

        if (phase == Gameplay.GAMEOVER) {
            final String gameOverStringNoMarkup = "Game Over, You're Bad at Life!";
            final String gameOverString = "[RED]Game Over[], [YELLOW]You're Bad at Life![]";
            font.getData().setScale(FONT_SCALE_GAMEOVER);
            layout.setText(font, gameOverStringNoMarkup);
            final float gameOverX = (camera.viewportWidth  - layout.width)  / 2f + phaseTextOffsetX.floatValue();
            final float gameOverY = (camera.viewportHeight + layout.height) / 2f;

            final float margin = 110f;
            final float w = layout.width + margin;
            final float h = 2f * layout.height;
            final float bgx = gameOverX - margin / 2f;
            final float bgy = gameOverY - 1.75f * layout.height;
            batch.setColor(Color.BLACK);
            batch.draw(Assets.spritesheetRegions[4][0], bgx - 4f, bgy - 4f, w + 8f, h + 8f);
            batch.setColor(Color.GRAY);
            batch.draw(Assets.spritesheetRegions[4][0], bgx, bgy, w, h);
            batch.setColor(Color.WHITE);

            font.setColor(Color.BLACK);
            font.draw(batch, gameOverStringNoMarkup, gameOverX + 2f, gameOverY + 2f);
            font.setColor(Color.WHITE);
            font.draw(batch, gameOverString, gameOverX, gameOverY);

            font.getData().setScale(FONT_SCALE_DEFAULT);
        }

        if (!phaseActive && phase != Gameplay.GAMEOVER){
            final String phaseString = phase.name() + " Phase!";
            font.getData().setScale(FONT_SCALE_PHASE);
            layout.setText(font, phaseString);
            phaseStringX = (camera.viewportWidth  - layout.width)  / 2f + phaseTextOffsetX.floatValue();
            phaseStringY = (camera.viewportHeight + layout.height) / 2f;

            final float margin = 100f;
            final float w = layout.width + margin;
            final float h = 2f * layout.height;
            final float bgx = phaseStringX - margin / 2f;
            final float bgy = phaseStringY - 1.75f * layout.height;
            batch.setColor(Color.BLACK);
            batch.draw(Assets.spritesheetRegions[4][0], bgx - 4f, bgy - 4f, w + 8f, h + 8f);
            batch.setColor(Color.GRAY);
            batch.draw(Assets.spritesheetRegions[4][0], bgx, bgy, w, h);
            batch.setColor(Color.WHITE);

            font.setColor(Color.BLACK);
            font.draw(batch, phaseString, phaseStringX + 2f, phaseStringY + 2f);
            font.setColor(Color.CYAN);
            font.draw(batch, phaseString, phaseStringX, phaseStringY);

            if (phaseEntryTimer <= 0) {
                final String touchString = "Touch to Start...";
                font.getData().setScale(FONT_SCALE_TOUCH);
                layout.setText(font, touchString);
                touchStringX = (camera.viewportWidth  - layout.width)  / 2f + phaseTextOffsetX.floatValue();
                touchStringY = (camera.viewportHeight + layout.height) / 2f - 35f;
                font.setColor(Color.BLACK);
                font.draw(batch, touchString, touchStringX + 2f, touchStringY + 2f);
                font.setColor(Color.GREEN);
                font.draw(batch, touchString, touchStringX, touchStringY);
            }

            font.getData().setScale(FONT_SCALE_DEFAULT);
            font.setColor(Color.WHITE);
        }
        batch.end();
    }

    boolean      playCountdown = false;
    boolean      justPlayedCountdown = false;
    MutableFloat dummy         = new MutableFloat(0f);

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.exit();
        }
        noActionTimer -= delta;
        tileMap.update(delta);

        if (phaseActive) {
            phaseTimer -= delta;
            if (!playCountdown && phaseTimer > 0f && phaseTimer <= 3.1f && !justPlayedCountdown) {
                playCountdown = true;
                justPlayedCountdown = true;
                Assets.countdownSound.play();
                // setTimeout(...)
                Tween.to(dummy, -1, 3f).target(0f).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        playCountdown = false;
                    }
                }).start(June15GAM.tween);
            }
            switch (phase) {
                case BUILD:  updateBuildPhase(delta);  break;
                case CANNON: updateCannonPhase(delta); break;
                case ATTACK: updateAttackPhase(delta); break;
            }
        } else {
            phaseEntryTimer -= delta;
            if (Gdx.input.justTouched()) {
                if (phaseEntryTimer <= 0) {
                    phaseActive = true;
                    noActionTimer = .2f;
                    justPlayedCountdown = false;
                }
                if (phase == Gameplay.GAMEOVER) {
                    dispose();
                    // TODO: nice transition?
                    game.setScreen(new MenuScreen(game));
                }
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
                phaseTextOffsetX.setValue(PHASE_OFFSET_MIN);
                Tween.to(phaseTextOffsetX, -1, 1f)
                     .target(0)
                     .ease(Quint.OUT)
                     .start(June15GAM.tween);
                tileMap.tetris = new CannonPlacer(tileMap); // TODO: should be based on number of interior tiles
                clearCannonballs();
                resetCannons();
            } else {
                phase = Gameplay.GAMEOVER;
                phaseTextOffsetX.setValue(PHASE_OFFSET_MIN);
                Tween.to(phaseTextOffsetX, -1, 1f)
                     .target(0)
                     .ease(Quint.OUT)
                     .start(June15GAM.tween);
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
            phaseTextOffsetX.setValue(PHASE_OFFSET_MIN);
            Tween.to(phaseTextOffsetX, -1, 1f)
                 .target(0)
                 .ease(Quint.OUT)
                 .start(June15GAM.tween);
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
            phaseTextOffsetX.setValue(PHASE_OFFSET_MIN);
            Tween.to(phaseTextOffsetX, -1, 1f)
                 .target(0)
                 .ease(Quint.OUT)
                 .start(June15GAM.tween);
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
            turn++;
            tileMap.tetris = new WallPiece(tileMap);
            clearCannonballs();
            resetCannons();
            effectsManager.clearDecals();
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
                        Assets.shipDeathSound.play();
                        effectsManager.newEffect(effectType, cannonball.position.x, cannonball.position.y);
                        shake.shake(SHAKE_AMOUNT_SHIP_DESTROYED);
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
        if (tileMap.destroyBuildingAt(tileHitX, tileHitY)) {
            Assets.wallHitSound.play();
        }
        if (hitTileType != TileType.WATER) {
            effectsManager.newEffect(Effect.Type.DECAL_CRATER, cannonball.target.x, cannonball.target.y);
            tileMap.tiles[tileHitY][tileHitX].type = TileType.GROUND;
            tileMap.tiles[tileHitY][tileHitX].texture = TileTexture.GROUND_GRASS;
        }
        effectsManager.newEffect(effectType, cannonball.target.x, cannonball.target.y);
        // TODO: different shake when a wall is hit
        shake.shake(SHAKE_AMOUNT_GROUND_HIT);
    }

    private void spawnShips() {
        // NOTE: for now, assume that the north edge of the screen is always a body of water, spawn from any tile along that edge
        // TODO: generate spawn points on the tilemap and randomly pick from those when spawning
        if (ships == null) {
            ships = new Array<Ship>();
        } else {
            ships.clear();
        }

        numShips = (int)Math.pow(numShipsToAdd, (turn + 1));

        final float tile_size = tileMap.tileSet.tileSize;
        for (int i = 0; i < numShips; ++i) {
            int tx = MathUtils.random(1, tileMap.tiles[0].length - 2);
            int ty = 0;
            float w = tile_size * 1.5f;
            float h = tile_size * 1.5f;
            Ship ship = new Ship(tileMap, tx * tile_size, ty * tile_size, w, h, turn + 1);
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
        // TODO: remove craters if building a wall over one
        if (placeButtonRect.contains(mouseWorldPos.x, mouseWorldPos.y)) {
            if (tileMap.tetris.place(tileMap)) {
                Assets.wallPlaceSound.play();
                shake.shake(SHAKE_AMOUNT_PLACE_WALL);
            }
            return;
        }
        if (rotateButtonRect.contains(mouseWorldPos.x, mouseWorldPos.y)) {
            tileMap.tetris.rotate(WallPiece.R.C);
            shake.shake(SHAKE_AMOUNT_PLACE_WALL);
            return;
        }
        if (tileMap.tetris.place(tileMap)) {
            Assets.wallPlaceSound.play();
            shake.shake(SHAKE_AMOUNT_PLACE_WALL);
        }
    }

    private void tapCannon() {
        // TODO: remove craters if building a wall over one
        if (placeButtonRect.contains(mouseWorldPos.x, mouseWorldPos.y)) {
            if (tileMap.tetris.place(tileMap)) {
                Assets.cannonPlaceSound.play();
                shake.shake(SHAKE_AMOUNT_PLACE_CANNON);
            }
            return;
        }
        if (tileMap.tetris.place(tileMap)) {
            Assets.cannonPlaceSound.play();
            shake.shake(SHAKE_AMOUNT_PLACE_CANNON);
        }
    }

    private void tapAttack() {
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

                Assets.cannonTowerSound.play();
                Cannonball cannonball = cannonballPool.obtain();
                cannonball.init(tower.x * tile_size + half_tile_size / 2f,
                                tower.y * tile_size + half_tile_size / 2f,
                                mouseWorldPos.x,
                                mouseWorldPos.y,
                                half_tile_size, half_tile_size,
                                speed);
                cannonball.setSource(Cannonball.Source.TOWER);
                activeCannonballs.add(cannonball);
                shake.shake(SHAKE_AMOUNT_PLAYER_FIRES);
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
