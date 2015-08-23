package net.mostlyoriginal.game.system.tap;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.Tapped;
import net.mostlyoriginal.game.component.logic.RetryButton;
import net.mostlyoriginal.game.system.flow.RetryLevelSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class RetryButtonSystem extends EntityProcessingSystem {

	protected M<Tapped> mTapped;
	RetryLevelSystem retryLevelSystem;

	public RetryButtonSystem() {
		super(Aspect.all(Tapped.class, RetryButton.class));
	}

	@Override
	protected void process(Entity e) {
		mTapped.remove(e);
		retryLevelSystem.transition(0.5f);
	}
}
