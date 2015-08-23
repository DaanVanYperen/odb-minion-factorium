package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Dragging;

/**
 * @author Daan van Yperen
 */
@Wire
public class TileDropSystem extends EntityProcessingSystem {

	private boolean leftButtonDown;

	protected M<Dragging> mDragging;
	protected M<Pos> mPos;
	private int gridX;
	private int gridY;

	TapSystem tapSystem;
	private boolean isDragging;

	public TileDropSystem() {
		super(Aspect.all(Dragging.class, Pos.class));
	}

	@Override
	protected void begin() {
		leftButtonDown = Gdx.input.isButtonPressed(0);

		// convert to grid coords.
		gridX = (Gdx.input.getX() / G.ZOOM) / G.TILE_SIZE;
		gridY = (((Gdx.graphics.getHeight() - Gdx.input.getY()) / G.ZOOM) - G.FOOTER_H) / G.TILE_SIZE;
	}

	@Override
	protected void end() {
		super.end();
		if (isDragging)
		{
			tapSystem.abortPendingTaps();
		}
	}

	@Override
	protected void process(Entity e) {

		if ( isWithinGrid() )
		{
			moveToDragLocation(e);

			if ( !leftButtonDown ) {
				if ( canDropHere(e) ) {
					actuallyMoveSubject(e);
				}
				e.deleteFromWorld();
			}
		}
	}

	private void actuallyMoveSubject(Entity e) {
		final Entity subject = world.getEntity(mDragging.get(e).entityId);
		if ( subject != null )
		{
			moveToDragLocation(subject);
		}
	}

	private void moveToDragLocation(Entity e) {
		final Pos pos = mPos.get(e);
		final int x = gridX * G.TILE_SIZE;
		final int y = gridY * G.TILE_SIZE + G.FOOTER_H;

		if ( pos.x != x || pos.y != y ) {
			isDragging = true;
		}

		pos.x = x;
		pos.y = y;

	}

	private boolean isWithinGrid() {
		return gridX >= 0 && gridY >= 0 && gridX < G.TILES_W && gridY < G.TILES_H;
	}

	private boolean canDropHere(Entity e) {
		return true;
	}
}
