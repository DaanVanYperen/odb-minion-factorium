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
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.component.common.JamBuilder;
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

	protected M<Physics> mPhysics;
	private Vector2 vector2;
	private AbstractAssetSystem abstractAssetSystem;

	public GameScreenSetupSystem(int levelIndex) {
		this.levelIndex = levelIndex;
	}

	M<Anim> mAnim;
	private int levelIndex;

	JamBuilder builder = new JamBuilder();

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
			builder.create(world).Anim("star-0")
					.Star(i)
					.Pos(G.VIEPORT_WIDTH / 2 - 13 * 5 + i * 13 - 2, G.VIEPORT_HEIGHT / 2 - 11 - 2)
					.Renderable(LAYER_OVERLAYS + 4).build();
		}
	}

	private void initResetButton() {
		Entity entity = builder.create(world)
				.with(Tappable.class, RetryButton.class, Tint.class, Invisible.class)
				.Anim("button-restart")
				.Bounds(0, 0, 24, 24)
				.Renderable(LAYER_OVERLAYS)
				.Pos(G.VIEPORT_WIDTH / G.ZOOM - 5 - 24, 5 + G.FOOTER_H)
				.build();

		entity.edit().add(new Schedule()
				.wait(2f)
				.remove(Invisible.class)
				.add(newColorAnimation(new Tint(1f, 1f, 1f, 0f), new Tint(1f, 1f, 1f, 1f), 1f)));
	}


	private ColorAnimation newColorAnimation(Tint colorA, Tint colorB, float speed) {
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

		builder.create(world).Tint("eed6ee").Label("level " + levelIndex).Font("5x5").Pos(4, G.VIEPORT_HEIGHT / 2 - 2).Renderable(LAYER_OVERLAYS + 1);
		builder.create(world).Tint("ffe6ff").Label(level.name).Font("5x5").Pos(4, G.VIEPORT_HEIGHT / 2 - 9).Renderable(LAYER_OVERLAYS + 1);

		if (level.tutorial) {
			builder.create(world).Pos(75, 115).Anim("mouse").Renderable(LAYER_OVERLAYS + 1);
		}

		if (level.scoreboard) {
			builder.create(world).Tint("000000").Label("Winner!").Font("5x5").Pos(G.VIEPORT_WIDTH / 4 - 20, G.VIEPORT_HEIGHT / 4 + 30).Renderable(LAYER_OVERLAYS + 1);
			builder.create(world).Tint("000000").Label(GdxArtemisGame.getInstance().starsCollected + " stars!").Font("5x5").Pos(G.VIEPORT_WIDTH / 4 - 20, G.VIEPORT_HEIGHT / 4 - 9 + 30).Renderable(LAYER_OVERLAYS + 1);
			builder.create(world).Tint("000000").Label("(You monster)").Font("5x5").Pos(G.VIEPORT_WIDTH / 4 - 35, G.VIEPORT_HEIGHT / 4 - 18 + 30).Renderable(LAYER_OVERLAYS + 1);
		}

		// spawn as entity so we can use it to track progress towards goal.
		builder.create(world).Inventory().build().edit().add(level);

		initMap(level);
	}

	private void initCursor() {
		new EntityBuilder(world).with(
				new MouseCursor(),
				new Bounds(-3, -3, 3, 3))
				.with(Pos.class).build();
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
					case '.':
						break;
					case '<':
						angle -= 90;
					case 'v':
						angle -= 90;
					case '>':
						angle -= 90;
					case '^':
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
								e = createSink(cx, cy, angle, Ingredient.Type.CHICK);
							}
							if (id2 == 'B') {
								e = createSink(cx, cy, angle, Ingredient.Type.BUNNY);
							}
							if (id2 == 'G') {
								e = createSink(cx, cy, angle, Ingredient.Type.GOOGLIE_EYE);
							}
							if (id2 == 'Y') {
								e = createSink(cx, cy, angle, Ingredient.Type.BLIND_CHICK);
							}
							if (id2 == 'M') {
								e = createSink(cx, cy, angle, Ingredient.Type.CHICKBUNNY);
							}
							if (id2 == 'G') {
								e = createSink(cx, cy, angle, Ingredient.Type.GOOGLIE_EYE);
							}
							if (id2 == 'Z') {
								e = createSink(cx, cy, angle, Ingredient.Type.MINION_ENLARGED);
							}
						}

						break;
					case '4':
						angle -= 90;
					case '3':
						angle -= 90;
					case '2':
						angle -= 90;
					case '1':
						if (id2 == 'i' || id2 == 'I') {
							e = createBeltBendInverse(cx, cy, angle);
						} else e = createBeltBend(cx, cy, angle);

						break;
					case 'S':
						e = createSplicer(cx, cy);
						break;
				}

				switch (id2) {
					case 'd':
					case 'I':
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

		return builder.create(world)
				.Pos(x, y)
				.Bounds(2, 2, 18, 18)
				.Conveyer(90f)
				.Angle(angle)
				.Dispenser(type).build();
	}

	private void spawnPointer(int x, int y, int angle, Ingredient.Type type, boolean inverted) {
		int i = angle + 90;
		vector2 = v2.set(0, -G.TILE_SIZE - 3).setAngle(i);

		builder.create(world)
				.Pos(x + 3 + vector2.x, y + 6 + vector2.y)
				.Angle(i - 90 + (inverted ? -180 : 0), 7, 5)
				.Anim("pointer")
				.Tint(1f, 1f, 1f, 0.6f)
				.Renderable(LAYER_CONVEYER + 1)
				.build();

		vector2 = v2.set(-15, -G.TILE_SIZE + 4).setAngle(i);

		final String id = "ingredient-" + type.name();
		final Animation animation = abstractAssetSystem.get(id);
		final TextureRegion region = animation.getKeyFrames()[0];
		builder.create(world)
				.Pos(x + vector2.x + 10 - region.getRegionWidth() / 2, y + 10 + vector2.y - region.getRegionHeight() / 2)
				.Tint(1f, 1f, 1f, 0.8f)
				.Anim(id)
				.Renderable(LAYER_CONVEYER + 2)
				.build();
	}

	private Entity createSink(int x, int y, int angle, Ingredient.Type type) {
		spawnPointer(x, y, angle - 180, type, true);

		return builder.create(world)
				.Pos(x, y)
				.Bounds(2, 2, 16, 16)
				.Inventory()
				.Autopickup()
				.with(Sink.class)
				.build();
	}

	private Entity createBeltStraight(int x, int y, int angle) {
		return builder.create(world)
				.Pos(x, y)
				.Bounds(2, 2, 18, 18)
				.Anim("belt-straight")
				.Renderable(1000)
				.Angle(angle)
				.Conveyer(90f)
				.build();
	}

	public Entity createIngredient(float x, float y, Ingredient.Type type) {
		return builder.create(world)
				.Pos(x - 3, y - 3)
				.Bounds(0, 0, 3, 3)
				.Anim("ingredient-" + type.name())
				.Ingredient(type)
				.Renderable(LAYER_CONVEYABLE)
				.SpawnProtected()
				.Physics()
				.with(Conveyable.class)
				.build();
	}

	private Entity createBeltBend(int x, int y, int angle) {
		return builder.create(world)
				.Pos(x, y)
				.Bounds(2, 2, 18, 18)
				.Anim("belt-bend")
				.Renderable(LAYER_CONVEYER)
				.Angle(angle)
				.Conveyer(45)
				.build();
	}

	private Entity createBeltBendInverse(int x, int y, int angle) {
		return builder.create(world)
				.Pos(x, y)
				.Bounds(2, 2, 18, 18)
				.Anim("belt-bend-inverse")
				.Renderable(LAYER_CONVEYER)
				.Angle(angle)
				.Conveyer(45 + 180)
				.build();
	}

	private Entity createCrusherY(int x, int y) {
		return builder.create(world)
				.Pos(x, y + 1)
				.Bounds(5, 0, 15, 20)
				.Anim("factory-crusher")
				.Renderable(LAYER_FACTORIES)
				.Angle(0f)
				.with(Crusher.class)
				.build();
	}

	private Entity createShowerY(int x, int y) {
		return builder.create(world)
				.Pos(x, y)
				.Bounds(0, 0, 20, 20)
				.Anim("factory-shower")
				.Renderable(LAYER_FACTORIES)
				.Angle(0f)
				.with(Shower.class).build();
	}


	private Entity createCrusherX(int x, int y) {
		return builder.create(world)
				.Pos(x + 2, y)
				.Bounds(0, 5, 20, 15)
				.Anim("factory-crusher")
				.Renderable(LAYER_FACTORIES)
				.Angle(-90f)
				.with(Crusher.class).build();
	}

	private Entity createShowerX(int x, int y) {
		return builder.create(world)
				.Pos(x + 6, y)
				.Bounds(0, 0, 20, 20)
				.Anim("factory-shower")
				.Renderable(LAYER_FACTORIES)
				.Angle(-90f)
				.with(Shower.class)
				.build();
	}

	private Entity createPainter(int x, int y) {
		return builder.create(world)
				.Pos(x, y)
				.Bounds(0, 0, 20, 20)
				.Anim("factory-shower")
				.Renderable(LAYER_FACTORIES)
				.Angle(0f)
				.Shower(ShowerLiquid.PAINT)
				.build();
	}

	private Entity createGouger(int x, int y) {
		return builder.create(world)
				.Pos(x - 2, y - 2)
				.Bounds(0, 0, 20, 20)
				.Anim("factory-gouger")
				.Renderable(LAYER_FACTORIES)
				.Angle(0f)
				.with(Gouger.class)
				.build();
	}


	private Entity createSplicer(int x, int y) {
		return builder.create(world)
				.Pos(x - 2, y - 2)
				.Bounds(6, 0, 25 - 6, 26)
				.Anim("factory-splicer")
				.Inventory()
				.Autopickup()
				.with(Splicer.class)
				.Renderable(LAYER_FACTORIES)
				.Angle(0f)
				.Conveyer(90f)
				.build();
	}


	private void initBackground() {

		for (int x = 0; x < G.TILES_W; x++) {
			for (int y = 0; y < G.TILES_H; y++) {
				createBackground(x * G.TILE_SIZE, y * G.TILE_SIZE + G.FOOTER_H, getBackgroundCellId(x, y));
			}
		}
		builder.create(world).Pos(0, G.VIEPORT_HEIGHT / 2 - G.HEADER_H).Anim("header").Renderable(LAYER_OVERLAYS);
		builder.create(world).Pos(0, 0).Anim("footer").Renderable(LAYER_OVERLAYS);
	}

	private String getBackgroundCellId(int x, int y) {
		return (x + y * (G.TILES_W + 1)) % 2 == 0 ? "cell-empty" : "cell-empty2";
	}

	private void createBackground(int x, int y, String id) {
		final float c = MathUtils.sin(x + y) * 0.025f + 0.975f;
		new JamBuilder<>().edit(Anims.createAnimAt(world,
				x,
				y,
				id,
				1)).Tint(c, c, c, 1f);
	}

	Vector2 v2 = new Vector2();

	public Entity createIngredientEject(float x, float y, Ingredient.Type type, float angle) {
		final Entity ingredient = createIngredient(x, y, type);

		ingredient.edit().remove(Conveyable.class).add(new Schedule().wait(2f).add(new Conveyable()));

		final Physics physics = mPhysics.get(ingredient);

		v2.set(50, 0).setAngle(-angle + 90);
		physics.vx = v2.x;
		physics.vy = v2.y;

		return ingredient;
	}
}
