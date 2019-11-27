package edu.shoreline.csclubgame.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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
import edu.shoreline.csclubgame.platformergame.world.Platform;
import edu.shoreline.csclubgame.platformergame.world.Player;
import edu.shoreline.csclubgame.platformergame.world.WorldContactManager;
import edu.shoreline.csclubgame.util.ScreenGame;

public class PlatformerGame extends ScreenGame {
    private static final int INITIAL_VIEWPORT_WIDTH = 1280;
    private static final int INITIAL_VIEWPORT_HEIGHT = 720;
    private static final float INITIAL_VIEWPORT_ASPECT_RATIO =
            ((float) INITIAL_VIEWPORT_HEIGHT) / ((float) INITIAL_VIEWPORT_WIDTH);
    private static final float INITIAL_GAME_VIEW_WIDTH = 40;
    private static final float INITIAL_GAME_VIEW_HEIGHT = INITIAL_GAME_VIEW_WIDTH * INITIAL_VIEWPORT_ASPECT_RATIO;

    private static final float PHYSICS_SIMULATION_STEP = 1f / 60f;

    private final CSClubGameMain main;

    private InputMultiplexer input;

    private boolean gameRunning;
    private OrthographicCamera camera;
    private Stage gameStage;
    private World gameWorld;
    private float physicsAccumulator;
    private edu.shoreline.csclubgame.platformergame.world.Player player;
    private Box2DDebugRenderer debugRenderer;

    private InputAdapter auxInput;

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
        setupInput();

        input = new InputMultiplexer(gameStage, uiStage, auxInput, player.getController());

        changeState(PlatformerGameState.PLAYING);
    }

    private void setupGame() {
        camera = new OrthographicCamera(INITIAL_GAME_VIEW_WIDTH, INITIAL_GAME_VIEW_HEIGHT);
        gameStage = new Stage(new ExtendViewport(INITIAL_GAME_VIEW_WIDTH, INITIAL_GAME_VIEW_HEIGHT, camera));

        gameWorld = new World(new Vector2(0, -9.8f), true);

        edu.shoreline.csclubgame.platformergame.world.WorldContactManager contactManager = new WorldContactManager();
        gameWorld.setContactListener(contactManager);

        player = new Player(gameWorld, contactManager, new Vector2(5, 5));

        new Platform(gameWorld, new Vector2(0, 0), new Vector2(20, 2));

        debugRenderer = new Box2DDebugRenderer();
    }

    private void setupInput() {
        auxInput = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (gameRunning) {
                        changeState(PlatformerGameState.ESCAPE_MENU);
                    } else {
                        changeState(PlatformerGameState.PLAYING);
                    }
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
        if (gameRunning) {
            stepPhysics(delta);
            moveCamera(delta);
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameStage.getViewport().apply();
        if (gameRunning) {
            gameStage.act(delta);
        }
        gameStage.draw();

        debugRenderer.render(gameWorld, camera.combined);

        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();
    }

    private void stepPhysics(float delta) {
        float maxDelta = Math.min(delta, 0.25f);
        physicsAccumulator += maxDelta;

        while (physicsAccumulator >= PHYSICS_SIMULATION_STEP) {
            updatePlayer();
            gameWorld.step(PHYSICS_SIMULATION_STEP, 6, 2);
            physicsAccumulator -= PHYSICS_SIMULATION_STEP;
        }
    }

    private void updatePlayer() {
        player.update();
    }

    private void moveCamera(float delta) {
        camera.position.add(new Vector3(player.getBody().getPosition(), 0f).sub(camera.position).scl(delta * 10));
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

    public void changeState(PlatformerGameState state) {
        if (state == PlatformerGameState.ESCAPE_MENU) {
            uiGroup.addActor(escapeMenuGroup);
        } else {
            uiGroup.removeActor(escapeMenuGroup);
        }
        gameRunning = state == PlatformerGameState.PLAYING;
    }

    @Override
    public void disposeGame() {
        gameStage.dispose();
        uiStage.dispose();
    }
}
