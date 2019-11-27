package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashSet;

public class Player {
    private static final HashSet<Integer> KEYS = new HashSet<>();

    static {
        KEYS.add(Input.Keys.W);
        KEYS.add(Input.Keys.A);
        KEYS.add(Input.Keys.D);
    }

    private PlayerController controller;
    private Body body;
    private Fixture fixture;
    private Fixture foot;
    private FootContactManager footContactManager;

    public Player(World world, WorldContactManager contactManager, Vector2 initialPosition) {
        controller = new PlayerController();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(initialPosition);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        fixture = body.createFixture(fixtureDef);

        shape.setAsBox(1, 0.2f, new Vector2(0, -1), 0);

        FixtureDef footFixtureDef = new FixtureDef();
        footFixtureDef.shape = shape;
        footFixtureDef.isSensor = true;
        foot = body.createFixture(footFixtureDef);

        footContactManager = new FootContactManager();
        contactManager.addListener(foot, footContactManager);

        shape.dispose();
    }

    public InputProcessor getController() {
        return controller;
    }

    public Body getBody() {
        return body;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public Fixture getFoot() {
        return foot;
    }

    public void move(Vector2 direction) {
        body.applyForceToCenter(direction, true);
    }

    public void update() {
        controller.update();
    }

    private class PlayerController extends InputAdapter {
        private HashSet<Integer> pressedKeys = new HashSet<>();
        private int jumpTime = 0;

        @Override
        public boolean keyDown(int keycode) {
            if (KEYS.contains(keycode)) {
                if (keycode == Input.Keys.W && footContactManager.isOnGround()) {
                    jumpTime = 4;
                }
                pressedKeys.add(keycode);
                return true;
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (KEYS.contains(keycode)) {
                pressedKeys.remove(keycode);
                return true;
            }
            return false;
        }

        private void update() {
            if (pressedKeys.contains(Input.Keys.A)) {
                move(new Vector2(-100, 0));
            }
            if (pressedKeys.contains(Input.Keys.D)) {
                move(new Vector2(100, 0));
            }
            if (pressedKeys.contains(Input.Keys.W) && jumpTime > 0) {
                move(new Vector2(0, 200));
                jumpTime--;
            }
        }
    }

    private class FootContactManager implements FixtureContactListener {
        private int numContacts = 0;

        @Override
        public void beginContact(Contact contact) {
            numContacts++;
        }

        @Override
        public void endContact(Contact contact) {
            numContacts--;
        }

        private boolean isOnGround() {
            return numContacts > 0;
        }
    }
}
