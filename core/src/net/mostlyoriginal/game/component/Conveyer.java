package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Conveyer extends Component {

	// direction of conveyance.
	public float direction;

	public Conveyer(float direction) {
		this.direction = direction;
	}
}
