package com.lando.systems.June15GAM.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lando.systems.June15GAM.Assets;
import com.lando.systems.June15GAM.June15GAM;

/**
 * Brian Ploeckelman created on 6/25/2015.
 */
public class MenuScreen extends ScreenAdapter {

    final June15GAM game;

    OrthographicCamera camera;
    SpriteBatch        batch;
    BitmapFont         font;
    GlyphLayout        layout;

    float u_time;

    String line1;
    String line2;

    public MenuScreen(June15GAM game) {
        this.game = game;
        this.camera = new OrthographicCamera(June15GAM.win_width, June15GAM.win_height);
        this.camera.translate(June15GAM.win_width / 2f, June15GAM.win_height / 2f);
        this.camera.update();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(Gdx.files.internal("fonts/2lines.fnt"));
        this.layout = new GlyphLayout();
        this.line1 = "Bulwark!";
        this.line2 = "touch to play...";
        this.u_time = 0f;
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

        batch.setProjectionMatrix(camera.combined);

        batch.setShader(Assets.menuBackgroundShader);
        batch.begin();
        Assets.menuBackgroundShader.setUniformf("u_time", u_time);
        Assets.menuBackgroundShader.setUniformf("u_resolution", camera.viewportWidth, camera.viewportHeight);
        batch.draw(Assets.spritesheetRegions[15][15], 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.end();

        batch.setShader(null);
        batch.begin();
        font.getData().setScale(5f);
        layout.setText(font, line1);
        font.draw(batch, line1, (camera.viewportWidth - layout.width) / 2f, (camera.viewportHeight / 2f) + layout.height);
        final float lineHeight = (camera.viewportHeight + layout.height) / 2f - (layout.height / 2f);

        font.getData().setScale(1f);
        layout.setText(font, line2);
        font.draw(batch, line2, (camera.viewportWidth - layout.width) / 2f, lineHeight);
        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }

}
