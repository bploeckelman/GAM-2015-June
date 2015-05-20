package com.lando.systems.June15GAM.cameras;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


/**
 * https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/utils/OrthoCamController.java
 */
public class OrthoCamController extends InputAdapter {
    final OrthographicCamera camera;
    final Vector3 curr                 = new Vector3();
    final Vector3 last                 = new Vector3(-1, -1, -1);
    final Vector3 delta                = new Vector3();
    final float   zoom_scale           = 0.025f;
    final float   min_camera_zoom      = 0.1f;
    final float   initial_camera_zoom  = 1;
    final boolean pan_right_mouse_only = true;

    public MutableFloat camera_zoom = new MutableFloat(initial_camera_zoom);

    boolean isRightMouseDown = false;

    public OrthoCamController(OrthographicCamera camera) {
        this.camera = camera;
        this.camera.zoom = camera_zoom.floatValue();
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (pan_right_mouse_only && isRightMouseDown) {
            camera.unproject(curr.set(x, y, 0));
            if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
                camera.unproject(delta.set(last.x, last.y, 0));
                delta.sub(curr);
                camera.position.add(delta.x, delta.y, 0);
            }
            last.set(x, y, 0);
        }
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            isRightMouseDown = false;
        }
        last.set(-1, -1, -1);
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            isRightMouseDown = true;
        }
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        if (!Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
            return false;
        }

        float scale = zoom_scale;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            scale *= 10;
        }
        float destZoom = camera_zoom.floatValue() + scale * amount;
        camera_zoom.setValue(MathUtils.lerp(camera_zoom.floatValue(), destZoom, 0.9f));
        if (destZoom < min_camera_zoom) {
            camera_zoom.setValue(min_camera_zoom);
        }
        camera.zoom = camera_zoom.floatValue();
        return false;
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

}
