package edu.shoreline.csclubgame.platformergame.physics;

import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldContactManager implements ContactListener {
    private Map<Fixture, Set<FixtureContactListener>> listeners = new HashMap<>();

    public void addListener(Fixture fixture, FixtureContactListener listener) {
        if (!listeners.containsKey(fixture)) {
            listeners.put(fixture, new HashSet<>());
        }

        listeners.get(fixture).add(listener);
    }

    public void removeListener(Fixture fixture, FixtureContactListener listener) {
        if (listeners.containsKey(fixture)) {
            Set<FixtureContactListener> listenerSet = listeners.get(fixture);
            listenerSet.remove(listener);

            if (listenerSet.isEmpty()) {
                listeners.remove(fixture);
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (listeners.containsKey(contact.getFixtureA())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureA())) {
                listener.beginContact(contact, contact.getFixtureA(), contact.getFixtureB());
            }
        }

        if (listeners.containsKey(contact.getFixtureB())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureB())) {
                listener.beginContact(contact, contact.getFixtureB(), contact.getFixtureA());
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (listeners.containsKey(contact.getFixtureA())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureA())) {
                listener.endContact(contact, contact.getFixtureA(), contact.getFixtureB());
            }
        }

        if (listeners.containsKey(contact.getFixtureB())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureB())) {
                listener.endContact(contact, contact.getFixtureB(), contact.getFixtureA());
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        if (listeners.containsKey(contact.getFixtureA())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureA())) {
                listener.preSolve(contact, oldManifold, contact.getFixtureA(), contact.getFixtureB());
            }
        }

        if (listeners.containsKey(contact.getFixtureB())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureB())) {
                listener.preSolve(contact, oldManifold, contact.getFixtureB(), contact.getFixtureA());
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if (listeners.containsKey(contact.getFixtureA())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureA())) {
                listener.postSolve(contact, impulse, contact.getFixtureA(), contact.getFixtureB());
            }
        }

        if (listeners.containsKey(contact.getFixtureB())) {
            for (FixtureContactListener listener : listeners.get(contact.getFixtureB())) {
                listener.postSolve(contact, impulse, contact.getFixtureB(), contact.getFixtureA());
            }
        }
    }
}
