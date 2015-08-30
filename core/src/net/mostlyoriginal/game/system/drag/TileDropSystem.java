package net.mostlyoriginal.game.system.drag;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Color;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Dragging;
import net.mostlyoriginal.game.component.ShowerLiquid;
import net.mostlyoriginal.game.component.Sprinkle;
import net.mostlyoriginal.game.component.Wet;
import net.mostlyoriginal.game.system.tap.TapSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class TileDropSystem extends EntityProcessingSystem {

	private boolean leftButtonDown;

	protected M<Dragging> mDragging;
	protected M<Pos> mPos;
	protected M<Color> mColor;
	protected M<Sprinkle> mSprinkle;
	private GridOverlapHelperSystem gridOverlapHelperSystem;
	private int gridX;
	private int gridY;

	TapSystem tapSystem;
	private boolean isDragging;
	private AbstractAssetSystem abstractAssetSystem;

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
			if (!leftButtonDown)
				isDragging = false;
		}
	}

	@Override
	protected void process(Entity e) {

		if ( isWithinGrid() )
		{
			moveToDragLocation(e);
			tintIndicator(e);

			if ( !leftButtonDown ) {
				if ( canDropHere(e) ) {
					abstractAssetSystem.playSfx("drop");
					actuallyMoveSubject(e);
				}
				e.deleteFromWorld();
			}
		}
	}

	private void tintIndicator(Entity e) {
		final Color color = mColor.create(e);
		if ( canDropHere(e) )
		{
			color.set(1f,1f,1f,0.7f);
		} else {
			color.set(1f,0f,0f,0.7f);
		}
	}

	private void actuallyMoveSubject(Entity e) {
		final Entity subject = world.getEntity(mDragging.get(e).entityId);
		if ( subject != null )
		{
			moveToDragLocation(subject);

			final Sprinkle sprinkle = mSprinkle.create(subject);
			sprinkle.liquid = ShowerLiquid.DUST;
			sprinkle.duration = 0.25f;

		}
	}

	private void moveToDragLocation(Entity e) {
		final Pos pos = mPos.get(e);
		final int x = gridX * G.TILE_SIZE;
		final int y = gridY * G.TILE_SIZE + G.FOOTER_H;

		if ( pos.xy.x != x || pos.xy.y != y ) {
			isDragging = true;
		}

		pos.xy.x = x;
		pos.xy.y = y;

	}

	private boolean isWithinGrid() {
		return gridX >= 0 && gridY >= 0 && gridX < G.TILES_W && gridY < G.TILES_H;
	}

	private boolean canDropHere(Entity e) {
		return !gridOverlapHelperSystem.overlaps(e);
	}
}
