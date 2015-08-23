package net.mostlyoriginal.game.component;

import com.artemis.Component;

import java.io.Serializable;

/**
 * @author Daan van Yperen
 */
public class Level extends Component {
	public int height;
	public int width;
	public String[] structure;
	public Inventory goals = new Inventory();
}
