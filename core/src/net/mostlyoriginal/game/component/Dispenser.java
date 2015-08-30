package net.mostlyoriginal.game.component;

import com.artemis.Component;
import net.mostlyoriginal.game.component.Ingredient;

/**
 * @author Daan van Yperen
 */
public class Dispenser extends Component {
	public Ingredient.Type type;

	public Dispenser() {}
	public Dispenser(Ingredient.Type type) {
		this.type = type;
	}
}
