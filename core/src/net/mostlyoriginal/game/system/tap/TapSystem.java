package net.mostlyoriginal.game.system.tap;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.Tappable;
import net.mostlyoriginal.game.component.Tapped;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * A 'tap' is an in place click, not drag.
 *
 * Adds {@see Tapped}. Consuming systems are responsible for removing.
 *
 * @author Daan van Yperen
 */
@Wire
public class TapSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	protected M<Tapped> mTapped;
	private boolean beenDown;
	private boolean leftButtonClicked;

	GameScreenAssetSystem gameScreenAssetSystem;

	public TapSystem() {
		super(Aspect.all(MouseCursor.class, Pos.class),
				Aspect.all(Tappable.class, Pos.class, Bounds.class));
	}

	@Override
	protected void begin() {
		super.begin();

		// prevent multiple triggers of tapped
		boolean leftButtonDown = Gdx.input.isButtonPressed(0);
		if (leftButtonDown) {
			beenDown = true;
		} else {
			if (beenDown) {
				leftButtonClicked = true;
				beenDown = false;
			} else {
				abortPendingTaps();
			}
		}
	}

	public void abortPendingTaps() {
		leftButtonClicked = false;
		beenDown = false;
	}

	@Override
	protected void process(Entity mouse, Entity tappable) {
		if (leftButtonClicked && collisionSystem.overlaps(mouse, tappable)) {
			mTapped.create(tappable);
			gameScreenAssetSystem.playSfx(MathUtils.randomBoolean() ? "button-play" : "button-rewind");
		}
	}
}
