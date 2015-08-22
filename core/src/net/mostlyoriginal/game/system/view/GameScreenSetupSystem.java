package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.util.Anims;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenSetupSystem extends PassiveSystem {

	public static final int CELL_SIZE = 20;
	GameScreenAssetSystem assetSystem;

	M<Anim> mAnim;


	@Override
	protected void initialize() {

		initBackground();

	}

	private void initBackground() {

		for (int x = 0; x < G.TILES_W; x++) {
			for (int y = 0; y < G.TILES_H; y++) {
				createBackground(x * G.TILE_SIZE, y * G.TILE_SIZE + G.FOOTER_H, getBackgroundCellId(x, y));
			}
		}
	}

	private String getBackgroundCellId(int x, int y) {
		return (x + y * (G.TILES_W +1)) % 2 == 0 ? "cell-empty" : "cell-empty2";
	}

	private void createBackground(int x, int y, String id) {
		Anims.createAnimAt(world,
				x,
				y,
				id,
				1);
	}

}
