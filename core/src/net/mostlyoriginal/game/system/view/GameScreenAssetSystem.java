package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenAssetSystem extends AbstractAssetSystem {

	public GameScreenAssetSystem() {
		super("tileset.png");
	}

	@Override
	protected void initialize() {
		super.initialize();

		loadSounds(new String[]{
				"button-play",
				"button-rewind",
				"chick-squeek",
				"factory-1",
				"factory-2",
				"flatten-eye",
				"hatching",
				"highscore",
				"hybrid-emerges",
				"loss",
				"shower",
				"stamper",
				"victory",

				"drag",
				"drop",
				"rotate",
				"score",
				"woop"
		});

		add("cell-empty", 20, 260, 20, 20, 1);
		add("cell-empty2", 40, 260, 20, 20, 1);

		add("belt-straight", 20, 200, 20, 20, 4).setFrameDuration(1 / 15f);
		add("belt-bend", 20, 220, 20, 20, 4).setFrameDuration(1 / 15f);
		add("belt-bend-inverse", 20, 240, 20, 20, 4).setFrameDuration(1 / 15f);

		add("ingredient-CHICK", 160, 20, 6, 6, 1);
		add("ingredient-BUNNY", 160, 32, 6, 8, 1);
		add("ingredient-CHICKBUNNY", 200, 31, 6, 9, 1);
		add("ingredient-BLIND_CHICK", 167, 20, 6, 6, 1);
		add("ingredient-BEAD_EYE", 179, 21, 1, 1, 1);
		add("ingredient-GOOGLIE_EYE", 175, 20, 3, 3, 1);
		add("ingredient-MINION_PAINTED", 215, 31, 6, 9, 1);
		add("ingredient-MINION_GOOGLED", 207, 31, 7, 9, 1);
		add("ingredient-MINION_ENLARGED", 222, 27, 8, 12, 1);
		add("ingredient-BLOOD", 119, 30, 10, 8, 1);

		add("factory-splicer", 20, 79, 25, 26, 2);

		add("draggable-indicator", 60, 260, 20, 20, 1);

		add("factory-crusher", 20, 120, 17, 23, 2).setFrameDuration(1 / 2f);
		add("factory-shower", 20, 160, 10, 23, 5).setFrameDuration(1 / 6f);
		add("factory-gouger", 20, 79, 25, 26, 2);

		add("button-restart", 45, 300, 24, 24, 1);

		add("particle-water", 80, 260, 8, 8, 1);

		add("pointer", 80, 268, 12, 6, 1);
		add("header",200, 60,160,20, 1);
		add("footer",200,320,160,2, 1);

		add("star-0",173,60,11,11, 1);
		add("star-1",186,60,11,11, 1);

	}
}
