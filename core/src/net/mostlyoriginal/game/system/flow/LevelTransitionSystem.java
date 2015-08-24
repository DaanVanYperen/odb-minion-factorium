package net.mostlyoriginal.game.system.flow;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.EntityBuilder;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.Star;
import net.mostlyoriginal.game.component.logic.Transition;
import net.mostlyoriginal.game.system.StarSystem;

/**
 * Switch to next level.
 *
 * @author Daan van Yperen
 */
@Wire
public class LevelTransitionSystem extends EntityProcessingSystem {

	protected ComponentMapper<Transition> mTransition;
	private AbstractAssetSystem gameScreenAssetSystem;

	private StarSystem starSystem;

	public LevelTransitionSystem() {
		super(Aspect.all(Transition.class));
	}

	/** Transition to screen after delay in seconds. */
	public void transition(float delay) {
		new EntityBuilder(world).with(new Schedule().wait(delay).add(new Transition()));
	}

	@Override
	protected void process(Entity e) {
		try {
			GdxArtemisGame.getInstance().nextLevel(starSystem.points);
		} catch (Exception ex ) {
			throw new RuntimeException(ex);
		}
	}
}
