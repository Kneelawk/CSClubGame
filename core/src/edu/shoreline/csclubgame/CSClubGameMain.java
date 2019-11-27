package edu.shoreline.csclubgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import edu.shoreline.csclubgame.mainmenu.MainMenu;
import edu.shoreline.csclubgame.platformergame.PlatformerGame;

public class CSClubGameMain extends Game {
    private MainMenu mainMenu;
    private PlatformerGame platformerGame;

    private TextureAtlas uiAtlas;
    private Skin uiSkin;

    @Override
    public void create() {
        uiAtlas = new TextureAtlas(Gdx.files.internal("skins/neon/neon-ui.atlas"));
        uiSkin = new Skin();
        uiSkin.addRegions(uiAtlas);
        uiSkin.load(Gdx.files.internal("skins/neon/neon-ui.json"));

        mainMenu = new MainMenu(this);
        platformerGame = new PlatformerGame(this);

        displayMainMenu();
    }

    @Override
    public void dispose() {
        super.dispose();
        uiSkin.dispose();
        uiAtlas.dispose();
    }

    public TextureAtlas getUiAtlas() {
        return uiAtlas;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    public void exit() {
        Gdx.app.exit();
    }

    public void displayMainMenu() {
        setScreen(mainMenu);
    }

    public void displayGameScreen() {
        setScreen(platformerGame);
    }
}
