package net.mostlyoriginal.game.system.view;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.script.Schedule;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.component.common.JamBuilder;
import net.mostlyoriginal.game.component.detection.OdbFeatureComponent;
import net.mostlyoriginal.game.screen.LevelScreen;
import net.mostlyoriginal.game.system.detection.OdbFeatureDetectionSystem;
import net.mostlyoriginal.game.system.flow.TransitionSystem;
import net.mostlyoriginal.game.util.Anims;

/**
 * @author Daan van Yperen
 */
@Wire
public class FeatureScreenSetupSystem extends PassiveSystem {

	public static final int FEATURE_BORDER_MARGIN = 1;
	public static final Tint Tint_FEATURE_FADED = new Tint(0.8f, 1.0f, 1.0f, 0.3f);
	public static final Tint Tint_FEATURE_OFF = new Tint(0.8f, 1.0f, 1.0f, 0.0f);
	public static final Tint Tint_FEATURE_ON_OFF_Tint = new Tint(0.8f, 1.0f, 1.0f, 1.0f);
	public static final Tint Tint_FEATURE_ON = new Tint(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Tint Tint_LOGO_FADED = new Tint(1.0f, 1.0f, 1.0f, 0.0f);
	public static final Tint Tint_LOGO_FULL = new Tint(1.0f, 1.0f, 1.0f, 1.0f);
	FeatureScreenAssetSystem assetSystem;
	TagManager tagManager;

	private int iconIndex;
	private JamBuilder builder = new JamBuilder();


	@Override
	protected void initialize() {
		super.initialize();

		addBackground();
		addLogo();

		final Entity featureEntity = tagManager.getEntity(OdbFeatureDetectionSystem.FEATURES_TAG);
		final OdbFeatureComponent featureComponent = featureEntity.getComponent(OdbFeatureComponent.class);

		addFeatureIcon(featureComponent.isHotspotOptimization, "feature-hotspot");
		addFeatureIcon(featureComponent.isPacked, "feature-packed");
		addFeatureIcon(featureComponent.isPooled, "feature-pooled");
		addFeatureIcon(featureComponent.isFactory, "feature-factory");

		scheduleTransitionToGameScreen();
	}

	private void addBackground() {

		// scale to fit.
		final float widthScale = Gdx.graphics.getWidth() / FeatureScreenAssetSystem.FEATURE_WIDTH;
		final float heightScale = Gdx.graphics.getHeight() / FeatureScreenAssetSystem.FEATURE_HEIGHT;

		Anims.createAnimAt(world,
				0,
				0,
				"background",
				Math.max(heightScale, widthScale));
	}

	private void addFeatureIcon(boolean state, String iconId) {

		final float scale = Anims.scaleToScreenRounded(0.08f, FeatureScreenAssetSystem.FEATURE_WIDTH);
		final float iconBorderMargin = scale * FEATURE_BORDER_MARGIN;
		final float iconOffset = ((scale * FeatureScreenAssetSystem.FEATURE_WIDTH) + iconBorderMargin);
		final Entity entity = Anims.createAnimAt(world,
				(int) (Gdx.graphics.getWidth() - iconOffset * ++iconIndex),
				(int) iconBorderMargin,
				iconId,
				scale);

		if (state) {
			builder.edit(entity)
					.Tint(Tint_FEATURE_OFF);

			entity.edit()
					.add(new Schedule()
							.wait(0.5f + iconIndex * 0.1f)
							.tween(Tint_FEATURE_OFF, Tint_FEATURE_ON_OFF_Tint, 0.5f)
							.wait(0.5f)
							.tween(Tint_FEATURE_ON_OFF_Tint, Tint_FEATURE_ON, 0.25f)
							.wait(0.25f));
		} else {
			builder.edit(entity)
					.Tint(Tint_FEATURE_OFF);

			entity.edit()
					.add(new Schedule()
							.wait(0.5f + iconIndex * 0.1f)
							.tween(Tint_FEATURE_OFF, Tint_FEATURE_FADED, 0.5f));
		}
	}

	public void addLogo() {

		// approximate percentage of screen size with logo. Use rounded numbers to keep the logo crisp.

		final Entity entity = Anims.createCenteredAt(world,
				FeatureScreenAssetSystem.LOGO_WIDTH,
				FeatureScreenAssetSystem.LOGO_HEIGHT,
				"logo",
				Anims.scaleToScreenRounded(0.8f, FeatureScreenAssetSystem.LOGO_WIDTH));

		builder.edit(entity).Tint(Tint_LOGO_FADED);

		entity.edit()
				.add(new Schedule()
						.tween(Tint_LOGO_FADED, Tint_LOGO_FULL, 0.5f, Interpolation.fade)
						.wait(0.5f));
	}

	public static final int DISPLAY_SECONDS = 2;

	private void scheduleTransitionToGameScreen() {
		world.getSystem(TransitionSystem.class).transition(LevelScreen.class, DISPLAY_SECONDS);
	}

}
