package net.mostlyoriginal.game.system.view;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.Sink;
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
	public static final int LAYER_OVERLAYS = 2500;
	GameScreenAssetSystem assetSystem;

	M<Anim> mAnim;


	@Override
	protected void initialize() {

		initCursor();
		initBackground();
		initMap(map1);
	}

	private void initCursor() {
		new EntityBuilder(world).with(
				new MouseCursor(),
				new Bounds(-3, -3, 3, 3),
				new Pos()).build();
	}

	/**
	 * Conveyer: 1 = up, 2 = right, 3 = down, 4 = left
	 * 5, 6, 7, 8 = clockwise corners (top-left first)
	 * <p/>
	 * Splicer = 9
	 * Chick spawner = 10 up,11 right,12 down ,13 left
	 * Bunny spawner = 14 up,15 right,16 down ,17 left
	 * 18,19,20,21 = counter clockwise corners (top-left first)
	 * Produce Receiver = 25
	 */

	private static int[][] map1 = new int[][]{
			{0, 0, 0, 0,25, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
			{11, 2, 2, 2, 9, 4, 4, 4, 4, 17},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{ 0,18, 4,19, 0, 0, 5, 2, 6, 0},
			{ 0, 3, 0, 1, 0, 0, 1, 0, 3, 0},
			{ 0,21, 2,20, 0, 0, 8, 4, 7, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	};

	private void initMap(int[][] map) {

		// slightly offset so we can have out of bounds parts.
		for (int x = -1; x < G.TILES_W + 1; x++) {
			for (int y = -1; y < G.TILES_H + 1; y++) {
				int cx = (x) * G.TILE_SIZE;
				int cy = (y) * G.TILE_SIZE + G.FOOTER_H;
				final int id = map[G.TILES_H - y][x + 1];

				Entity e = null;
				switch (id) {
					case 0:
						;
						break;
					case 1:
					case 2:
					case 3:
					case 4:
						e = createBeltStraight(cx, cy, -90 * (id - 1));
						break;
					case 5:
					case 6:
					case 7:
					case 8:
						e = createBeltBend(cx, cy, -90 * (id - 5));
						break;
					case 9:
						e = createSplicer(cx, cy);
						break;
					case 10:
					case 11:
					case 12:
					case 13:
						e = createDispenser(cx, cy, -90 * (id - 10), Ingredient.Type.CHICK, 999);
						break;
					case 14:
					case 15:
					case 16:
					case 17:
						e = createDispenser(cx, cy, -90 * (id - 14), Ingredient.Type.BUNNY, 999);
						break;
					case 18:
					case 19:
					case 20:
					case 21:
						e = createBeltBendInverse(cx, cy, -90 * (id - 18));
						break;
					case 25:
						e = createSink(cx, cy);
						break;
				}

				if ( e != null ) {
					e.edit().add(new Draggable()).add(new Tappable()).add(new Rotatable());
				}
			}
		}
	}

	private Entity createDispenser(int x, int y, int angle, Ingredient.Type type, int count) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2, 18, 18),
				new Inventory().inc(type, count),
				new Conveyer(90f),
				new Angle(angle),
				new Dispenser()).build();
	}

	private Entity createSink(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2,16,16),
				new Inventory(),
				new Autopickup(),
				new Sink()).build();
	}

	private Entity createBeltStraight(int x, int y, int angle) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2, 18, 18),
				new Anim("belt-straight"),
				new Renderable(1000),
				new Angle(angle),
				new Conveyer(90f)).build();
	}

	public Entity createIngredient(float x, float y, Ingredient.Type type) {
		return new EntityBuilder(world).with(
				new Pos(x - 3, y - 3),
				new Bounds(0, 0, 5, 5),
				new Anim("ingredient-" + type.name()),
				new Ingredient(type),
				new Renderable(LAYER_CONVEYABLE),
				new SpawnProtected(),
				new Physics()).build();
	}

	private Entity createBeltBend(int x, int y, int angle) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2, 18, 18),
				new Anim("belt-bend"),
				new Renderable(LAYER_CONVEYER),
				new Angle(angle),
				new Conveyer(45)).build();
	}

	private Entity createBeltBendInverse(int x, int y, int angle) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2, 18, 18),
				new Anim("belt-bend-inverse"),
				new Renderable(LAYER_CONVEYER),
				new Angle(angle),
				new Conveyer(45 + 180)).build();
	}

	private Entity createSplicer(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x - 2, y - 2),
				new Bounds(6, 0, 25 - 6, 26),
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
		return (x + y * (G.TILES_W + 1)) % 2 == 0 ? "cell-empty" : "cell-empty2";
	}

	private void createBackground(int x, int y, String id) {
		Anims.createAnimAt(world,
				x,
				y,
				id,
				1);
	}

}
