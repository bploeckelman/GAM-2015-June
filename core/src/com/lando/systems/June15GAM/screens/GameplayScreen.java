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
import com.lando.systems.June15GAM.buildings.Wall;
import com.lando.systems.June15GAM.effects.Effect;
import com.lando.systems.June15GAM.effects.EffectsManager;
import com.lando.systems.June15GAM.effects.ExplosionWater;
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

    boolean            phaseActive;
    float              phaseTimer;
    float              phaseEntryTimer;
    final float        cannonTimer = 15;
    final float        attackTimer = 30;
    final float        buildTimer = 15;
    final float        phaseEntryDelayTime = 1;
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

        // NOTE: for now, assume that the north edge of the screen is always a body of water, spawn from any tile along that edge
        // TODO: generate spawn points on the tilemap and randomly pick from those when spawning
        final int NUM_SHIPS = 10;
        ships = new Array<Ship>();
        for (int i = 0; i < NUM_SHIPS; ++i) {
            int tx = MathUtils.random(1, tileMap.tiles[0].length - 2);
            int ty = tileMap.tiles.length - 1;
            Ship ship = new Ship(tx * tileSet.tileSize, ty * tileSet.tileSize, tileSet.tileSize, tileSet.tileSize);
            ships.add(ship);
        }

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
                case BUILD:  updateBuild(delta);  break;
                case CANNON: updateCannon(delta); break;
                case ATTACK: updateAttack(delta); break;
            }
        } else {
            phaseEntryTimer -= delta;
            if (phaseEntryTimer <= 0) phaseActive = true;
        }



        effectsManager.update(delta);

        camera.update();
        updateMouseVectors(camera);
    }



    private void updateBuild(float delta) {
        // TODO: switch to attack phase based on some condition (timer? done placing wall sections?)
        if (phaseTimer <= 0){
            phase = Gameplay.CANNON;
            phaseTimer = cannonTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
            tileMap.tetris = new CannonPlacer(3); // TODO: This should be based on something
        }

    }

    private void updateCannon(float delta){
        if (phaseTimer <= 0 || tileMap.tetris.getNumberLeft() <= 0){
            phase = Gameplay.ATTACK;
            phaseTimer = attackTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
        }
    }

    private void updateAttack(float delta) {

        for (Ship ship : ships) {
            ship.update(delta);

            // Handle ship movement
            if (ship.reachedTarget()) {
                // TODO: pick new water tile target (reachable from current location) move this stuff into ship? or move ships into tilemap?
                final float tile_size = tileMap.tileSet.tileSize;
                final float map_width = tileMap.tiles[0].length * tile_size;
                final float map_height = tileMap.tiles.length * tile_size;

                ship.moveTarget.set(
                        MathUtils.random(tile_size, map_width - tile_size),
                        MathUtils.random(map_height * 2f / 3f + tile_size, map_height - tile_size));
                ship.velocity.set(ship.moveTarget.x - ship.position.x, ship.moveTarget.y - ship.position.y);
                ship.velocity.nor().scl(Ship.SPEED);
            }

            // Handle ship shooting
            if (ship.canShoot()) {
                ship.shoot();

                final int wallIndex = MathUtils.random(0, tileMap.getWalls().size() - 1);
                final Wall wall = tileMap.getWalls().get(wallIndex);
                final float wallX = wall.x * tileMap.tileSet.tileSize + tileMap.tileSet.tileSize / 2f;
                final float wallY = wall.y * tileMap.tileSet.tileSize + tileMap.tileSet.tileSize / 2f;

                Cannonball cannonball = cannonballPool.obtain();
                cannonball.init(ship.position.x, ship.position.y, wallX, wallY);
                cannonball.source = Cannonball.Source.SHIP;
                activeCannonballs.add(cannonball);
            }
        }

        for (Tower tower : tileMap.getTowers()) {
            tower.update(delta);
        }

        // TODO: clean this stuff up
        for (int i = activeCannonballs.size - 1; i >= 0; --i) {
            Cannonball cannonball = activeCannonballs.get(i);
            cannonball.update(delta);
            if (cannonball.position.epsilonEquals(cannonball.target, tileMap.tileSet.tileSize / 2f)) {
                final int tx = (int) (cannonball.position.x / tileMap.tileSet.tileSize);
                final int ty = (int) (cannonball.position.y / tileMap.tileSet.tileSize);
                // TODO: differnt explosion for walls
                final Effect.Type effectType = Effect.Type.EXPLOSION_GROUND;
                effectsManager.newEffect(effectType, cannonball.position.x, cannonball.position.y);

                cannonballPool.free(cannonball);
                activeCannonballs.removeIndex(i);

                tileMap.tiles[ty][tx].type = TileType.GROUND;
                tileMap.tiles[ty][tx].texture = TileTexture.GROUND_SAND;
            } else {
                for (int s = ships.size - 1; s >= 0; --s) {
                    final Ship ship = ships.get(s);
                    if (cannonball.source == Cannonball.Source.TOWER &&
                        cannonball.position.epsilonEquals(ship.position, tileMap.tileSet.tileSize / 2f)) {
                        final int tx = (int) (cannonball.position.x / tileMap.tileSet.tileSize);
                        final int ty = (int) (cannonball.position.y / tileMap.tileSet.tileSize);
                        TileType tileType = tileMap.getTileType(tx, ty);
                        if (tileType == null) tileType = TileType.GROUND;
                        final Effect.Type effectType = tileType == TileType.GROUND ? Effect.Type.EXPLOSION_GROUND : Effect.Type.EXPLOSION_WATER;
                        effectsManager.newEffect(effectType, cannonball.position.x, cannonball.position.y);

                        cannonballPool.free(cannonball);
                        activeCannonballs.removeIndex(i);

                        // TODO: moar kaboom
                        ships.removeIndex(s);
                    }
                }
            }
        }
        if (phaseTimer <= 0 && activeCannonballs.size == 0){
            phase = Gameplay.BUILD;
            phaseTimer = buildTimer;
            phaseActive = false;
            phaseEntryTimer = phaseEntryDelayTime;
            turn++;
            tileMap.tetris = new WallPiece();
        }
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
                cannonball.init(towerPos.x, towerPos.y, mouseWorldPos.x, mouseWorldPos.y);
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
