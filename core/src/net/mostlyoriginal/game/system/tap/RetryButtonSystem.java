package net.mostlyoriginal.game.system.tap;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.ShowerLiquid;
import net.mostlyoriginal.game.component.Sprinkle;
import net.mostlyoriginal.game.component.Tapped;
import net.mostlyoriginal.game.component.logic.RetryButton;
import net.mostlyoriginal.game.system.flow.RetryLevelSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class RetryButtonSystem extends EntityProcessingSystem {

	protected M<Tapped> mTapped;
	protected M<Anim> mAnim;
	protected M<Pos> mPos
			;
	protected M<Sprinkle> mSprinkle;
	RetryLevelSystem retryLevelSystem;

	public RetryButtonSystem() {
		super(Aspect.all(Tapped.class, RetryButton.class));
	}

	@Override
	protected void process(Entity e) {
		mTapped.remove(e);
		mAnim.get(e).scale = 0.9f;
		final Pos pos = mPos.get(e);
		pos.x+=2;
		pos.y+=2;

		e.edit().add(new Schedule().wait(0.1f).deleteFromWorld());

		final Sprinkle sprinkle = mSprinkle.create(e);
		sprinkle.liquid = ShowerLiquid.DUST;
		sprinkle.duration = 0.25f;

		retryLevelSystem.transition(0.5f);
	}
}
