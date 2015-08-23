package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class SplicerSystem extends IntervalEntityProcessingSystem  {


	protected GameScreenSetupSystem setupSystem;
	protected M<Inventory> mInventory;
	protected M<Pos> mPos;
	protected M<Sprinkle> mSprinkle;
	private AbstractAssetSystem gameScreenAssetSystem;


	public SplicerSystem() {
		super(Aspect.all(Splicer.class, Inventory.class, Pos.class), 1f);
	}

	@Override
	protected void process(Entity e) {


		final Inventory inventory = mInventory.get(e);

		if ( inventory.has(Ingredient.Type.CHICK, 1) && inventory.has(Ingredient.Type.BUNNY, 1) )
		{
			playSfx(e);
			final Pos pos = mPos.get(e);

			inventory.dec(Ingredient.Type.CHICK, 1);
			inventory.dec(Ingredient.Type.BUNNY, 1);

			setupSystem.createIngredient(pos.x + 2 + G.TILE_SIZE / 2, pos.y + 2 + G.TILE_SIZE / 2, Ingredient.Type.CHICKBUNNY);
		}

		if ( inventory.has(Ingredient.Type.MINION_PAINTED, 1) && inventory.has(Ingredient.Type.GOOGLIE_EYE, 1) )
		{
			playSfx(e);
			final Pos pos = mPos.get(e);
			inventory.dec(Ingredient.Type.MINION_PAINTED, 1);
			inventory.dec(Ingredient.Type.GOOGLIE_EYE, 1);

			setupSystem.createIngredient(pos.x + 2 + G.TILE_SIZE / 2, pos.y + 2 + G.TILE_SIZE / 2, Ingredient.Type.MINION_GOOGLED);
		}
	}

	private void playSfx(Entity e) {
		final Sprinkle sprinkle = mSprinkle.create(e);
		sprinkle.liquid = ShowerLiquid.STEAM;
		sprinkle.duration = 0.3f;
		gameScreenAssetSystem.playSfx(MathUtils.randomBoolean() ? "factory-1" : "factory-2");
		gameScreenAssetSystem.playSfx("hybrid-emerges");
	}
}
