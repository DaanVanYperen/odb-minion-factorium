package net.mostlyoriginal.game.system.view;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.component.ui.Label;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.component.logic.RetryButton;
import net.mostlyoriginal.game.util.Anims;

import java.time.temporal.TemporalAmount;

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

	protected M<Physics> mPhysics;
	private Vector2 vector2;
	private AbstractAssetSystem abstractAssetSystem;

	public GameScreenSetupSystem(int levelIndex) {
		this.levelIndex = levelIndex;
	}

	M<Anim> mAnim;
	private int levelIndex;


	@Override
	protected void initialize() {

		initCursor();
		initBackground();
		initStars();
		loadLevel(levelIndex);
		initResetButton();
	}

	private void initStars() {
		for (int i = 0; i < 5; i++) {
			new EntityBuilder(world).with(new Anim("star-0"),
					new Star(i),
					new Pos(G.VIEPORT_WIDTH/2 - 13*5 + i*13 - 2,G.VIEPORT_HEIGHT/2-11 - 2), new Renderable(LAYER_OVERLAYS+4));
		}
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

		Label label = new Label("level " + levelIndex);
		label.fontName="5x5";
		new EntityBuilder(world).with(new Color("eed6ee"), new Pos(4, G.VIEPORT_HEIGHT/2 - 2), label, new Renderable(LAYER_OVERLAYS+1));

		label = new Label(level.name);
		label.fontName="5x5";
		new EntityBuilder(world).with(new Color("ffe6ff"),new Pos(4, G.VIEPORT_HEIGHT/2 - 9), label, new Renderable(LAYER_OVERLAYS+1));

		if (level.tutorial )
		{
			new EntityBuilder(world).with(new Pos(75, 115), new Anim("mouse"), new Renderable(LAYER_OVERLAYS+1));
		}

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
						} else if (id2 == 'm') {
							e = createDispenser(cx, cy, angle, Ingredient.Type.CHICKBUNNY);
						} else if (id2 == 'g') {
							e = createDispenser(cx, cy, angle, Ingredient.Type.GOOGLIE_EYE);
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
							if (id2 == '3') {
								createGouger(cx, cy);
							}
							if (id2 == '4') {
								createPainter(cx, cy);
							}
							if (id2 == 'C') {
								e = createSink(cx, cy,angle, Ingredient.Type.CHICK);
							}
							if (id2 == 'B') {
								e = createSink(cx, cy,angle, Ingredient.Type.BUNNY);
							}
							if (id2 == 'G') {
								e = createSink(cx, cy,angle, Ingredient.Type.GOOGLIE_EYE);
							}
							if (id2 == 'Y') {
								e = createSink(cx, cy,angle, Ingredient.Type.BLIND_CHICK);
							}
							if (id2 == 'M') {
								e = createSink(cx, cy,angle, Ingredient.Type.CHICKBUNNY);
							}
							if (id2 == 'G') {
								e = createSink(cx, cy,angle, Ingredient.Type.GOOGLIE_EYE);
							}
							if (id2 == 'Z') {
								e = createSink(cx, cy,angle, Ingredient.Type.MINION_ENLARGED);
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
						if (id2 == 'i' || id2 == 'I') {
							e = createBeltBendInverse(cx, cy, angle);
						} else e = createBeltBend(cx, cy, angle);

						break;
					case 'S' :
						e = createSplicer(cx, cy);
						break;
				}

				switch (id2) {
					case 'd' :
					case 'I' :
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

		spawnPointer(x, y, angle, type, false);

		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(2, 2, 18, 18),
				new Conveyer(90f),
				new Angle(angle),
				new Dispenser(type)).build();
	}

	private void spawnPointer(int x, int y, int angle, Ingredient.Type type, boolean inverted) {
		int i =angle+90;
		vector2 = v2.set(0, -G.TILE_SIZE - 3).setAngle(i);

		new EntityBuilder(world).with(
				new Pos(x + 3 + vector2.x, y + 6  + vector2.y),
				new Angle(i-90+(inverted?-180:0), 7, 5),
				new Anim("pointer"),
				new Color(1f,1f,1f,0.6f),
				new Renderable(LAYER_CONVEYER + 1)
		).build();

//		for (i =0; i>-360; i--) {
			vector2 = v2.set(-15, -G.TILE_SIZE + 4).setAngle(i);

		final String id = "ingredient-" + type.name();
		final Animation animation = abstractAssetSystem.get(id);
		final TextureRegion region = animation.getKeyFrames()[0];
		new EntityBuilder(world).with(
					new Pos(x + vector2.x + 10 - region.getRegionWidth()/2, y + 10 + vector2.y - region.getRegionHeight()/2),
					new Color(1f, 1f, 1f, 0.8f),
					new Anim(id),
					new Renderable(LAYER_CONVEYER + 2)
			).build();
		//}
	}

	private Entity createSink(int x, int y, int angle, Ingredient.Type type) {
		spawnPointer(x, y, angle-180, type, true);

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
				new Bounds(0, 0, 3, 3),
				new Anim("ingredient-" + type.name()),
				new Ingredient(type),
				new Renderable(LAYER_CONVEYABLE),
				new Conveyable(),
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
				new Bounds(5, 0, 15, 20),
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
				new Bounds(0, 5, 20, 15),
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

	private Entity createPainter(int x, int y) {
		final Shower shower = new Shower();
		shower.liquid = ShowerLiquid.PAINT;
		return new EntityBuilder(world).with(
				new Pos(x, y),
				new Bounds(0, 0, 20, 20),
				new Anim("factory-shower"),
				new Renderable(LAYER_FACTORIES),
				new Angle(0f),
				shower).build();
	}

	private Entity createGouger(int x, int y) {
		return new EntityBuilder(world).with(
				new Pos(x-2, y-2),
				new Bounds(0, 0, 20, 20),
				new Anim("factory-gouger"),
				new Renderable(LAYER_FACTORIES),
				new Angle(0f),
				new Gouger()).build();
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
		new EntityBuilder(world).with(new Pos(0, G.VIEPORT_HEIGHT/2 - G.HEADER_H), new Anim("header"), new Renderable(LAYER_OVERLAYS));
		new EntityBuilder(world).with(new Pos(0, 0), new Anim("footer"), new Renderable(LAYER_OVERLAYS));
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

	Vector2 v2 = new Vector2();

	public Entity createIngredientEject(float x, float y, Ingredient.Type type, float angle) {
		final Entity ingredient = createIngredient(x, y, type);

		ingredient.edit().remove(Conveyable.class).add(new Schedule().wait(2f).add(new Conveyable()));

		final Physics physics = mPhysics.get(ingredient);

		v2.set(50,0).setAngle(-angle+90);
		physics.vx = v2.x;
		physics.vy = v2.y;

		return ingredient;
	}
}
