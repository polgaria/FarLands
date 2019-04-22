package site.geni.FarLands.gui;

import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Pair;
import site.geni.FarLands.gui.entries.EstimateEntry;
import site.geni.FarLands.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CustomizeFarLandsScreen {
	private static final String INVALID = TextFormat.RED + I18n.translate("config.farlands.invalid");

	private static final String[] PARTICLES = new String[]{
		"water", "lava", "tnt", "end_portal", "falling_block", "mycelium", "leaves", "repeater", "nether_portal"
	};

	private static final String[] ENTITIES = new String[]{
		"tnt", "enchanting_table"
	};

	private static int farLandsLocation = (int) (Integer.MAX_VALUE / ((Config.getConfig().coordinateScale * Config.getConfig().coordinateScaleMultiplier) / 4));
	private static long fartherLandsLocation = farLandsLocation * 80;

	private static long farthererLandsLocation = (long) (Long.MAX_VALUE / ((Config.getConfig().coordinateScale * Config.getConfig().coordinateScaleMultiplier) / 4));
	private static long farthestLandsLocation = farthererLandsLocation * 80;

	public static void openAndCreateConfigScreen(Screen parent) {
		MinecraftClient.getInstance().openScreen(createConfigScreen(parent));
	}

	public static Screen createConfigScreen(Screen parent) {
		ConfigScreenBuilder builder = ConfigScreenBuilder.create(parent, I18n.translate("config.farlands.title"), null);

		ConfigScreenBuilder.CategoryBuilder general = builder.addCategory("config.farlands.category.general");
		ConfigScreenBuilder.CategoryBuilder fixes = builder.addCategory("config.farlands.category.fixes");
		ConfigScreenBuilder.CategoryBuilder world = builder.addCategory("config.farlands.category.world");


		// Adds the option for enabling the Far Lands in the "General" category
		general.addOption(new BooleanListEntry(
			"config.farlands.farLandsEnabled",
			Config.getConfig().farLandsEnabled,
			"text.cloth.reset_value",
			() -> true,
			bool -> Config.getConfig().farLandsEnabled = bool
		));

		// Adds the option for killing entities in the Far Lands in the "General" category
		general.addOption(new BooleanListEntry(
			"config.farlands.killEntities",
			Config.getConfig().killFallingBlockEntitiesInFarLands,
			"text.cloth.reset_value",
			() -> false,
			bool -> Config.getConfig().killFallingBlockEntitiesInFarLands = bool
		));

		// Adds the warning in the "World" category
		world.addOption(new TextListEntry(
			"",
			TextFormat.RED + I18n.translate("config.farlands.category.world.warning")
		));

		// Adds the option for setting the coordinate scale in the "General" category
		world.addOption(new DoubleListEntry(
			"config.farlands.coordinateScale",
			Config.getConfig().coordinateScale,
			"text.cloth.reset_value",
			() -> 684.4119873046875,
			scale -> Config.getConfig().coordinateScale = scale
		) {
			@Override
			public boolean charTyped(char character, int charCode) {
				if (super.charTyped(character, charCode)) {
					try {
						double coordinateScale = this.getObject();

						Config.ConfigSpec config = Config.getConfig();
						config.coordinateScale = coordinateScale;


						CustomizeFarLandsScreen.updateOptions(world, config);
					} catch (ArithmeticException | NumberFormatException ignore) {

					}

					return true;
				}

				return false;
			}

			@Override
			public boolean keyPressed(int charCode, int int_1, int int_2) {
				if (super.keyPressed(charCode, int_1, int_2)) {
					try {
						double coordinateScale = this.getObject();

						Config.ConfigSpec config = Config.getConfig();
						config.coordinateScale = coordinateScale;

						CustomizeFarLandsScreen.updateOptions(world, config);

						return true;
					} catch (ArithmeticException | NumberFormatException ignore) {

					}
				}

				return false;
			}
		});

		// Adds the option for setting the coordinate scale multiplier in the "General" category
		world.addOption(new DoubleListEntry(
			"config.farlands.coordinateScaleMultiplier",
			Config.getConfig().coordinateScaleMultiplier,
			"text.cloth.reset_value",
			() -> 1.0,
			scale -> Config.getConfig().coordinateScaleMultiplier = scale
		) {
			@Override
			public boolean charTyped(char character, int charCode) {
				if (super.charTyped(character, charCode)) {
					try {
						Double coordinateScaleMultiplier = Double.parseDouble(this.getObject() + String.valueOf(character));

						Config.ConfigSpec config = Config.getConfig();
						config.coordinateScaleMultiplier = coordinateScaleMultiplier;

						CustomizeFarLandsScreen.updateOptions(world, config);
					} catch (ArithmeticException | NumberFormatException ignore) {

					}

					return true;
				}

				return false;
			}

			@Override
			public boolean keyPressed(int charCode, int int_1, int int_2) {
				if (super.keyPressed(charCode, int_1, int_2)) {
					try {
						double coordinateScaleMultiplier = this.getObject();

						Config.ConfigSpec config = Config.getConfig();
						config.coordinateScaleMultiplier = coordinateScaleMultiplier;

						CustomizeFarLandsScreen.updateOptions(world, config);
					} catch (ArithmeticException | NumberFormatException ignore) {

					}

					return true;
				}

				return false;
			}
		});

		// Adds the option for setting the height scale in the "General" category
		world.addOption(new DoubleListEntry(
			"config.farlands.heightScale",
			Config.getConfig().heightScale,
			"text.cloth.reset_value",
			() -> 684.4119873046875,
			scale -> Config.getConfig().heightScale = scale
		));

		// Adds the option for setting the height scale multiplier in the "General" category
		world.addOption(new DoubleListEntry(
			"config.farlands.heightScaleMultiplier",
			Config.getConfig().heightScaleMultiplier,
			"text.cloth.reset_value",
			() -> 1.0,
			scale -> Config.getConfig().heightScaleMultiplier = scale
		));

		// "World" category's "Estimates" sub-category's entries
		List<ClothConfigScreen.AbstractListEntry> estimatesEntries = Arrays.asList(
			// Adds the estimate for the Far Lands' location
			new EstimateEntry(
				"config.farlands.estimatedPosition",
				farLandsLocation >= 0 && farLandsLocation != Integer.MAX_VALUE ? "±" + farLandsLocation : INVALID
			),

			// Adds the estimate for the Farther Lands' location
			new EstimateEntry(
				"config.farlands.estimatedFartherPosition",
				fartherLandsLocation >= 0 ? "±" + fartherLandsLocation : INVALID
			),

			// Adds the estimate for the Fartherer Lands' location
			new EstimateEntry(
				"config.farlands.estimatedFarthererPosition",
				farthererLandsLocation >= 0 && farthererLandsLocation != Long.MAX_VALUE ? "±" + farthererLandsLocation : INVALID
			),

			// Adds the estimate for the Farthest Lands' location
			new EstimateEntry(
				"config.farlands.estimatedFarthestPosition",
				farthestLandsLocation > 0 && farthestLandsLocation / 80 == farthererLandsLocation ? "±" + farthestLandsLocation : INVALID
			)
		);

		world.addOption(new SubCategoryListEntry(
			"config.farlands.category.world.subcategory.estimates",
			estimatesEntries,
			true
		));


		// Adds the option for fixing ore generation in the "Fixes" category
		fixes.addOption(new BooleanListEntry(
			"config.farlands.fixOreGeneration",
			Config.getConfig().fixOreGeneration,
			"text.cloth.reset_value",
			() -> true,
			bool -> Config.getConfig().fixOreGeneration = bool,
			() -> Optional.of(new String[]{
				I18n.translate("config.farlands.fixOreGeneration.tooltip")
			})
		));


		List<String> particleTooltip = new ArrayList<>();

		particleTooltip.add(I18n.translate("config.farlands.fixParticles.tooltip.description"));
		for (String particle : PARTICLES) {
			particleTooltip.add(TextFormat.GREEN + I18n.translate("config.farlands.fixParticles.tooltip.description." + particle));
		}

		particleTooltip.add(I18n.translate("config.farlands.fixParticles.tooltip.description.entities"));
		for (String entity : ENTITIES) {
			particleTooltip.add(TextFormat.GREEN + I18n.translate("config.farlands.fixParticles.tooltip.description.entities.") + entity);
		}


		// Adds the option for fixing particles/entities in the "Fixes" category
		fixes.addOption(new BooleanListEntry(
			"config.farlands.fixParticles",
			Config.getConfig().fixParticles,
			"text.cloth.reset_value",
			() -> true,
			bool -> Config.getConfig().fixParticles = bool,
			() -> Optional.of(particleTooltip.toArray(new String[0]))
		));


		// "Fixes" category's "Experimental" sub-category's entries
		List<ClothConfigScreen.AbstractListEntry> experimentalEntries = Arrays.asList(
			// Warning message (16733525 is TextFormat.RED's color)
			new TextListEntry("",
				I18n.translate("config.farlands.category.fixes.subcategory.experimental.warning"),
				16733525
			),
			// Lighting
			new BooleanListEntry(
				"config.farlands.fixLighting",
				Config.getConfig().fixLighting,
				"text.cloth.reset_value",
				() -> false,
				bool -> Config.getConfig().fixLighting = bool,
				() -> Optional.of(new String[]{
					I18n.translate("config.farlands.fixLighting.tooltip.description"),
					TextFormat.RED + I18n.translate("config.farlands.fixLighting.tooltip.warning")
				})
			),
			// Mob spawning
			new BooleanListEntry(
				"config.farlands.fixMobSpawning",
				Config.getConfig().fixMobSpawning,
				"text.cloth.reset_value",
				() -> true,
				bool -> Config.getConfig().fixMobSpawning = bool,
				() -> Optional.of(new String[]{
					I18n.translate("config.farlands.fixMobSpawning.tooltip")
				})
			)
		);

		// Adds the "Experimental" sub-category to the "Fixes" category
		fixes.addOption(new SubCategoryListEntry(
			"config.farlands.category.fixes.subcategory.experimental",
			experimentalEntries,
			false
		));


		builder.setOnSave(savedConfig -> Config.saveConfig());

		return builder.build();
	}

	/**
	 * Updates the estimates in the World category
	 *
	 * @param category {@link me.shedaniel.cloth.api.ConfigScreenBuilder.CategoryBuilder} to take the options from
	 * @param config   {@link site.geni.FarLands.util.Config.ConfigSpec} to use
	 */
	@SuppressWarnings("deprecation")
	private static void updateOptions(ConfigScreenBuilder.CategoryBuilder category, Config.ConfigSpec config) {
		for (Pair<String, Object> option : category.getOptions()) {
			if (option.getRight() instanceof SubCategoryListEntry) {
				List<? extends Element> entries = ((SubCategoryListEntry) option.getRight()).children();

				farLandsLocation = (int) (Integer.MAX_VALUE / ((config.coordinateScale * config.coordinateScaleMultiplier) / 4));
				fartherLandsLocation = (long) farLandsLocation * 80;

				farthererLandsLocation = (long) (Long.MAX_VALUE / ((config.coordinateScale * config.coordinateScaleMultiplier) / 4));
				farthestLandsLocation = farthererLandsLocation * 80;

				for (Element element : entries) {
					if (!(element instanceof TextFieldListEntry)) {
						continue;
					}

					String name = ((TextFieldListEntry) element).getFieldName();

					switch (name) {
						case "config.farlands.estimatedPosition":
							TextFieldWidget farLandsLocationWidget = (TextFieldWidget) ((TextFieldListEntry) element).children().get(0);

							farLandsLocationWidget.setText(farLandsLocation >= 0 && farLandsLocation != Integer.MAX_VALUE ? "±" + farLandsLocation : INVALID);

							break;
						case "config.farlands.estimatedFartherPosition":
							TextFieldWidget fartherLandsLocationWidget = (TextFieldWidget) ((TextFieldListEntry) element).children().get(0);

							fartherLandsLocationWidget.setText(fartherLandsLocation >= 0 && fartherLandsLocation / 80 == farLandsLocation ? "±" + fartherLandsLocation : INVALID);

							break;
						case "config.farlands.estimatedFarthererPosition":
							TextFieldWidget farthererLandsLocationWidget = (TextFieldWidget) ((TextFieldListEntry) element).children().get(0);

							farthererLandsLocationWidget.setText(farthererLandsLocation >= 0 && farthererLandsLocation != Long.MAX_VALUE ? "±" + farthererLandsLocation : INVALID);

							break;
						case "config.farlands.estimatedFarthestPosition":
							TextFieldWidget farthestLandsLocationWidget = (TextFieldWidget) ((TextFieldListEntry) element).children().get(0);

							farthestLandsLocationWidget.setText(farthestLandsLocation >= 0 && farthestLandsLocation / 80 == farthererLandsLocation ? "±" + farthestLandsLocation : INVALID);

							break;
					}
				}
			}
		}
	}
}