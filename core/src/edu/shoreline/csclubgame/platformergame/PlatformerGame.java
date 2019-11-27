package edu.shoreline.csclubgame.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.shoreline.csclubgame.CSClubGameMain;
import edu.shoreline.csclubgame.util.ScreenGame;

public class PlatformerGame extends ScreenGame {
    private static final int INITIAL_VIEWPORT_WIDTH = 1280;
    private static final int INITIAL_VIEWPORT_HEIGHT = 720;
    private static final float INITIAL_VIEWPORT_ASPECT_RATIO =
            ((float) INITIAL_VIEWPORT_HEIGHT) / ((float) INITIAL_VIEWPORT_WIDTH);
    private static final float INITIAL_GAME_VIEW_WIDTH = 20;
    private static final float INITIAL_GAME_VIEW_HEIGHT = INITIAL_GAME_VIEW_WIDTH * INITIAL_VIEWPORT_ASPECT_RATIO;

    private final CSClubGameMain main;

    private InputMultiplexer input;

    private OrthographicCamera camera;
    private Stage gameStage;
    private InputAdapter gameInput;

    private Stage uiStage;
    private WidgetGroup uiGroup;
    private WidgetGroup escapeMenuGroup;

    public PlatformerGame(CSClubGameMain main) {
        this.main = main;
    }

    @Override
    public void init() {
        setupGame();
        setupUI();

        input = new InputMultiplexer(gameStage, uiStage, gameInput);
    }

    private void setupGame() {
        camera = new OrthographicCamera(INITIAL_GAME_VIEW_WIDTH, INITIAL_GAME_VIEW_HEIGHT);
        gameStage = new Stage(new ExtendViewport(INITIAL_GAME_VIEW_WIDTH, INITIAL_GAME_VIEW_HEIGHT, camera));

        // detect escape press
        gameInput = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    changeState(PlatformerGameState.ESCAPE_MENU);
                    return true;
                }
                return false;
            }
        };
    }

    private void setupUI() {
        uiStage = new Stage(new FitViewport(INITIAL_VIEWPORT_WIDTH, INITIAL_VIEWPORT_HEIGHT));

        // This group is the thing we add UIs to
        uiGroup = new WidgetGroup();
        uiStage.addActor(uiGroup);
        uiGroup.setFillParent(true);

        Skin skin = main.getUiSkin();

        // This holds the escape menu
        escapeMenuGroup = new WidgetGroup();
        escapeMenuGroup.setFillParent(true);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.defaults().pad(5);
        escapeMenuGroup.addActor(table);

        TextButton continueGame = new TextButton("Continue Game", skin);
        table.add(continueGame).width(400);
        continueGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeState(PlatformerGameState.PLAYING);
            }
        });

        table.row();

        TextButton exitGame = new TextButton("Return to Main Menu", skin);
        table.add(exitGame).width(400);
        exitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO save game, reset game
                main.displayMainMenu();
            }
        });
    }

    @Override
    public void showGame() {
        Gdx.input.setInputProcessor(input);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void hideGame() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height, false);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameStage.getViewport().apply();
        gameStage.act(delta);
        gameStage.draw();

        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();
    }

    public void changeState(PlatformerGameState state) {
        if (state == PlatformerGameState.ESCAPE_MENU) {
            uiGroup.addActor(escapeMenuGroup);
        } else {
            uiGroup.removeActor(escapeMenuGroup);
        }
    }

    @Override
    public void pause() {
        changeState(PlatformerGameState.PAUSED_UNKNOWN);
    }

    @Override
    public void resume() {
        // I think this is mainly just something that happens on mobile when the user switches to another app and then
        // switches back.
        changeState(PlatformerGameState.ESCAPE_MENU);
    }

    @Override
    public void disposeGame() {
        gameStage.dispose();
        uiStage.dispose();
    }
}
