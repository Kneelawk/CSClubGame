package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.physics.box2d.Fixture;

public class ContactKey {
    private final Fixture fixtureA;
    private final Fixture fixtureB;

    public ContactKey(Fixture fixtureA, Fixture fixtureB) {
        this.fixtureA = fixtureA;
        this.fixtureB = fixtureB;
    }

    public Fixture getFixtureA() {
        return fixtureA;
    }

    public Fixture getFixtureB() {
        return fixtureB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactKey that = (ContactKey) o;

        if (!fixtureA.equals(that.fixtureA)) return false;
        return fixtureB.equals(that.fixtureB);
    }

    @Override
    public int hashCode() {
        int result = fixtureA.hashCode();
        result = 31 * result + fixtureB.hashCode();
        return result;
    }
}
