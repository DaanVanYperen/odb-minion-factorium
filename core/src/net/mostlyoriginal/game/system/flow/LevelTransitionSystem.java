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
import net.mostlyoriginal.game.component.logic.Transition;

/**
 * Switch to next level.
 *
 * @author Daan van Yperen
 */
@Wire
public class LevelTransitionSystem extends EntityProcessingSystem {

	protected ComponentMapper<Transition> mTransition;
	private AbstractAssetSystem gameScreenAssetSystem;

	public LevelTransitionSystem() {
		super(Aspect.all(Transition.class));
	}

	/** Transition to screen after delay in seconds. */
	public void transition(float delay) {
		new EntityBuilder(world).with(new Schedule().wait(delay).add(new Transition()));
		gameScreenAssetSystem.playSfx("victory");
	}

	@Override
	protected void process(Entity e) {
		try {
			GdxArtemisGame.getInstance().nextLevel();
		} catch (Exception ex ) {
			throw new RuntimeException(ex);
		}
	}
}
