package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
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
	CollisionSystem collisionSystem;

	public M<Pos> mPos;
	public M<Angle> mAngle;
	public M<Conveyer> mConveyer;

	public ConveyerSystem() {
		super(Aspect.all(Conveyer.class, Bounds.class, Pos.class),
				Aspect.all(Conveyable.class, Bounds.class, Pos.class));
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

	private void convey(Entity belt, Entity conveyable) {
		vec.set(CONVEY_SPEED,0).setAngle(getAngle(belt));
		final Pos pos = mPos.get(conveyable);
		pos.x += vec.x * world.delta;
		pos.y += vec.y * world.delta;
	}

	private float getAngle(Entity belt) {
		return mAngle.getSafe(belt, notAngled).rotation + mConveyer.get(belt).direction;
	}
}
