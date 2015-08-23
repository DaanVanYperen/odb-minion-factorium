package net.mostlyoriginal.game.system.flow;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.EntityBuilder;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.RetryLevel;
import net.mostlyoriginal.game.component.logic.Transition;

/**
 * @author Daan van Yperen
 */
@Wire
public class RetryLevelSystem extends EntityProcessingSystem {

	protected ComponentMapper<RetryLevel> mRetryLevelSystem;

	public RetryLevelSystem() {
		super(Aspect.all(RetryLevel.class));
	}

	/** Transition to screen after delay in seconds. */
	public void transition(float delay) {
		new EntityBuilder(world).with(new Schedule().wait(delay).add(new RetryLevel()));
	}

	@Override
	protected void process(Entity e) {
		try {
			GdxArtemisGame.getInstance().retryLevel();
		} catch (Exception ex ) {
			throw new RuntimeException(ex);
		}
	}
}
