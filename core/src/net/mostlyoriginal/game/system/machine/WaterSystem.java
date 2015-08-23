package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class WaterSystem extends EntityProcessingSystem {

	protected M<Pos> mPos;
	protected M<Wet> mWet;
	protected M<Sprinkle> mSprinkle;

	public WaterSystem() {
		super(Aspect.all(Pos.class).one(Sprinkle.class, Wet.class));
	}

	@Override
	protected void process(Entity e) {
		if ( mSprinkle.has(e)) sprinkle(e);
		if ( mWet.has(e)) drip(e);
	}

	private void drip(Entity e) {
		final Wet wet = mWet.get(e);

		wet.cooldown -= world.delta;
		if ( wet.cooldown <= 0 ) {
			wet.cooldown = 0.5f;
			final Pos pos = mPos.get(e);
			createWaterParticle(pos.x+4, pos.y+8, true);
		}

		wet.duration -= world.delta;
		if ( wet.duration <= 0 ) {
			mWet.remove(e);
		}
	}

	private void sprinkle(Entity shower) {
		final Sprinkle sprinkle = mSprinkle.get(shower);

		sprinkle.cooldown -= world.delta;
		if ( sprinkle.cooldown <= 0 ) {
			sprinkle.cooldown = 0.1f;
			final Pos pos = mPos.get(shower);
			createWaterParticle(pos.x+4, pos.y+8, false);
		}

		sprinkle.duration -= world.delta;
		if ( sprinkle.duration <= 0 ) {
			mSprinkle.remove(shower);
		}
	}

	Vector2 v = new Vector2();

	private Entity createWaterParticle(float x, float y, boolean drip) {

		v.set(MathUtils.random(20,30),0).setAngle(MathUtils.random(0, 359));

		final Color colorA;
		final Color colorB;
		if ( MathUtils.random(0,100) < 5 || drip )
		{
			colorA = new Color(0.7f, 0.9f, 1f, MathUtils.random(0.75f, 0.9f));
			colorB = new Color(0.9f, 0.9f, 1f, 0.9f);
		} else {
			colorA = new Color(0f, 0f, 1f, MathUtils.random(0.75f, 0.9f));
			colorB = new Color(0f, 0f, 1f, 0.9f);
		}


		final Anim anim = new Anim("particle-water");
		anim.scale = MathUtils.random(0.1f,0.5f);
		if ( drip ) {
			v.set(0, MathUtils.random(-7, -5));
			anim.scale = MathUtils.random(0.1f,0.2f);
			x += MathUtils.random(-5f,5f);
			y += MathUtils.random(-1f,1f);
		}

		final Physics physics = new Physics();
		physics.vx = v.x;
		physics.vy = v.y;
		physics.vr = MathUtils.random(-1f,1f);


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
}
