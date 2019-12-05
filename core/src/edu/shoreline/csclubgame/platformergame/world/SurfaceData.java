package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.math.Vector2;

public class SurfaceData {
    private final Vector2 normal;

    public SurfaceData(Vector2 normal) {
        this.normal = normal;
    }

    public Vector2 getNormal() {
        return normal;
    }
}
