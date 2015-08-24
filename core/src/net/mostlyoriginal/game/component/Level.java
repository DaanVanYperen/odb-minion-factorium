package net.mostlyoriginal.game.component;

import com.artemis.Component;

import java.io.Serializable;

/**
 * @author Daan van Yperen
 */
public class Level extends Component {
	public String name;
	public int height;
	public int width;
	public boolean tutorial = false;
	public String[] structure;
	public Inventory goals = new Inventory();
	public Inventory input = new Inventory();
}
