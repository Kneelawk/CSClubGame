package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Platform {
    private Body body;
    private Fixture fixture;

    public Platform(World world, Vector2 position, Vector2 dimensions, float rotation) {
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

        shape.dispose();
    }

    public Body getBody() {
        return body;
    }

    public Fixture getFixture() {
        return fixture;
    }
}
