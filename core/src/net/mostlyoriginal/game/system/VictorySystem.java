package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.game.component.Inventory;
import net.mostlyoriginal.game.component.Level;
import net.mostlyoriginal.game.component.Sink;
import net.mostlyoriginal.game.system.flow.LevelTransitionSystem;

/**
 * Test victory conditions.
 *
 * @author Daan van Yperen
 */
@Wire
public class VictorySystem extends DualEntityProcessingSystem {

	private boolean levelSwitchPending = false;

	protected M<Level> mLevel;
	protected M<Inventory> mInventory;

	LevelTransitionSystem levelTransitionSystem;

	public VictorySystem() {
		super(Aspect.all(Level.class, Inventory.class),
				Aspect.all(Sink.class));
	}

	@Override
	protected void begin() {
		super.begin();
	}

	@Override
	protected void process(Entity levelEntity, Entity sinkEntity) {
		final Level level = mLevel.get(levelEntity);
		final Inventory levelInventory = mInventory.get(levelEntity);
		final Inventory sinkInventory = mInventory.get(sinkEntity);

		sinkInventory.emptyInto(levelInventory);

		if ( levelInventory.containsAtLeast(level.goals) ) {
			prepareNextLevel();
		}

	}

	private void prepareNextLevel() {
		if (!levelSwitchPending )
		{
			levelSwitchPending = true;
			levelTransitionSystem.transition(0.25f);
		}
	}
}
