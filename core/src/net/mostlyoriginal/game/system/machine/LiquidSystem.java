package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Color;
import net.mostlyoriginal.api.component.graphics.ColorAnimation;
import net.mostlyoriginal.api.component.graphics.InterpolationStrategy;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.ShowerLiquid;
import net.mostlyoriginal.game.component.Sprinkle;
import net.mostlyoriginal.game.component.Wet;
import net.mostlyoriginal.game.component.common.JamBuilder;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class LiquidSystem extends EntityProcessingSystem {

	protected M<Pos> mPos;
	protected M<Wet> mWet;
	protected M<Sprinkle> mSprinkle;

	JamBuilder jamBuilder = new JamBuilder();

	public LiquidSystem() {
		super(Aspect.all(Pos.class).one(Sprinkle.class, Wet.class));
	}

	@Override
	protected void process(Entity e) {
		if (mSprinkle.has(e)) sprinkle(e);
		if (mWet.has(e)) drip(e);
	}

	private void drip(Entity e) {
		final Wet wet = mWet.get(e);

		wet.cooldown -= world.delta;
		if (wet.cooldown <= 0) {
			wet.cooldown = 0.1f + (1 - (wet.duration / wet.DEFAULT_DURATION)) * 0.4f;
			final Pos pos = mPos.get(e);
			createLiquidParticle(pos.xy.x + 2, pos.xy.y + 8, true, wet.liquid);
			createLiquidParticle(pos.xy.x + 2, pos.xy.y + 8, true, wet.liquid);
		}

		wet.duration -= world.delta;
		if (wet.duration <= 0) {
			mWet.remove(e);
		}
	}

	private void sprinkle(Entity shower) {
		final Sprinkle sprinkle = mSprinkle.get(shower);

		sprinkle.cooldown -= world.delta;
		if (sprinkle.cooldown <= 0) {
			sprinkle.cooldown = 0.1f;
			final Pos pos = mPos.get(shower);
			createLiquidParticle(pos.xy.x + 4, pos.xy.y + 8, false, sprinkle.liquid);
			if (sprinkle.liquid == ShowerLiquid.DUST) {
				for (int i = 0; i < 10; i++) {
					createLiquidParticle(pos.xy.x + 4, pos.xy.y + 8, false, sprinkle.liquid);
				}
			}
		}

		sprinkle.duration -= world.delta;
		if (sprinkle.duration <= 0) {
			mSprinkle.remove(shower);
		}
	}

	Vector2 v = new Vector2();

	private Entity createLiquidParticle(float x, float y, boolean drip, ShowerLiquid liquid) {

		v.set(MathUtils.random(20, 30), 0).setAngle(MathUtils.random(0, 359));

		final Color colorA;
		final Color colorB;

		switch (liquid) {
			case BLOOD:
				colorA = new Color(1f, 0f, 0f, MathUtils.random(0.8f, 0.9f));
				colorB = new Color(1f, 0f, 0f, 0.9f);
				break;
			case PAINT:
				if (MathUtils.random(0, 100) < 5 || drip) {
					colorA = new Color(0.5f, 0.9f, 0.5f, MathUtils.random(0.75f, 0.9f));
					colorB = new Color(0.5f, 0.9f, 0.5f, 0.9f);
				} else {
					colorA = new Color(0f, 1f, 0f, MathUtils.random(0.75f, 0.9f));
					colorB = new Color(0f, 1f, 0f, 0.9f);
				}
				break;
			case SPARKLE:
				colorA = new Color(1f, 0.8f, 0.4f, MathUtils.random(0.9f, 1f));
				colorB = new Color(1f, 0.8f, 0.4f, 0.9f);
				break;
			case DUST:
			case STEAM:
				colorA = new Color(0.9f, 0.9f, 1f, MathUtils.random(0.75f, 0.9f));
				colorB = new Color(0.9f, 0.9f, 1f, 0.9f);
				break;
			default:
				if (MathUtils.random(0, 100) < 5 || drip) {
					colorA = new Color(0.7f, 0.9f, 1f, MathUtils.random(0.75f, 0.9f));
					colorB = new Color(0.9f, 0.9f, 1f, 0.9f);
				} else {
					colorA = new Color(0f, 0f, 1f, MathUtils.random(0.75f, 0.9f));
					colorB = new Color(0f, 0f, 1f, 0.9f);
				}
				break;
		}


		final Anim anim = new Anim("particle-water");
		anim.scale = MathUtils.random(0.1f, 0.5f);
		if (drip) {
			v.set(0, MathUtils.random(-7, -3)).setAngle(MathUtils.random(-5, 5));
			anim.scale = MathUtils.random(0.1f, 0.2f);
			x += MathUtils.random(-5f, 5f);
			y += MathUtils.random(-1f, 1f);
		}

		if (liquid == ShowerLiquid.STEAM) {
			v.y = 5;
			x += 10;
			y += 5 + MathUtils.random(13);
		}

		if (liquid == ShowerLiquid.DUST) {
			v.set(0, 10).setAngle(MathUtils.random(0, 360));
			x += v.x;
			y += v.y;
			v.scl(4);
		}

		if (liquid == ShowerLiquid.SPARKLE) {
			v.set(0, 10).setAngle(MathUtils.random(0, 360));
			x += v.x;
			y += v.y;
			v.scl(4);
		}

		final Physics physics = new Physics();
		physics.vx = v.x;
		physics.vy = v.y;
		physics.vr = MathUtils.random(-1f, 1f);

		Entity entity = jamBuilder.create(world)
				.Pos(x, y)
				.Color("000000")
				.Renderable(liquid == ShowerLiquid.STEAM ? GameScreenSetupSystem.LAYER_OVERLAYS + 1 :
						liquid == ShowerLiquid.SPARKLE ? GameScreenSetupSystem.LAYER_OVERLAYS + 100 :
								liquid == ShowerLiquid.DUST ? GameScreenSetupSystem.LAYER_CONVEYER - 1 : GameScreenSetupSystem.LAYER_VAPOR)
				.Angle(MathUtils.random(100)).build();

		entity.edit().add(
				newColorAnimation(colorA, colorB, 0.5f))
				.add(anim)
				.add(new Schedule()
						.wait(0.5f)
						.deleteFromWorld())
				.add(physics);

		return entity;

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
