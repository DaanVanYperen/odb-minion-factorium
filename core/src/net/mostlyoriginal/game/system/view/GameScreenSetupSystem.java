package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.util.Anims;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenSetupSystem extends PassiveSystem {

	public static final int CELL_SIZE = 20;
	public static final int LAYER_CONVEYER = 1000;
	public static final int LAYER_CONVEYABLE = 1500;
	public static final int LAYER_FACTORIES = 2000;
	GameScreenAssetSystem assetSystem;

	M<Anim> mAnim;


	@Override
	protected void initialize() {

		initBackground();
		initMap(map1);
	}

	/**
	 * Conveyer: 1 = up, 2 = right, 3 = down, 4 = left
	 *           5, 6, 7, 8 = clockwise corners (top-left first)
	 *
	 * Splicer = 9
	 * Chick spawner = 10 up,11 right,12 down ,13 left
	 */

	private static int[][] map1 = new int[][] {
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,5,2,2,2,6,0},
		{0,0,0,0,1,4,4,4,7,0},
		{0,0,0,0,1,0,0,0,3,0},
		{11,2,2,2,9,4,4,4,3,13},
		{0,0,0,0, 0,0,0,0,0,0},
		{0,5,2,2,5,2,2,6,3,0},
		{0,1,0,0,1,0,0,3,3,0},
		{0,1,0,0,1,0,0,3,3,0},
		{0,1,0,0,8,4,4,7,3,0},
		{0,1,0,0,1,0,0,0,3,0},
		{0,1,0,0,1,0,0,0,3,0},
		{0,8,4,4,4,4,4,4,7,0},
		{0,0,0,0,1,0,0,0,0,0},
	};

	private void initMap(int[][] map) {

		// slightly offset so we can have out of bounds parts.
		for (int x = -1; x < G.TILES_W+1; x++) {
			for (int y = -1; y < G.TILES_H+1; y++) {
				int cx = (x) * G.TILE_SIZE;
				int cy = (y) * G.TILE_SIZE + G.FOOTER_H;
				final int id = map[G.TILES_H - y][x + 1];
				switch(id) {
					case 0:;
						break;
					case 1:
					case 2:
					case 3:
					case 4:
						createBeltStraight(cx, cy, -90 * (id - 1));
						break;
					case 5 :
					case 6 :
					case 7 :
					case 8 :
						createBeltBend(cx, cy, -90 * (id - 5));
						break;
					case 9 :
						createSplicer(cx, cy);
						break;
					case 10 :
					case 11 :
					case 12 :
					case 13 :
						createDispenser(cx, cy, -90 * (id - 10), Ingredient.Type.CHICK, 999);
						break;
				}
			}
		}
	}

	private void createDispenser(int x, int y, int angle, Ingredient.Type type, int count) {
		new EntityBuilder(world).with(
				new Pos(x,y),
				new Bounds(2,2,18,18),
				new Inventory().inc(type,count),
				new Conveyer(90f),
				new Angle(angle),
				new Dispenser()).build();
	}

	private void createBeltStraight(int x, int y, int angle) {
		new EntityBuilder(world).with(
				new Pos(x,y),
				new Bounds(2,2,18,18),
				new Anim("belt-straight"),
				new Renderable(1000),
				new Angle(angle),
				new Conveyer(90f)).build();

		createIngredient(x+G.TILE_SIZE/2, y+G.TILE_SIZE/2, Ingredient.Type.CHICK);
	}

	public void createIngredient(float x, float y, Ingredient.Type type) {
		new EntityBuilder(world).with(
				new Pos(x-3,y-3),
				new Bounds(0,0,5,5),
				new Anim("ingredient-" + type.name()),
				new Ingredient(type),
				new Renderable(LAYER_CONVEYABLE),
				new SpawnProtected(),
				new Physics()).build();
	}

	private void createBeltBend(int x, int y, int angle) {
		new EntityBuilder(world).with(
				new Pos(x,y),
				new Bounds(2,2,18,18),
				new Anim("belt-bend"),
				new Renderable(LAYER_CONVEYER),
				new Angle(angle),
				new Conveyer(45)).build();
	}

	private void createSplicer(int x, int y) {
		new EntityBuilder(world).with(
				new Pos(x-2,y-2),
				new Bounds(6,0,25-6,26),
				new Anim("factory-splicer"),
				new Inventory(),
				new Autopickup(),
				new Splicer(),
				new Renderable(LAYER_FACTORIES),
				new Angle(0f),
				new Conveyer(90f)).build();
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
