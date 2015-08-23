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
		/*  0     1        2          3           4           5 */
		BUNNY, CHICK, CHICKBUNNY, BEAD_EYE, GOOGLIE_EYE, BLIND_CHICK
	}

	public int count = 1;
}
