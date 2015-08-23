package net.mostlyoriginal.game.screen;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.ExtendedComponentMapperPlugin;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.ColorAnimationSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.mouse.MouseCursorSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.api.system.physics.PhysicsSystem;
import net.mostlyoriginal.api.system.render.AnimRenderSystem;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import net.mostlyoriginal.api.utils.builder.WorldConfigurationBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.system.*;
import net.mostlyoriginal.game.system.drag.DragStartSystem;
import net.mostlyoriginal.game.system.drag.DraggableIndicatorSystem;
import net.mostlyoriginal.game.system.drag.TileDropSystem;
import net.mostlyoriginal.game.system.tap.RotateSystem;
import net.mostlyoriginal.game.system.tap.TapSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class LevelScreen extends WorldScreen {

	public static final String BACKGROUND_COLOR_HEX = "3e2336";
	private int levelIndex;

	public LevelScreen(int levelIndex) {
		this.levelIndex = levelIndex;
	}

	@Override
	protected World createWorld() {
	return new World(new WorldConfigurationBuilder()
			    .dependsOn(ExtendedComponentMapperPlugin.class)
				.with(
						// Replace with your own systems!
						instanceDancingManSystems()
				).build());
	}

	/** Just get a basic dancing man going! */
	private BaseSystem[] instanceDancingManSystems() {
		RenderBatchingSystem renderBatchingSystem;
		levelIndex = 1;
		return new BaseSystem[]{

				new CameraSystem(G.ZOOM),

				new ClearScreenSystem(Color.valueOf(BACKGROUND_COLOR_HEX)),
				new GameScreenAssetSystem(),
				new GameScreenSetupSystem(levelIndex),

				new MouseCursorSystem(),
				new CollisionSystem(),
				new ConveyerSystem(),
				new PhysicsSystem(),

			    new InventoryScoopSystem(),
				new SplicerSystem(),
				new SpawnProtectSystem(),
				new DispenserSystem(),

				new TapSystem(),
				new DraggableIndicatorSystem(),
				new DragStartSystem(),
				new TileDropSystem(),
				new RotateSystem(),
				new GridOverlapHelperSystem(),

				new ColorAnimationSystem(),

				renderBatchingSystem = new RenderBatchingSystem(),
				new AnimRenderSystem(renderBatchingSystem),
		};
	}
}
