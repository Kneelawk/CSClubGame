package edu.shoreline.csclubgame.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.shoreline.csclubgame.CSClubGameMain;
import edu.shoreline.csclubgame.util.ScreenGame;

public class MainMenu extends ScreenGame {
    private final CSClubGameMain main;

    private Stage stage;

    public MainMenu(CSClubGameMain main) {
        this.main = main;
    }

    @Override
    public void init() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        stage = new Stage(new FitViewport(1280, 720));

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skins/neon/neon-ui.atlas"));

        Skin skin = new Skin();
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skins/neon/neon-ui.json"));

        Table table = new Table(skin);
        table.setFillParent(true);
        table.defaults().pad(5);
        stage.addActor(table);

        TextButton start = new TextButton("Start", skin);
        table.add(start).width(400);
        start.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO start game
            }
        });

        table.row();

        TextButton quit = new TextButton("Quit", skin);
        table.add(quit).width(400);
        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.exit();
            }
        });
    }

    @Override
    public void showGame() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hideGame() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void disposeGame() {
        stage.dispose();
    }
}
