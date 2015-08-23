package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
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

	protected M<Level> mLevel;
	protected M<Inventory> mInventory;

	LevelTransitionSystem levelTransitionSystem;
	private StarSystem starSystem;
	private float switchCooldown = 0;
	private AbstractAssetSystem gameScreenAssetSystem;
	private AbstractAssetSystem abstractAssetSystem;

	public VictorySystem() {
		super(Aspect.all(Level.class, Inventory.class),
				Aspect.all(Sink.class));
	}

	@Override
	protected void begin() {
		if (switchCooldown > 0) {
			switchCooldown -= world.delta;
			if (switchCooldown <= 0) {
				prepareNextLevel();
				gameScreenAssetSystem.playSfx(starSystem.points >= 5 ? "highscore" : "victory");
			}
		}
	}

	@Override
	protected void process(Entity levelEntity, Entity sinkEntity) {

		final Level level = mLevel.get(levelEntity);
		final Inventory levelInventory = mInventory.get(levelEntity);
		final Inventory sinkInventory = mInventory.get(sinkEntity);

		boolean recentlySinked = sinkInventory.totalItems() > 0;

		sinkInventory.emptyInto(levelInventory);

		float factor = (float) levelInventory.totalItems() / (float) Math.max(level.goals.totalItems(), 0);

		starSystem.setPoints((int) (factor * 3f));

		if (recentlySinked && levelInventory.containsAtLeast(level.goals) ) {
			// provide a little cooldown so user has time to submit above average.
			switchCooldown = 2f;
			abstractAssetSystem.playSfx("score");
		}

	}

	private void prepareNextLevel() {
		levelTransitionSystem.transition(1.5f);
	}
}
