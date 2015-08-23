package net.mostlyoriginal.game;

import com.badlogic.gdx.Game;
import net.mostlyoriginal.game.screen.LevelScreen;

public class GdxArtemisGame extends Game {

	public static final int START_LEVEL = 3;
	private static GdxArtemisGame instance;
	private int levelIndex = START_LEVEL;

	@Override
	public void create() {
		instance = this;
		restart();
	}

	public void restart() {
		levelIndex = START_LEVEL-1;
		nextLevel();
	}

	public void nextLevel() {
		setScreen(new LevelScreen(++levelIndex));
	}

	public static GdxArtemisGame getInstance()
	{
		return instance;
	}

	public void retryLevel() {
		setScreen(new LevelScreen(levelIndex));
	}
}
