package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.Autopickup;
import net.mostlyoriginal.game.component.Ingredient;
import net.mostlyoriginal.game.component.Inventory;

/**
 * @author Daan van Yperen
 */
@Wire

public class InventoryScoopSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	M<Inventory> mInventory;
	M<Ingredient> mIngredient;


	public InventoryScoopSystem() {

		super(Aspect.all(Autopickup.class, Inventory.class, Pos.class, Bounds.class),
				Aspect.all(Ingredient.class, Pos.class, Bounds.class));
	}

	@Override
	protected void process(Entity factory, Entity ingredient) {
		if ( ingredient.isActive() && collisionSystem.overlaps(factory,ingredient))
		{
			pickup(factory, ingredient);
		}
	}

	private void pickup(Entity factory, Entity ingredientEntity) {
		final Ingredient ingredient = mIngredient.get(ingredientEntity);

		// add items to inventory.
		mInventory.get(factory).items[ingredient.type.ordinal()] += ingredient.count;

		// poof!
		ingredientEntity.deleteFromWorld();
	}
}
