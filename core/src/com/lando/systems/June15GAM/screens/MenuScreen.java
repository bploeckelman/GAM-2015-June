package com.lando.systems.June15GAM.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;
import com.lando.systems.June15GAM.accessors.Vector2Accessor;

/**
 * Brian Ploeckelman created on 6/25/2015.
 */
public class MenuScreen extends ScreenAdapter {

    final June15GAM game;

    OrthographicCamera camera;
    SpriteBatch        batch;
    BitmapFont         font;
    String             line1;
    String             line2;
    Vector2            line1Pos;
    Vector2            line2Pos;
    boolean            introDone;

    float u_time;

    private final float LINE_1_SCALE = 5f;
    private final float LINE_2_SCALE = 1f;

    public MenuScreen(June15GAM game) {
        this.game = game;
        this.camera = new OrthographicCamera(June15GAM.win_width, June15GAM.win_height);
        this.camera.translate(June15GAM.win_width / 2f, June15GAM.win_height / 2f);
        this.camera.update();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(Gdx.files.internal("fonts/2lines.fnt"));
        this.line1 = "Bulwark!";
        this.line2 = "touch to play...";
        this.line1Pos = new Vector2();
        this.line2Pos = new Vector2();
        this.u_time = 0f;
        this.introDone = false;

        font.getData().setScale(LINE_1_SCALE);
        final GlyphLayout line1Layout = new GlyphLayout(font, line1);
        final float line1TargetX = (camera.viewportWidth - line1Layout.width) / 2f   - 15f;
        final float line1TargetY = (camera.viewportHeight / 2f) + line1Layout.height - 15f;
        line1Pos.x = line1TargetX;
        line1Pos.y = camera.viewportHeight;
        Tween.to(line1Pos, Vector2Accessor.Y, 2f)
             .target(line1TargetY)
             .ease(Circ.OUT)
             .setCallback(new TweenCallback() {
                 @Override
                 public void onEvent(int i, BaseTween<?> baseTween) {
                     introDone = true;
                     startRepeatingTweens();
                 }
             })
             .start(June15GAM.tween);

        // TODO: fade in line2
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.exit();
        }

        if (Gdx.input.justTouched()) {
            dispose();
            game.setScreen(new GameplayScreen(game));
        }

        u_time += delta;
    }

    public void render(float delta) {
        update(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.setShader(Assets.menuBackgroundShader);
        batch.begin();
        Assets.menuBackgroundShader.setUniformf("u_time", u_time);
        Assets.menuBackgroundShader.setUniformf("u_resolution", camera.viewportWidth, camera.viewportHeight);
        // NOTE: could be any full texture (not a region)
        batch.draw(Assets.vehiclesTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.end();

        batch.setShader(null);
        batch.begin();
        font.getData().setScale(LINE_1_SCALE);
        font.draw(batch, line1, line1Pos.x, line1Pos.y);
        if (introDone) {
            font.getData().setScale(LINE_2_SCALE);
            font.draw(batch, line2, line2Pos.x, line2Pos.y);
        }
        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }

    private void startRepeatingTweens() {
        final float TWEEN_SPEED = 0.5f;

        font.getData().setScale(LINE_1_SCALE);
        final GlyphLayout line1Layout = new GlyphLayout(font, line1);
        final float line1TargetX = (camera.viewportWidth - line1Layout.width) / 2f;
        final float line1TargetY = (camera.viewportHeight / 2f) + line1Layout.height;
        line1Pos.x = line1TargetX - 15f;
        line1Pos.y = line1TargetY - 15f;
        Tween.to(line1Pos, Vector2Accessor.XY, TWEEN_SPEED)
             .target(line1TargetX, line1TargetY)
             .ease(Bounce.OUT)
             .repeatYoyo(-1, TWEEN_SPEED)
             .start(June15GAM.tween);

        font.getData().setScale(LINE_2_SCALE);
        final GlyphLayout line2Layout = new GlyphLayout(font, line2);
        final float line2TargetX = (camera.viewportWidth  + line2Layout.width) / 2f - 10f;
        line2Pos.x = line2TargetX + 150f;
        line2Pos.y =  camera.viewportHeight / 2f;
        Tween.to(line2Pos, Vector2Accessor.X, TWEEN_SPEED)
             .target(line2TargetX)
             .ease(Quint.OUT)
             .repeatYoyo(-1, TWEEN_SPEED)
             .start(June15GAM.tween);
    }

}
