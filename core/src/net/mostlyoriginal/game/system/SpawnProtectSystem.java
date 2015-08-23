package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.SpawnProtected;

/**
 * @author Daan van Yperen
 */
@Wire
public class SpawnProtectSystem extends EntityProcessingSystem {

	protected M<SpawnProtected> mSpawnProtected;

	public SpawnProtectSystem() {
		super(Aspect.all(SpawnProtected.class));
	}

	@Override
	protected void process(Entity e) {
		final SpawnProtected spawnProtected = mSpawnProtected.get(e);
		spawnProtected.cooldown -= world.delta;
		if ( spawnProtected.cooldown <= 0 )
		{
			mSpawnProtected.remove(e);
		}

	}
}
