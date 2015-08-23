package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.Star;

/**
 * @author Daan van Yperen
 */
@Wire
public class StarSystem extends EntityProcessingSystem {

	public int points = 0;

	protected M<Star> mStar;
	protected M<Anim> mAnim;

	public StarSystem() {
		super(Aspect.all(Anim.class, Star.class));
	}

	public void setPoints(int points) {
		if ( points != this.points ) {
			this.points = points;
		}
	}

	@Override
	protected void process(Entity e) {
		final Star star = mStar.get(e);
		mAnim.get(e).id = (star.point < points) ? "star-1" : "star-0";
	}
}
