package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.Conveyable;
import net.mostlyoriginal.game.component.Ingredient;
import net.mostlyoriginal.game.system.machine.CrusherSystem;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class ConveyableExpirationSystem extends EntityProcessingSystem {

	protected M<Conveyable> mConveyable;
	protected M<Ingredient> mIngredient;
	protected M<Anim> mAnim;
	protected M<Renderable> mRenderable;

	CrusherSystem crusherSystem;

	public ConveyableExpirationSystem() {
		super(Aspect.all(Conveyable.class, Ingredient.class));
	}

	@Override
	protected void process(Entity e) {
		final Conveyable conveyable = mConveyable.get(e);
		if (mIngredient.get(e).type != Ingredient.Type.BLOOD) {
			conveyable.touchedAgo += world.delta;
			if (conveyable.touchedAgo >= 1f) {
				final Entity entity = crusherSystem.replace(e, Ingredient.Type.BLOOD);
				moveToFloor(entity);
			}
		}
	}

	private void moveToFloor(Entity entity) {
		// it is now on the floor and unsalvageable.
		mRenderable.get(entity).layer = GameScreenSetupSystem.LAYER_CONVEYER-10;
		mConveyable.remove(entity);
	}
}
