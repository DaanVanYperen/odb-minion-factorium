package net.mostlyoriginal.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import net.mostlyoriginal.game.screen.LevelScreen;

public class GdxArtemisGame extends Game {

	public static final int START_LEVEL = 1;
	private static GdxArtemisGame instance;
	private int levelIndex = START_LEVEL;

	@Override
	public void create() {
		instance = this;
		restart();
	}

	public void restart() {
		levelIndex = START_LEVEL-1;
		playMusic();
		nextLevel();
	}

	private void playMusic() {
		final Music music = Gdx.audio.newMusic(Gdx.files.internal("sfx/music.mp3"));
		music.setLooping(true);
		music.play();
		music.setVolume(0.3f);
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
