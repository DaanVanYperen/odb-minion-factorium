package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Sprinkle extends Component {
	public float cooldown = 0;
	public float duration = 0.01f;
	public ShowerLiquid liquid = ShowerLiquid.WATER;
}
