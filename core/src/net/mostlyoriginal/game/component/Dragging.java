package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.EntityId;

/**
 * @author Daan van Yperen
 */
public class Dragging extends Component {

	// entity being dragged.
	@EntityId
	public int entityId;

	public Dragging(Entity subject) {
		this.entityId = subject.getId();
	}
}
