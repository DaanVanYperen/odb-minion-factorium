package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * Pickup protect so factories don't scoop their own produce.
 *
 * @author Daan van Yperen
 */
public class SpawnProtected extends Component {
	public float cooldown = 2f;
}
