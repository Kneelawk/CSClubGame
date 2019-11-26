package edu.shoreline.csclubgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import edu.shoreline.csclubgame.mainmenu.MainMenu;

public class CSClubGameMain extends Game {
	private MainMenu mainMenu;
	
	@Override
	public void create () {
		mainMenu = new MainMenu(this);

		displayMainMenu();
	}
	
	@Override
	public void dispose () {
		super.dispose();

	}

	public void displayMainMenu() {
		setScreen(mainMenu);
	}
}
