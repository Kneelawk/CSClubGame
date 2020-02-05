package edu.shoreline.csclubgame.platformergame.world.action;

public interface PhysicsActionController {
    void setControlTarget(ControllablePhysicsAction action);

    void update(float delta);
}
