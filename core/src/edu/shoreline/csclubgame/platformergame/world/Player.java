package edu.shoreline.csclubgame.platformergame.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends Actor {

    public Player() {
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.WHITE);
        renderer.rectLine(new Vector2(getX(), getY()), new Vector2(getX(), getY()).add(new Vector2(0, 2).rotateRad(getRotation())),
                0.4f);
        renderer.end();
        batch.begin();
    }
}
