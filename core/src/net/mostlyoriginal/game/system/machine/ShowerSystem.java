package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class ShowerSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	M<Inventory> mInventory;
	M<Ingredient> mIngredient;
	protected M<Pos> mPos;
	protected M<Wet> mWet;
	protected M<Drenched> mDrenched;
	protected M<SpawnProtected> mSpawnProtected;

	public ShowerSystem() {

		super(Aspect.all(Shower.class, Pos.class, Bounds.class),
				Aspect.all(Ingredient.class, Pos.class, Bounds.class).exclude(SpawnProtected.class));
	}

	@Override
	protected void process(Entity shower, Entity ingredient) {
		if ( showerActive(shower) ) {
			sprinkle(shower);
			if (ingredient.isActive() && collisionSystem.overlaps(shower, ingredient)) {
				act(shower, ingredient);
			}
		}
	}

	private boolean showerActive(Entity shower) {
		return true;
	}

	float sprinkleCooldown = 0;
	private void sprinkle(Entity shower) {
		sprinkleCooldown -= world.delta;
		if ( sprinkleCooldown <= 0 ) {
			sprinkleCooldown = 0.1f;
			final Pos pos = mPos.get(shower);
			createWaterParticle(pos.x+4, pos.y+8);
		}
	}

	Vector2 v = new Vector2();

	private Entity createWaterParticle(float x, float y) {

		v.set(MathUtils.random(20,30),0).setAngle(MathUtils.random(0, 359));

		final Physics physics = new Physics();
		physics.vx = v.x;
		physics.vy = v.y;
		physics.vr = MathUtils.random(-1f,1f);

		final Anim anim = new Anim("particle-water");
		anim.scale = MathUtils.random(0.1f,0.5f);
		final Color colorA;
		final Color colorB;
		if ( MathUtils.random(0,100) < 5 )
		{
			colorA = new Color(0.7f, 0.9f, 1f, MathUtils.random(0.75f, 0.9f));
			colorB = new Color(0.9f, 0.9f, 1f, 0.9f);
		} else {
			colorA = new Color(0f, 0f, 1f, MathUtils.random(0.75f, 0.9f));
			colorB = new Color(0f, 0f, 1f, 0.9f);
		}

		return new EntityBuilder(world).with(
				new Pos(x, y),
				newColorAnimation(colorA, colorB,0.5f),
				new Color(),
				anim,
				new Renderable(GameScreenSetupSystem.LAYER_VAPOR),
				new Schedule().wait(0.5f).deleteFromWorld(),
				new Angle(MathUtils.random(100)),
				physics).build();
	}

	private ColorAnimation newColorAnimation(Color colorA, Color colorB, float speed) {
		return new ColorAnimation(colorA, colorB, new InterpolationStrategy() {
			@Override
			public float apply(float v1, float v2, float a) {
				return Interpolation.exp5Out.apply(v1, v2, a);
			}
		}, speed, -1);
	}

	private void act(Entity showery, Entity ingredient) {
		mDrenched.create(ingredient);
		mWet.create(ingredient);
		mSpawnProtected.create(ingredient);
	}
}
