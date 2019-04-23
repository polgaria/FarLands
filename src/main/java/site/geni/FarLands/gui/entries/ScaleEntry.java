package site.geni.FarLands.gui.entries;

import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.entries.DoubleListEntry;
import me.shedaniel.cloth.gui.entries.SubCategoryListEntry;
import me.shedaniel.cloth.gui.entries.TextFieldListEntry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Pair;
import site.geni.FarLands.gui.CustomizeFarLandsScreen;
import site.geni.FarLands.util.Config;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScaleEntry extends DoubleListEntry {
	private static final String INVALID = TextFormat.RED + I18n.translate("config.farlands.invalid");

	private final ConfigScreenBuilder.CategoryBuilder category;

	public ScaleEntry(String fieldName, Double value, String resetButtonKey, Supplier<Double> defaultValue, Consumer<Double> saveConsumer, ConfigScreenBuilder.CategoryBuilder category) {
		super(fieldName, value, resetButtonKey, defaultValue, saveConsumer);

		this.category = category;
	}

	@Override
	public boolean keyPressed(int charCode, int int_1, int int_2) {
		return super.keyPressed(charCode, int_1, int_2) && updateScales();
	}

	@Override
	public boolean charTyped(char character, int charcode) {
		return super.charTyped(character, charcode) && updateScales();
	}

	private boolean updateScales() {
		try {
			double scale = this.getObject();

			Config.ConfigSpec config = Config.getConfig();
			switch (this.getFieldName()) {
				case "config.farlands.coordinateScale":
					config.coordinateScale = scale;
					break;
				case "config.farlands.coordinateScaleMultiplier":
					config.coordinateScaleMultiplier = scale;
					break;
			}

			ScaleEntry.updateOptions(this.category, config);

			return true;
		} catch (ArithmeticException | NumberFormatException ignored) {

		}

		return false;
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

				CustomizeFarLandsScreen.setFarLandsLocation((int) (Integer.MAX_VALUE / ((config.coordinateScale * config.coordinateScaleMultiplier) / 4)));
				CustomizeFarLandsScreen.setFartherLandsLocation((long) CustomizeFarLandsScreen.getFarLandsLocation() * 80);

				CustomizeFarLandsScreen.setFarthererLandsLocation((long) (Long.MAX_VALUE / ((config.coordinateScale * config.coordinateScaleMultiplier) / 4)));
				CustomizeFarLandsScreen.setFarthestLandsLocation(CustomizeFarLandsScreen.getFarthererLandsLocation() * 80);

				for (Element element : entries) {
					if (!(element instanceof TextFieldListEntry)) {
						continue;
					}

					String name = ((TextFieldListEntry) element).getFieldName();
					TextFieldWidget widget = (TextFieldWidget) ((TextFieldListEntry) element).children().get(0);

					switch (name) {
						case "config.farlands.estimatedPosition":
							widget.setText(CustomizeFarLandsScreen.getFarLandsLocation() >= 0 && CustomizeFarLandsScreen.getFarLandsLocation() != Integer.MAX_VALUE ? "±" + CustomizeFarLandsScreen.getFarLandsLocation() : INVALID);

							break;
						case "config.farlands.estimatedFartherPosition":
							widget.setText(CustomizeFarLandsScreen.getFartherLandsLocation() >= 0 && CustomizeFarLandsScreen.getFartherLandsLocation() / 80 != Integer.MAX_VALUE ? "±" + CustomizeFarLandsScreen.getFartherLandsLocation() : INVALID);

							break;
						case "config.farlands.estimatedFarthererPosition":
							widget.setText(CustomizeFarLandsScreen.getFarthererLandsLocation() >= 0 && CustomizeFarLandsScreen.getFarthererLandsLocation() != Long.MAX_VALUE ? "±" + CustomizeFarLandsScreen.getFarthererLandsLocation() : INVALID);

							break;
						case "config.farlands.estimatedFarthestPosition":
							widget.setText(CustomizeFarLandsScreen.getFarthestLandsLocation() >= 0 && CustomizeFarLandsScreen.getFarthestLandsLocation() / 80 == CustomizeFarLandsScreen.getFarthererLandsLocation() ? "±" + CustomizeFarLandsScreen.getFarthestLandsLocation() : INVALID);

							break;
					}

					widget.moveCursor(0);
				}
			}
		}
	}
}
