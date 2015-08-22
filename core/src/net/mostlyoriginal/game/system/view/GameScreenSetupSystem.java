package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
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
		initMap(map1);
	}

	private static int[][] map1 = new int[][] {
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,5,2,2,6,0,0},
		{0,0,1,0,0,3,0,0},
		{0,0,1,0,0,3,0,0},
		{0,0,8,4,4,7,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0}};

	private void initMap(int[][] map) {

		for (int x = 0; x < G.TILES_W; x++) {
			for (int y = 0; y < G.TILES_H; y++) {
				int cx = x * G.TILE_SIZE;
				int cy = y * G.TILE_SIZE + G.HEADER_H;
				switch(map[G.TILES_H - 1 - y][x]) {
					case 0:;
						break;
					case 1 :
						createBeltStraight(cx,cy,0);
						break;
					case 2 :
						createBeltStraight(cx,cy,-90);
						break;
					case 3 :
						createBeltStraight(cx,cy,-180);
						break;
					case 4 :
						createBeltStraight(cx, cy, -270);
						break;
					case 5 :
						createBeltBend(cx, cy, 0);
						break;
					case 6 :
						createBeltBend(cx,cy,-90);
						break;
					case 7 :
						createBeltBend(cx,cy,-180);
						break;
					case 8 :
						createBeltBend(cx,cy,-270);
						break;
				}
			}
		}
	}

	private void createBeltStraight(int x, int y, int angle) {
		new EntityBuilder(world).with(
				new Pos(x,y),
				new Anim("belt-straight"),
				new Renderable(1000),
				new Angle(angle)).build();
	}

	private void createBeltBend(int x, int y, int angle) {
		new EntityBuilder(world).with(
				new Pos(x,y),
				new Anim("belt-bend"),
				new Renderable(1000),
				new Angle(angle)).build();
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
