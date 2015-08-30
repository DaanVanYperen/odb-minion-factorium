package net.mostlyoriginal.game.system.drag;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.Draggable;
import net.mostlyoriginal.game.component.common.JamBuilder;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * Manages and updates draggable indicators.
 *
 * @author Daan van Yperen
 */
@Wire
public class DraggableIndicatorSystem extends EntityProcessingSystem {

	protected M<Draggable> mDraggable;
	protected M<Pos> mPos;
	protected M<Invisible> mInvisible;
	
	protected DragStartSystem dragStartSystem;
	
	private JamBuilder builder = new JamBuilder();

	public DraggableIndicatorSystem() {
		super(Aspect.all(Draggable.class));
	}

	@Override
	protected void process(Entity e) {
		updateIndicatorPos(e);
	}

	private void updateIndicatorPos(Entity e) {
		final Draggable draggable = mDraggable.get(e);
		final Entity indicator = draggable.indicator;

		mPos.mirror(indicator, e);

		if (shouldShowIndicators())
		{
			mInvisible.remove(indicator);
		} else {
			mInvisible.create(indicator);
		}
	}

	private boolean shouldShowIndicators() {
		return !dragStartSystem.isUserDragging();
	}

	@Override
	protected void inserted(int entityId) {
		final Draggable draggable = mDraggable.get(entityId);
		draggable.indicator =
				builder.create(world).Anim("draggable-indicator")
						.Renderable(GameScreenSetupSystem.LAYER_OVERLAYS)
						.Tint("000000").Pos(0,0).build();

		draggable.indicator.edit().add(newTintAnimation(new Tint("3e233600"), new Tint(1f, 1f, 1f, 1f), 1f));
	}

	private ColorAnimation newTintAnimation(Tint TintA, Tint TintB, float speed) {
		return new ColorAnimation(TintA, TintB, new InterpolationStrategy() {
			@Override
			public float apply(float v1, float v2, float a) {
				return Interpolation.exp5Out.apply(v1, v2, a);
			}
		}, speed, -1);
	}

	@Override
	protected void removed(int entityId) {
		final Draggable draggable = mDraggable.get(entityId);
		draggable.indicator.deleteFromWorld();
	}
}
