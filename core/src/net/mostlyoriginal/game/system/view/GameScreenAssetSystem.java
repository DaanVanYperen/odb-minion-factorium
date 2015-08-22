package net.mostlyoriginal.game.system.view;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenAssetSystem extends AbstractAssetSystem {

	public GameScreenAssetSystem() {
		super("tileset.png");
	}

	@Override
	protected void initialize() {
		super.initialize();

		add("cell-empty", 20, 260, 20, 20, 1);
		add("cell-empty2", 40, 260, 20, 20, 1);

		add("belt-straight", 20, 200, 20, 20, 4).setFrameDuration(1 / 15f);
		add("belt-bend", 20, 220, 20, 20, 2).setFrameDuration(1 / 15f);
		add("belt-bend-inverse", 20, 240, 20, 20, 2).setFrameDuration(1 / 15f);

		add("ingredient-CHICK", 160, 20, 6, 6, 1);
		add("ingredient-BUNNY", 160, 32, 6, 8, 1);
		add("ingredient-CHICKBUNNY", 200, 31, 6, 9, 1);

		add("factory-splicer", 20, 79, 25, 26, 2);
	}
}
