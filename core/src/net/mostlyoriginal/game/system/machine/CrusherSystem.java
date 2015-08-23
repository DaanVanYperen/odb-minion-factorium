package net.mostlyoriginal.game.system.machine;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;
import sun.rmi.server.InactiveGroupException;

/**
 * @author Daan van Yperen
 */
@Wire
public class CrusherSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	M<Ingredient> mIngredient;
	private GameScreenSetupSystem setupSystem;
	private M<Pos> mPos;
	protected M<Anim> mAnim;
	protected M<Angle> mAngle;
	private AbstractAssetSystem abstractAssetSystem;
	protected M<SpawnProtected> mSpawnProtected;
	protected M<Wet> mWet;

	public CrusherSystem() {

		super(Aspect.all(Crusher.class, Pos.class, Bounds.class),
				Aspect.all(Ingredient.class, Pos.class, Bounds.class).exclude(SpawnProtected.class));
	}

	@Override
	protected void process(Entity factory, Entity ingredient) {
		if (crusherActive(factory) && ingredient.isActive() && collisionSystem.overlaps(factory, ingredient)) {
			act(factory, ingredient);
		}
	}

	private boolean crusherActive(Entity crusher) {
		final Anim anim = mAnim.get(crusher);
		final Animation animation = abstractAssetSystem.get(anim.id);
		animation.setPlayMode(Animation.PlayMode.LOOP);
		return animation.getKeyFrameIndex(anim.age) == 1;
	}


	private void act(Entity crusher, Entity ingredient) {

		abstractAssetSystem.playSfx("stamper");
		switch (mIngredient.get(ingredient).type) {
			case GOOGLIE_EYE:
				return;
			case BEAD_EYE: {
				replace(crusher, ingredient, Ingredient.Type.GOOGLIE_EYE);
				break;
			}
			case MINION_GOOGLED: {
				replace(crusher, ingredient, Ingredient.Type.MINION_ENLARGED);
				break;
			}
			default:
				ingredient = replace(crusher, ingredient, Ingredient.Type.BLOOD);
				//ingredient.deleteFromWorld();
				break;
		}

	}

	private Entity replace(Entity crusher, Entity ingredient, Ingredient.Type type) {
		final Pos sourcePos = mPos.get(ingredient);
		ingredient.deleteFromWorld();
		final Pos pos = mPos.get(crusher);
		ingredient = setupSystem.createIngredient(pos.x + 2 + G.TILE_SIZE / 2, pos.y + 2 + G.TILE_SIZE / 2, type);
		final Pos newIngredient = mPos.get(ingredient);
		newIngredient.x = sourcePos.x;
		newIngredient.y = sourcePos.y;


		if ( type == Ingredient.Type.BLOOD) {
			mSpawnProtected.create(ingredient);
			mAngle.create(ingredient).rotation = MathUtils.random(0,360f);
			mWet.create(ingredient).liquid = ShowerLiquid.BLOOD;
			abstractAssetSystem.playSfx("chick-squeek");
		} else {
			abstractAssetSystem.playSfx("flatten-eye");
		}
		return ingredient;
	}
}
