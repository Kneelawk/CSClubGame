package edu.shoreline.csclubgame.platformergame.world.action;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Action;
import edu.shoreline.csclubgame.platformergame.physics.FixtureContactListener;
import edu.shoreline.csclubgame.platformergame.physics.WorldContactManager;
import edu.shoreline.csclubgame.platformergame.world.ContactKey;
import edu.shoreline.csclubgame.platformergame.world.SurfaceData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EntityPhysicsAction extends Action implements ControllablePhysicsAction {
    protected final Body body;
    protected final Fixture fixture;
    protected final Fixture foot;
    protected final FootContactManager footContactManager;
    protected final FrictionManager frictionManager;
    protected final NormalManager normalManager;
    protected final Set<PhysicsActionController> controllers = new HashSet<>();

    public EntityPhysicsAction(World world, WorldContactManager contactManager, Vector2 initialPosition) {
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

    public boolean addController(PhysicsActionController controller) {
        controller.setControlTarget(this);
        return controllers.add(controller);
    }

    public boolean removeController(PhysicsActionController controller) {
        controller.setControlTarget(null);
        return controllers.remove(controller);
    }

    @Override
    public boolean act(float delta) {
        for (PhysicsActionController controller : controllers) {
            controller.update(delta);
        }

        target.setPosition(body.getPosition().x, body.getPosition().y);
        target.setRotation(normalManager.getRotation());

        return false;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public Fixture getFixture() {
        return fixture;
    }

    @Override
    public Fixture getFoot() {
        return foot;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public Vector2 getNormal() {
        return normalManager.getNormal();
    }

    @Override
    public float getRotation() {
        return normalManager.getRotation();
    }

    @Override
    public boolean isOnGround() {
        return footContactManager.isOnGround();
    }

    @Override
    public void move(Vector2 direction) {
        body.applyForceToCenter(direction, true);
    }

    @Override
    public void moveRelative(Vector2 direction) {
        body.applyForceToCenter(direction.rotateRad(normalManager.getRotation()), true);
    }

    @Override
    public void slowMovement(float factor) {
        body.applyForceToCenter(body.getLinearVelocity().scl(Vector2.X).scl(factor * body.getMass() * -1), true);
    }

    @Override
    public void setFriction(float friction) {
        fixture.setFriction(friction);
        frictionManager.setFriction(friction);
    }

    @Override
    public float getFriction() {
        return 0;
    }

    private static class FootContactManager implements FixtureContactListener {
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

    private static class FrictionManager implements FixtureContactListener {
        private final HashSet<Contact> contacts = new HashSet<>();

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

    private static class NormalManager implements FixtureContactListener {
        private final HashMap<ContactKey, Vector2> contactNormals = new HashMap<>();
        private Vector2 normal = new Vector2(0, 1);
        private float rotation = 0;

        @Override
        public void beginContact(Contact contact, Fixture you, Fixture other) {
            if (other.getUserData() instanceof SurfaceData) {
                contactNormals.put(new ContactKey(you, other), ((SurfaceData) other.getUserData()).getNormal());
                Vector2 accumulator = new Vector2(0, 0);
                for (Vector2 normal : contactNormals.values()) {
                    accumulator.add(normal);
                }
                normal = accumulator.scl(1f / contactNormals.size());
                rotation = (float) Math.atan2(-normal.x, normal.y);
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
                rotation = (float) Math.atan2(-normal.x, normal.y);
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

        public float getRotation() {
            return rotation;
        }
    }
}
