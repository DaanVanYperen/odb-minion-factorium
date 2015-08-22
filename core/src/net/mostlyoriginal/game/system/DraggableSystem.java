package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.*;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.Draggable;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class DraggableSystem extends EntityProcessingSystem {

	protected M<Draggable> mDraggable;
	protected M<Pos> mPos;

	public DraggableSystem() {
		super(Aspect.all(Draggable.class));
	}

	@Override
	protected void process(Entity e) {
		updateIndicatorPos(e);
	}

	private void updateIndicatorPos(Entity e) {
		final Draggable draggable = mDraggable.get(e);
		final Pos indicatorPos = mPos.get(draggable.indicator);
		final Pos topicPos = mPos.get(e);
		indicatorPos.x = topicPos.x;
		indicatorPos.y = topicPos.y;
	}

	@Override
	protected void inserted(int entityId) {
		final Draggable draggable = mDraggable.get(entityId);
		draggable.indicator = new EntityBuilder(world)
				.with(
						new Anim("draggable-indicator"),
						new Renderable(GameScreenSetupSystem.LAYER_OVERLAYS),
						new Color(),
						newColorAnimation(new Color("3e233600"), new Color(1f,1f,1f,1f),1f),
						new Pos()).build();
	}

	private ColorAnimation newColorAnimation(Color colorA, Color colorB, float speed) {
		return new ColorAnimation(colorA, colorB, new InterpolationStrategy() {
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
