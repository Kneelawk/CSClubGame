package edu.shoreline.csclubgame.platformergame.world.action;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public interface ControllablePhysicsAction {
    Body getBody();

    Fixture getFixture();

    Fixture getFoot();

    Vector2 getPosition();

    Vector2 getNormal();

    float getRotation();

    boolean isOnGround();

    void move(Vector2 direction);

    void moveRelative(Vector2 direction);

    void slowMovement(float factor);

    void setFriction(float friction);

    float getFriction();
}
