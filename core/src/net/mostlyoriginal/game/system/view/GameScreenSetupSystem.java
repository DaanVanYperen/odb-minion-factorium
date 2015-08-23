package net.mostlyoriginal.game.system.view;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.component.logic.RetryButton;
import net.mostlyoriginal.game.util.Anims;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenSetupSystem extends PassiveSystem {

	public static final int CELL_SIZE = 20;
	public static final int LAYER_CONVEYER = 1000;
	public static final int LAYER_CONVEYABLE = 1500;
	public static final int LAYER_VAPOR = 1999;
	public static final int LAYER_FACTORIES = 2000;
	public static final int LAYER_OVERLAYS = 2500;
	public static final int LAYER_DRAGGING = 2600;
	GameScreenAssetSystem assetSystem;

	public GameScreenSetupSystem(int levelIndex) {
		this.levelIndex = levelIndex;
	}

	M<Anim> mAnim;
	private int levelIndex;


	@Override
	protected void initialize() {

		initCursor();
		initBackground();
		loadLevel(levelIndex);
		initResetButton();
	}

	private void initResetButton() {
		new EntityBuilder(world)
				.with(new Tappable(), new RetryButton(), new Color())
				.with(new Schedule().wait(2f).remove(Invisible.class).add(newColorAnimation(new Color(1f,1f,1f,0f), new Color(1f,1f,1f,1f),1f)), new Invisible())
				.with(new Anim("button-restart"), new Bounds(0, 0, 24, 24), new Renderable(LAYER_OVERLAYS), new Pos(G.VIEPORT_WIDTH / G.ZOOM - 5 - 24, 5 + G.FOOTER_H)).build();
	}


	private ColorAnimation newColorAnimation(Color colorA, Color colorB, float speed) {
		return new ColorAnimation(colorA, colorB, new InterpolationStrategy() {
			@Override
			public float apply(float v1, float v2, float a) {
				return Interpolation.linear.apply(v1, v2, a);
			}
		}, speed, 1f / speed);
	}

	private void loadLevel(int levelIndex) {

		//final Json json = new Json();
		//json.setUsePrototypes(false);
		//System.out.println(json.prettyPrint(new Level()));

		final Level level = new Json().fromJson(Level.class, Gdx.files.internal("level/level" + levelIndex + ".json"));

		// spawn as entity so we can use it to track progress towards goal.
		new EntityBuilder(world).with(level, new Inventory()).build();

		initMap(level);
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


	private void initMap(Level level) {

		// slightly offset so we can have out of bounds parts.
		for (int x = -1; x < G.TILES_W + 1; x++) {
			for (int y = -1; y < G.TILES_H + 1; y++) {
				int cx = (x) * G.TILE_SIZE;
				int cy = (y) * G.TILE_SIZE + G.FOOTER_H;
				final char id = level.structure[(G.TILES_H - y) * 2].charAt(x + 1);
				final char id2 = level.structure[(G.TILES_H - y) * 2 + 1].charAt(x + 1);

				Entity e = null;
				int angle = 0;
				switch (id) {
					case '.' :
						break;
					case '<' :
						angle -= 90;
					case 'v' :
						angle -= 90;
					case '>' :
						angle -= 90;
					case '^' :
						if (id2 == 'c') {
							e = createDispenser(cx, cy, angle, Ingredient.Type.CHICK);
						} else if (id2 == 'b') {
							e = createDispenser(cx, cy, angle, Ingredient.Type.BUNNY);
						} else {
							e = createBeltStraight(cx, cy, angle);
							if (id2 == '1') {
								if (angle == -90 || angle == -270) {
									createCrusherY(cx, cy);
								} else {
									createCrusherX(cx, cy);
								}
							}
							if (id2 == '2') {
								if (angle == -90 || angle == -270) {
									createShowerY(cx, cy);
								} else {
									createShowerX(cx, cy);
								}
							}
						}

						break;
					case '4' :
						angle -= 90;
					case '3' :
						angle -= 90;
					case '2' :
						angle -= 90;
					case '1' :
						if (id2 == 'i') {
							e = createBeltBendInverse(cx, cy, angle);
						} else e = createBeltBend(cx, cy, angle);

						break;
					case 'S' :
						e = createSplicer(cx, cy);
						break;
					case 'X' :
						e = createSink(cx, cy);
						break;
				}

				switch (id2) {
					case 'd' :
						if (e != null) {
							makeDraggable(e);
						}
						break;
				}

			}
		}
	}

	private void makeDraggable(Entity e) {
		e.edit().add(new Draggable()).add(new Tappable()).add(new Rotatable());
	}

	private Entity createDispenser(int x, int y, int angle, Ingredient.Type type) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2, 18, 18),
				new Conveyer(90f),
				new Angle(angle),
				new Dispenser(type)).build();
	}

	private Entity createSink(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2, 16, 16),
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

	private Entity createCrusherY(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x, y + 1),
				new Bounds(0, 0, 20, 20),
				new Anim("factory-crusher"),
				new Renderable(LAYER_FACTORIES),
				new Angle(0f),
				new Crusher()).build();
	}

	private Entity createShowerY(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(0, 0, 20, 20),
				new Anim("factory-shower"),
				new Renderable(LAYER_FACTORIES),
				new Angle(0f),
				new Shower()).build();
	}


	private Entity createCrusherX(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x+2, y),
				new Bounds(0, 0, 20, 20),
				new Anim("factory-crusher"),
				new Renderable(LAYER_FACTORIES),
				new Angle(-90f),
				new Crusher()).build();
	}

	private Entity createShowerX(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x+6, y),
				new Bounds(0, 0, 20, 20),
				new Anim("factory-shower"),
				new Renderable(LAYER_FACTORIES),
				new Angle(-90f),
				new Shower()).build();
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
		final float c = MathUtils.sin(x + y) * 0.025f + 0.975f;
		Anims.createAnimAt(world,
				x,
				y,
				id,
				1).edit().add(new Color(c, c, c, 1f));
	}

}
