package net.mostlyoriginal.game.system.drag;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.Conveyer;
import net.mostlyoriginal.game.component.Draggable;

/**
 * @author Daan van Yperen
 */
@Wire
public class GridOverlapHelperSystem extends EntitySystem {

	CollisionSystem collisionSystem;
	private Entity flyweightEntity;

	public GridOverlapHelperSystem() {
		super(Aspect.one(Draggable.class, Conveyer.class));
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		flyweightEntity = createFlyweightEntity();
	}

	/**
	 * Does passed entity overlap any grid entity? (except self)
	 */
	public boolean overlaps(Entity e) {
		IntBag actives = subscription.getEntities();
		int[] array = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++) {
			flyweightEntity.id = array[i];
			if (flyweightEntity.getId() != e.getId() && collisionSystem.overlaps(flyweightEntity, e)) {
				return true;
			}
		}
		return false;
	}


	@Override
	protected void processSystem() {
	}
}
