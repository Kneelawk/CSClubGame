package edu.shoreline.csclubgame.platformergame.world.action;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntSet;

public class InputEntityController extends InputAdapter implements PhysicsActionController {
    private final IntSet pressedKeys = new IntSet();
    private final IntSet detectableKeys = new IntSet();
    private final int upKey;
    private final int rightKey;
    private final int leftKey;
    private int jumpTime = 0;
    private ControllablePhysicsAction action;

    public InputEntityController(int upKey, int rightKey, int leftKey) {
        this.upKey = upKey;
        this.rightKey = rightKey;
        this.leftKey = leftKey;

        detectableKeys.addAll(upKey, rightKey, leftKey);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (detectableKeys.contains(keycode)) {
            if (keycode == upKey && action.isOnGround()) {
                jumpTime = 5;
            }
            pressedKeys.add(keycode);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (detectableKeys.contains(keycode)) {
            pressedKeys.remove(keycode);
            return true;
        }
        return false;
    }

    @Override
    public void setControlTarget(ControllablePhysicsAction action) {
        this.action = action;
    }

    @Override
    public void update(float delta) {
        if (pressedKeys.contains(leftKey) && pressedKeys.contains(rightKey)) {
            action.slowMovement(2f);
            action.setFriction(0.6f);
        } else if (pressedKeys.contains(leftKey)) {
            action.move(action.getNormal().cpy().rotate90(1).scl(20));
            action.setFriction(0.0f);
        } else if (pressedKeys.contains(rightKey)) {
            action.move(action.getNormal().cpy().rotate90(-1).scl(20));
            action.setFriction(0.0f);
        } else {
            action.slowMovement(2f);
            action.setFriction(0.6f);
        }
        if (pressedKeys.contains(upKey) && jumpTime > 0) {
            action.move(action.getNormal().cpy().scl(150));
            jumpTime--;
        }
    }
}
