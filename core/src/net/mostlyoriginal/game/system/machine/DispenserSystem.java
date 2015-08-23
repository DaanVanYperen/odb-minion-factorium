package net.mostlyoriginal.game.system.machine;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntityProcessingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Dispenser;
import net.mostlyoriginal.game.component.Ingredient;
import net.mostlyoriginal.game.component.Inventory;
import net.mostlyoriginal.game.component.Level;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class DispenserSystem extends IntervalEntityProcessingSystem {


	public static final float DISPENSE_INTERVAL = 1f;
	protected GameScreenSetupSystem setupSystem;
	protected M<Inventory> mInventory;
	protected M<Dispenser> mDispenser;
	protected M<Pos> mPos;
	protected M<Level> mLevel;
	private EntitySubscription levelMetadata;

	public DispenserSystem() {
		super(Aspect.all(Dispenser.class, Pos.class), DISPENSE_INTERVAL);
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		levelMetadata = world.getManager(AspectSubscriptionManager.class).get(Aspect.all(Level.class));
	}

	public Entity getLevel() {
		return levelMetadata.getEntities().size() > 0 ? world.getEntity(levelMetadata.getEntities().get(0)) : null;
	}

	@Override
	protected void process(Entity e) {
		final Inventory inventory = mLevel.get(getLevel()).input;
		final Dispenser dispenser = mDispenser.get(e);

		if (inventory.has(dispenser.type, 1)) {
			inventory.dec(dispenser.type, 1);
			dispense(e, dispenser.type);
		}
	}

	private void dispense(Entity e, Ingredient.Type type) {
		final Pos pos = mPos.get(e);
		setupSystem.createIngredient(pos.x + G.TILE_SIZE / 2, pos.y + G.TILE_SIZE / 2, type);
	}
}
