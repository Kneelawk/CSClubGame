package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.physics.box2d.Contact;

public interface FixtureContactListener {
    void beginContact(Contact contact);

    void endContact(Contact contact);
}
