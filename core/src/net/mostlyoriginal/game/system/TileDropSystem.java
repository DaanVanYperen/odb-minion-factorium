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
	protected void process(Entity e) {
		if ( isWithinGrid() )
		{
			moveToLocation(e);

			if ( !leftButtonDown && canDropHere(e) ) {
				mDragging.remove(e);
			}
		}

	}

	private void moveToLocation(Entity e) {
		final Pos pos = mPos.get(e);
		pos.x = gridX * G.TILE_SIZE;
		pos.y = gridY * G.TILE_SIZE + G.FOOTER_H;
	}

	private boolean isWithinGrid() {
		return gridX >= 0 && gridY >= 0 && gridX < G.TILES_W && gridY < G.TILES_H;
	}

	private boolean canDropHere(Entity e) {
		return true;
	}
}
