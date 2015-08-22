package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Inventory extends Component {
	public int items[] = new int[Ingredient.Type.values().length];

	public boolean has(Ingredient.Type type, int count) {
		return ( items[type.ordinal()] >= count);
	}

	public Inventory dec(Ingredient.Type type, int count) {
		final int index = type.ordinal();
		items[index] -= count;
		if ( items[index] < 0 ) items[index] = 0;
		return this;
	}

	public Inventory inc(Ingredient.Type type, int count) {
		final int index = type.ordinal();
		items[index] += count;
		return this;
	}
}
