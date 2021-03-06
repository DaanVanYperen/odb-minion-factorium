package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class GougerSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	GameScreenSetupSystem setupSystem;

	protected M<Anim> mAnim;
	protected M<Pos> mPos;
	protected M<Physics> mPhysics;
	protected M<Ingredient> mIngredient;
	protected M<Sprinkle> mSprinkle;
	protected AbstractAssetSystem abstractAssetSystem;
	private GameScreenAssetSystem gameScreenAssetSystem;

	public GougerSystem() {

		super(Aspect.all(Gouger.class, Pos.class, Bounds.class),
				Aspect.all(Ingredient.class, Pos.class, Bounds.class).exclude(SpawnProtected.class));
	}

	@Override
	protected void process(Entity gouger, Entity ingredient) {
		if (ingredient.isActive() && collisionSystem.overlaps(gouger, ingredient)) {
			act(gouger, ingredient);
			final Sprinkle sprinkle = mSprinkle.create(gouger);
			sprinkle.liquid = ShowerLiquid.STEAM;
			sprinkle.duration = 0.3f;
		}
	}

	private void act(Entity gouger, Entity ingredient) {
		ingredient.deleteFromWorld();

		switch (mIngredient.get(ingredient).type) {
			case CHICK:
				playSfx();
				final Pos pos = mPos.get(gouger);
				setupSystem.createIngredientEject(pos.x + 2 + G.TILE_SIZE / 2, pos.y + 2 + G.TILE_SIZE / 2, Ingredient.Type.BEAD_EYE, 0f);
				setupSystem.createIngredientEject(pos.x + 2 + G.TILE_SIZE / 2, pos.y + 2 + G.TILE_SIZE / 2, Ingredient.Type.BLIND_CHICK, -90f);
				break;
		}
	}

	private void playSfx() {
		gameScreenAssetSystem.playSfx(MathUtils.randomBoolean() ? "factory-1" : "factory-2");
	}
}
