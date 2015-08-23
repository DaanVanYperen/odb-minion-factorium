package net.mostlyoriginal.game.system.tap;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.Rotatable;
import net.mostlyoriginal.game.component.Tapped;

/**
 * @author Daan van Yperen
 */
@Wire
public class RotateSystem extends EntityProcessingSystem {

	protected M<Angle> mAngle;
	protected M<Tapped> mTapped;
	private AbstractAssetSystem abstractAssetSystem;

	public RotateSystem() {
		super(Aspect.all(Tapped.class, Rotatable.class, Angle.class));
	}

	@Override
	protected void process(Entity e) {
		mTapped.remove(e);

		final Angle angle = mAngle.get(e);
		angle.rotation -= 90;
		if ( angle.rotation < 0 ) angle.rotation += 360;
		abstractAssetSystem.playSfx("rotate");
	}
}
