package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.EntityBuilder;
import com.artemis.utils.reflect.ClassReflection;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.logic.Transition;

/**
 * Switch to next level.
 *
 * @author Daan van Yperen
 */
@Wire
public class LevelTransitionSystem extends EntityProcessingSystem {

	protected ComponentMapper<Transition> mTransition;

	public LevelTransitionSystem() {
		super(Aspect.all(Transition.class));
	}

	/** Transition to screen after delay in seconds. */
	public void transition(float delay) {
		new EntityBuilder(world).with(new Schedule().wait(delay).add(new Transition()));
	}

	@Override
	protected void process(Entity e) {
		try {
			GdxArtemisGame.getInstance().nextLevel();
		} catch (Exception ex ) {
			throw new RuntimeException(ex);
		}
	}
}
