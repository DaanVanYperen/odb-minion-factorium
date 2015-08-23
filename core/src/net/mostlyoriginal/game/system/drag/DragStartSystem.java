package net.mostlyoriginal.game.system.drag;

import com.artemis.Aspect;
import com.artemis.AspectSubscriptionManager;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Color;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.DualEntityProcessingSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.Draggable;
import net.mostlyoriginal.game.component.Dragging;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class DragStartSystem extends DualEntityProcessingSystem {

	CollisionSystem collisionSystem;

	protected M<Anim> mAnim;
	protected M<Pos> mPos;
	protected M<Angle> mAngle;
	protected M<Bounds> mBounds;

	private boolean leftButtonDown;
	private EntitySubscription currentlyDragged;
	private Angle NO_ANGLE;
	private AbstractAssetSystem abstractAssetSystem;

	public DragStartSystem() {
		super(Aspect.all(MouseCursor.class, Pos.class),
				Aspect.all(Draggable.class, Pos.class, Bounds.class, Anim.class));
	}

	@Override
	protected void initialize() {
		super.initialize();
		currentlyDragged = world.getManager(AspectSubscriptionManager.class).get(Aspect.all(Dragging.class));
	}

	@Override
	protected boolean checkProcessing() {
		return !isUserDragging();
	}

	public boolean isUserDragging() {
		return currentlyDragged.getEntities().size() > 0;
	}

	@Override
	protected void begin() {
		super.begin();


		// prevent multiple triggers of tapped
		leftButtonDown = Gdx.input.isButtonPressed(0);
	}

	@Override
	protected void process(Entity mouse, Entity draggable) {
		if (leftButtonDown && collisionSystem.overlaps(mouse, draggable)) {
			createDraggingIndicator(draggable);
			abstractAssetSystem.playSfx("drag");
		}
	}

	private void createDraggingIndicator(Entity draggable) {
		final Anim sourceAnim = mAnim.get(draggable);
		final Pos sourcePos = mPos.get(draggable);
		final Bounds sourceBounds = mBounds.get(draggable);

		final Anim anim = new Anim();
		anim.id = sourceAnim.id;
		anim.speed = sourceAnim.speed;
		anim.age = sourceAnim.age;

		final Pos pos = new Pos();
		pos.x = sourcePos.x;
		pos.y = sourcePos.y;


		NO_ANGLE = new Angle(0);
		new EntityBuilder(world)
				.with(new Dragging(draggable), new Renderable(GameScreenSetupSystem.LAYER_DRAGGING),
						new Bounds(sourceBounds.minx, sourceBounds.miny, sourceBounds.maxx, sourceBounds.maxy),
						anim, pos, new Angle(mAngle.getSafe(draggable, NO_ANGLE).rotation), new Color(1f, 1f, 1f, 0.5f)
				).build();
	}
}
