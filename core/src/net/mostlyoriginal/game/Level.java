package net.mostlyoriginal.game;

import java.io.Serializable;

/**
 * @author Daan van Yperen
 */
public class Level {

	public int height = G.TILES_H+2;
	public int width = G.TILES_W+2;
	public String[] structure = new String[]{
			".......X..",
			"          ",
			".......^..",
			"          ",
			"..5<6..^..",
			"          ",
			"..v.^..^..",
			"          ",
			"..8>7..^..",
			"          ",
			">>>>>>>S<<",
			"c        b",
			".1>>>>>2..",
			"          ",
			".^.....v..",
			"          ",
			".^.....v..",
			"          ",
			".^.....v..",
			"          ",
			".4<<<<<3..",
			"   ddd    ",
			"..........",
			"          ",
			"..........",
			"          ",
			"..........",
			"          ",
	};
}
