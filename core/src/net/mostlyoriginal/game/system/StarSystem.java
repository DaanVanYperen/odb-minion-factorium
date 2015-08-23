package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.ShowerLiquid;
import net.mostlyoriginal.game.component.Sprinkle;
import net.mostlyoriginal.game.component.Star;

/**
 * @author Daan van Yperen
 */
@Wire
public class StarSystem extends EntityProcessingSystem {

	public int points = 0;

	protected M<Star> mStar;
	protected M<Anim> mAnim;
	protected M<Sprinkle> mSprinkle;
	private AbstractAssetSystem abstractAssetSystem;

	public StarSystem() {
		super(Aspect.all(Anim.class, Star.class));
	}

	public void setPoints(int points) {
		if ( points != this.points ) {
			this.points = points;
			abstractAssetSystem.playSfx("woop");
		}
	}

	@Override
	protected void process(Entity e) {
		final Star star = mStar.get(e);
		final boolean glow = star.point < points;
		if ( mAnim.get(e).id.equals("star-0") && glow)
		{
			final Sprinkle sprinkle = mSprinkle.create(e);
			sprinkle.liquid = ShowerLiquid.SPARKLE;
			sprinkle.duration = 1f;
		}
		mAnim.get(e).id = glow ? "star-1" : "star-0";
	}
}
