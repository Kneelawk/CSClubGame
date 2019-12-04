package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public interface FixtureContactListener {
    void beginContact(Contact contact, Fixture you, Fixture other);

    void endContact(Contact contact, Fixture you, Fixture other);

    void preSolve(Contact contact, Manifold oldManifold, Fixture you, Fixture other);

    void postSolve(Contact contact, ContactImpulse impulse, Fixture you, Fixture other);
}
