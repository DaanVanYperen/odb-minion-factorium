package net.mostlyoriginal.game;

import com.badlogic.gdx.Game;
import net.mostlyoriginal.game.screen.LevelScreen;

public class GdxArtemisGame extends Game {

	private static GdxArtemisGame instance;
	private int levelIndex;

	@Override
	public void create() {
		instance = this;
		restart();
	}

	public void restart() {
		levelIndex = 0;
		nextLevel();
	}

	public void nextLevel() {
		setScreen(new LevelScreen(++levelIndex));
	}

	public static GdxArtemisGame getInstance()
	{
		return instance;
	}
}
