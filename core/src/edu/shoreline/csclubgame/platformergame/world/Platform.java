package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Platform {
    private Body body;
    private Fixture fixture;
    private Fixture surface;

    public Platform(World world, Vector2 position, Vector2 dimensions, float rotation, boolean landing) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(dimensions.x, dimensions.y, new Vector2(0, 0), rotation);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0.2f;

        fixture = body.createFixture(fixtureDef);

        if (landing) {
            Vector2 offset = new Vector2(0, dimensions.y + 0.15f).rotateRad(rotation);
            shape.setAsBox(dimensions.x, 0.15f, offset, rotation);

            FixtureDef surfaceDef = new FixtureDef();
            surfaceDef.shape = shape;
            surfaceDef.isSensor = true;

            surface = body.createFixture(surfaceDef);
            surface.setUserData(new SurfaceData(new Vector2(0, 1).rotateRad(rotation)));
        }

        shape.dispose();
    }

    public Body getBody() {
        return body;
    }

    public Fixture getFixture() {
        return fixture;
    }
}
