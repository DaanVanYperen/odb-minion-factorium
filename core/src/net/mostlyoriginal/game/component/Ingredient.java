package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Ingredient extends Component {
	public Type type;

	public Ingredient(Type type) {
		this.type = type;
	}

	public enum Type {
		CHICK
	}
	public int count=1;
}
