package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.*;

/**
 * @author Daan van Yperen
 */
@Wire
public class CrusherSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	M<Inventory> mInventory;
	M<Ingredient> mIngredient;
	protected M<Wet> mWet;
	protected M<Drenched> mDrenched;
	protected M<SpawnProtected> mSpawnProtected;

	public CrusherSystem() {

		super(Aspect.all(Crusher.class, Pos.class, Bounds.class),
				Aspect.all(Ingredient.class, Pos.class, Bounds.class).exclude(SpawnProtected.class));
	}

	@Override
	protected void process(Entity factory, Entity ingredient) {
		if ( ingredient.isActive() && collisionSystem.overlaps(factory,ingredient))
		{
			act(factory, ingredient);
		}
	}

	private void act(Entity crusher, Entity ingredient) {
		ingredient.deleteFromWorld();
	}
}
