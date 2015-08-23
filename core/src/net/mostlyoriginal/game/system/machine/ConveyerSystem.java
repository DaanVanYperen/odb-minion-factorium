package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.Conveyable;
import net.mostlyoriginal.game.component.Conveyer;

/**
 * @author Daan van Yperen
 */
@Wire
public class ConveyerSystem extends DualEntityProcessingSystem {

	public static final int CONVEY_SPEED = 20;
	public static final int CORRECTION_SPEED = 5;
	CollisionSystem collisionSystem;

	public M<Pos> mPos;
	public M<Angle> mAngle;
	public M<Conveyer> mConveyer;
	public M<Bounds> mBounds;
	public M<Physics> mPhysics;

	public ConveyerSystem() {
		super(Aspect.all(Conveyer.class, Bounds.class, Pos.class),
				Aspect.all(Physics.class, Bounds.class, Pos.class, Conveyable.class));
	}

	@Override
	protected void process(Entity belt, Entity conveyable) {
		if ( collisionSystem.overlaps(belt,conveyable))
		{
			convey(belt, conveyable);
		}
	}

	Angle notAngled = new Angle(0);
	Vector2 vec = new Vector2();
	Vector2 vec2 = new Vector2();

	private void convey(Entity belt, Entity item) {

		// apply force along belt direction.

		final float beltAngle = getAngle(belt);
		vec.set(CONVEY_SPEED,0).setAngle(beltAngle);
/*		final Pos itemPos = mPos.get(item);
		final Pos beltPos = mPos.get(belt);

		// gravitate to belt center to avoid item near edge.

		final Bounds beltBounds = mBounds.get(belt);
		final Bounds itemBounds = mBounds.get(item);

		vec2.set(beltPos.x + beltBounds.cx(), beltPos.y + beltBounds.cy())
			.sub(itemPos.x + itemBounds.cx(), itemPos.y + itemBounds.cy());

		// only correct towards the direction the belt doesn't push.
		vec2.nor().scl(CORRECTION_SPEED).add(vec);
*/
		final Physics physics = mPhysics.get(item);
		physics.vx = vec.x;
		physics.vy = vec.y;
	}

	private float getAngle(Entity belt) {
		return mAngle.getSafe(belt, notAngled).rotation + mConveyer.get(belt).direction;
	}
}
