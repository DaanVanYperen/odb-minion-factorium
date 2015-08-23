package net.mostlyoriginal.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.GdxArtemisGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = G.VIEPORT_WIDTH;
		config.height = G.VIEPORT_HEIGHT;
		config.resizable = false;
		config.title = "Minion Factorium";
		new LwjglApplication(new GdxArtemisGame(), config);
	}
}
