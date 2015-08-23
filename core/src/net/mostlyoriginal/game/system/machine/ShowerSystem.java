package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
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

	protected M<Wet> mWet;
	protected M<Drenched> mDrenched;
	protected M<Anim> mAnim;
	protected M<SpawnProtected> mSpawnProtected;
	protected AbstractAssetSystem abstractAssetSystem;
	protected M<Sprinkle> mSprinkle;
	protected M<Shower> mShower;

	public ShowerSystem() {

		super(Aspect.all(Shower.class, Pos.class, Bounds.class),
				Aspect.all(Ingredient.class, Pos.class, Bounds.class).exclude(SpawnProtected.class));
	}

	@Override
	protected void process(Entity shower, Entity ingredient) {
		if ( showerActive(shower) ) {
			mSprinkle.create(shower).liquid = mShower.get(shower).liquid;
			if (ingredient.isActive() && collisionSystem.overlaps(shower, ingredient)) {
				act(shower, ingredient);
			}
		}
	}

	private boolean showerActive(Entity shower) {
		final Anim anim = mAnim.get(shower);
		final Animation animation = abstractAssetSystem.get(anim.id);
		animation.setPlayMode(Animation.PlayMode.LOOP);
		return animation.getKeyFrameIndex(anim.age) == 4;
	}

	private void act(Entity showery, Entity ingredient) {
		mDrenched.create(ingredient);
		mWet.create(ingredient).liquid = mShower.get(showery).liquid;
		mSpawnProtected.create(ingredient);
	}
}
