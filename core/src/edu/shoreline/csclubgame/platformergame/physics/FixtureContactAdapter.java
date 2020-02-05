package edu.shoreline.csclubgame.platformergame.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public abstract class FixtureContactAdapter implements FixtureContactListener {
    @Override
    public void beginContact(Contact contact, Fixture you, Fixture other) {
    }

    @Override
    public void endContact(Contact contact, Fixture you, Fixture other) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold, Fixture you, Fixture other) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse, Fixture you, Fixture other) {
    }
}
