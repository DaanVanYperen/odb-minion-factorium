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

	public void emptyInto(Inventory target) {
		for (int i = 0, s= Ingredient.Type.values().length; i < s; i++) {
			target.items[i] += items[i];
			items[i] = 0;
		}
	}

	public boolean containsAtLeast(Inventory goals) {
		for (int i = 0, s= Ingredient.Type.values().length; i < s; i++) {
			if (items[i] < goals.items[i]) return false;
		}
		return true;
	}
}
