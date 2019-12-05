package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.IntSet;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends Actor {
    private static final IntSet KEYS = new IntSet();

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
    private FrictionManager frictionManager;
    private NormalManager normalManager;

    public Player(World world, WorldContactManager contactManager, Vector2 initialPosition) {
        controller = new PlayerController();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(initialPosition);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setPosition(new Vector2(0f, 0f));
        shape.setRadius(1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.25f;
        fixtureDef.friction = 0.6f;
        fixtureDef.restitution = 0.2f;

        fixture = body.createFixture(fixtureDef);

        frictionManager = new FrictionManager();
        contactManager.addListener(fixture, frictionManager);

        shape.setRadius(1.3f);

        FixtureDef footFixtureDef = new FixtureDef();
        footFixtureDef.friction = 0f;
        footFixtureDef.density = 0f;
        footFixtureDef.restitution = 0f;
        footFixtureDef.shape = shape;
        footFixtureDef.isSensor = true;
        foot = body.createFixture(footFixtureDef);

        footContactManager = new FootContactManager();
        contactManager.addListener(foot, footContactManager);
        normalManager = new NormalManager();
        contactManager.addListener(foot, normalManager);

        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.WHITE);
        renderer.rectLine(body.getPosition(), body.getPosition().cpy().add(normalManager.getNormal().cpy().scl(2)),
                0.4f);
        renderer.end();
        batch.begin();
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

    public void slowMovement(float factor) {
        body.applyForceToCenter(body.getLinearVelocity().scl(Vector2.X).scl(factor * body.getMass() * -1), true);
    }

    public void setFriction(float friction) {
        fixture.setFriction(friction);
        frictionManager.setFriction(friction);
    }

    public void update() {
        controller.update();
    }

    private class PlayerController extends InputAdapter {
        private IntSet pressedKeys = new IntSet();
        private int jumpTime = 0;

        @Override
        public boolean keyDown(int keycode) {
            if (KEYS.contains(keycode)) {
                if (keycode == Input.Keys.W && footContactManager.isOnGround()) {
                    jumpTime = 5;
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
            if (pressedKeys.contains(Input.Keys.A) && pressedKeys.contains(Input.Keys.D)) {
                slowMovement(2f);
                setFriction(0.6f);
            } else if (pressedKeys.contains(Input.Keys.A)) {
                move(normalManager.getNormal().cpy().rotate90(1).scl(20));
                setFriction(0.0f);
            } else if (pressedKeys.contains(Input.Keys.D)) {
                move(normalManager.getNormal().cpy().rotate90(-1).scl(20));
                setFriction(0.0f);
            } else {
                slowMovement(2f);
                setFriction(0.6f);
            }
            if (pressedKeys.contains(Input.Keys.W) && jumpTime > 0) {
                move(normalManager.getNormal().cpy().scl(150));
                jumpTime--;
            }
        }
    }

    private class FootContactManager implements FixtureContactListener {
        private int numContacts = 0;

        @Override
        public void beginContact(Contact contact, Fixture you, Fixture other) {
            numContacts++;
        }

        @Override
        public void endContact(Contact contact, Fixture you, Fixture other) {
            numContacts--;
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold, Fixture you, Fixture other) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse, Fixture you, Fixture other) {
        }

        private boolean isOnGround() {
            return numContacts > 0;
        }
    }

    private class FrictionManager implements FixtureContactListener {
        private HashSet<Contact> contacts = new HashSet<>();

        @Override
        public void beginContact(Contact contact, Fixture you, Fixture other) {
            contacts.add(contact);
        }

        @Override
        public void endContact(Contact contact, Fixture you, Fixture other) {
            contacts.remove(contact);
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold, Fixture you, Fixture other) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse, Fixture you, Fixture other) {
        }

        private void setFriction(float friction) {
            for (Contact contact : contacts) {
                contact.setFriction(friction);
            }
        }
    }

    private class NormalManager implements FixtureContactListener {
        private HashMap<ContactKey, Vector2> contactNormals = new HashMap<>();
        private Vector2 normal = new Vector2(0, 1);

        @Override
        public void beginContact(Contact contact, Fixture you, Fixture other) {
            System.out.println("Contact");
            if (other.getUserData() instanceof SurfaceData) {
                System.out.println("Surface contact");
                System.out.println("Surface Normal: " + ((SurfaceData) other.getUserData()).getNormal());
                contactNormals.put(new ContactKey(you, other), ((SurfaceData) other.getUserData()).getNormal());
                Vector2 accumulator = new Vector2(0, 0);
                for (Vector2 normal : contactNormals.values()) {
                    accumulator.add(normal);
                }
                normal = accumulator.scl(1f / contactNormals.size());
            }
        }

        @Override
        public void endContact(Contact contact, Fixture you, Fixture other) {
            contactNormals.remove(new ContactKey(you, other));
            if (contactNormals.size() > 0) {
                Vector2 accumulator = new Vector2(0, 0);
                for (Vector2 normal : contactNormals.values()) {
                    accumulator.add(normal);
                }
                normal = accumulator.scl(1f / contactNormals.size());
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold, Fixture you, Fixture other) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse, Fixture you, Fixture other) {
        }

        public Vector2 getNormal() {
            return normal;
        }
    }
}
