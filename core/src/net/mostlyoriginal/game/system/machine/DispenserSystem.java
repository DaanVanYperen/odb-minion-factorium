package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Dispenser;
import net.mostlyoriginal.game.component.Ingredient;
import net.mostlyoriginal.game.component.Inventory;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class DispenserSystem extends IntervalEntityProcessingSystem {


	public static final float DISPENSE_INTERVAL = 1f;
	protected GameScreenSetupSystem setupSystem;
	protected M<Inventory> mInventory;
	protected M<Pos> mPos;

	public DispenserSystem() {
		super(Aspect.all(Dispenser.class, Inventory.class, Pos.class), DISPENSE_INTERVAL);
	}

	@Override
	protected void process(Entity e) {
		final Inventory inventory = mInventory.get(e);
		for (Ingredient.Type type : Ingredient.Type.values()) {
			if (inventory.has(type, 1)) {
				inventory.dec(type, 1);
				dispense(e, type);
			}
		}
	}

	private void dispense(Entity e, Ingredient.Type type) {
		final Pos pos = mPos.get(e);
		setupSystem.createIngredient(pos.x + G.TILE_SIZE / 2, pos.y + G.TILE_SIZE / 2, type);
	}
}
