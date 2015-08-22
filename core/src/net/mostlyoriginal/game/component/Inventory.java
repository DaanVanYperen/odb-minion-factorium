package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Inventory extends Component {
	public int items[] = new int[Ingredient.Type.values().length];
}
