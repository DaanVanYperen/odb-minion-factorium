package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Animation;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
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
	protected M<Pos> mPos;
	protected M<SpawnProtected> mSpawnProtected;
	protected AbstractAssetSystem abstractAssetSystem;
	protected M<Sprinkle> mSprinkle;
	protected M<Shower> mShower;

	GameScreenSetupSystem setupSystem;
	private M<Ingredient> mIngredient;
	private AbstractAssetSystem gameScreenAssetSystem;

	public ShowerSystem() {

		super(Aspect.all(Shower.class, Pos.class, Bounds.class),
				Aspect.all(Ingredient.class, Pos.class, Bounds.class).exclude(SpawnProtected.class));
	}

	@Override
	protected void process(Entity shower, Entity ingredient) {
		if (showerActive(shower)) {
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
		final ShowerLiquid liquid = mShower.get(showery).liquid;
		switch (liquid) {
			case PAINT:
				if (mIngredient.get(ingredient).type == Ingredient.Type.CHICKBUNNY) {
					// replace minion with correct type.
					final Pos sourcePos = mPos.get(ingredient);
					ingredient.deleteFromWorld();
					ingredient = setupSystem.createIngredient(sourcePos.x + 3, sourcePos.y + 3, Ingredient.Type.MINION_PAINTED);
					break;
				}
		}
		mDrenched.create(ingredient);
		mWet.create(ingredient).liquid = liquid;
		mSpawnProtected.create(ingredient);
		gameScreenAssetSystem.playSfx("shower");
	}
}
