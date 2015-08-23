package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.Draggable;
import net.mostlyoriginal.game.component.Dragging;
import net.mostlyoriginal.game.component.Tappable;
import net.mostlyoriginal.game.component.Tapped;

/**
 * @author Daan van Yperen
 */
@Wire
public class DragStartSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	protected M<Dragging> mDragging;
	private boolean leftButtonDown;

	public DragStartSystem() {
		super(Aspect.all(MouseCursor.class, Pos.class),
				Aspect.all(Draggable.class, Pos.class, Bounds.class));
	}

	@Override
	protected void begin() {
		super.begin();

		// prevent multiple triggers of tapped
		leftButtonDown = Gdx.input.isButtonPressed(0);
	}

	@Override
	protected void process(Entity mouse, Entity draggable) {
		if (leftButtonDown && collisionSystem.overlaps(mouse, draggable)) {
			mDragging.create(draggable);
		}
	}
}
