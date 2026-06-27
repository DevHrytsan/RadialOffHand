package github.devhrytsan.radialoffhand.config;

import github.devhrytsan.radialoffhand.Constants;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RadialOffHandConfigScreen {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("main.radialoffhand.title"));
        builder.setSavingRunnable(FileConfigHandler::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.radialhotbar.category.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();


        var modEnableToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.enabled"), FileConfigHandler.CONFIG_INSTANCE.modEnabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.radialoffhand.option.enabled.tooltip"))
                .setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.modEnabled = newValue)
                .build();

        var scaleFactorSlider = entryBuilder.startIntSlider(
						Component.translatable("config.radialoffhand.option.scaleFactor"),
                        FileConfigHandler.CONFIG_INSTANCE.scaleFactor,
                        Constants.MIN_SCALE_FACTOR,
                        Constants.MAX_SCALE_FACTOR
                ).setDefaultValue(Constants.DEFAULT_SCALE_FACTOR)
                .setTooltip(Component.translatable("config.radialoffhand.option.scaleFactor.tooltip"))
                .setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.scaleFactor = newValue) // Save action
                .build();

		var toggleBooleanToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.toggleMode"), FileConfigHandler.CONFIG_INSTANCE.toggleMode)
				.setDefaultValue(false)
				.setTooltip(Component.translatable("config.radialoffhand.option.toggleMode.tooltip"))
				.setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.toggleMode = newValue)
				.build();

        var hideEmptySlotToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.hideEmptySlots"), FileConfigHandler.CONFIG_INSTANCE.hideEmptySlots)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.radialoffhand.option.hideEmptySlots.tooltip"))
                .setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.hideEmptySlots = newValue)
                .build();

        var useCenterPreviewToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.useCenterItemPreview"), FileConfigHandler.CONFIG_INSTANCE.useCenterItemPreview)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.radialoffhand.option.useCenterItemPreview.tooltip"))
                .setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.useCenterItemPreview = newValue)
                .build();

		var useCenterPreviewDescriptionToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.useCenterPreviewDescription"), FileConfigHandler.CONFIG_INSTANCE.useCenterPreviewDescription)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("config.radialoffhand.option.useCenterPreviewDescription.tooltip"))
				.setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.useCenterPreviewDescription = newValue)
				.setRequirement(() -> useCenterPreviewToggle.getValue() == true)
				.build();

        var allowMovementToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.allowMovementWhileOpen"), FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.radialoffhand.option.allowMovementWhileOpen.tooltip"))
                .setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen = newValue)
                .build();

		var prioritySortToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.usePrioritySort"), FileConfigHandler.CONFIG_INSTANCE.usePrioritySort)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("config.radialoffhand.option.usePrioritySort.tooltip"))
				.setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.usePrioritySort = newValue)
				.build();

		var excludeHotbarToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.excludeHotbar"), FileConfigHandler.CONFIG_INSTANCE.excludeHotbar)
				.setDefaultValue(false)
				.setTooltip(Component.translatable("config.radialoffhand.option.excludeHotbar.tooltip"))
				.setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.excludeHotbar = newValue)
				.build();
		var onlyShowConsumablesToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.onlyShowConsumables"), FileConfigHandler.CONFIG_INSTANCE.onlyShowConsumables)
				.setDefaultValue(false)
				.setTooltip(Component.translatable("config.radialoffhand.option.onlyShowConsumables.tooltip"))
				.setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.onlyShowConsumables = newValue)
				.build();

		 var showConsumablesFromHotbarToggle = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.showConsumablesFromHotbar"), FileConfigHandler.CONFIG_INSTANCE.showConsumablesFromHotbar)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("config.radialoffhand.option.showConsumablesFromHotbar.tooltip"))
				.setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.showConsumablesFromHotbar = newValue)
				 .setRequirement(() -> onlyShowConsumablesToggle.getValue() == true && excludeHotbarToggle.getValue() == true)
				 .build();

		var showMenuBackgroundEffect = entryBuilder.startBooleanToggle(Component.translatable("config.radialoffhand.option.useMenuBackgroundEffects"), FileConfigHandler.CONFIG_INSTANCE.useMenuBackgroundEffects)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("config.radialoffhand.option.useMenuBackgroundEffects.tooltip"))
				.setSaveConsumer(newValue -> FileConfigHandler.CONFIG_INSTANCE.useMenuBackgroundEffects = newValue)
				.build();

		general.addEntry(modEnableToggle);
		general.addEntry(scaleFactorSlider);
		general.addEntry(toggleBooleanToggle);
		general.addEntry(hideEmptySlotToggle);
		general.addEntry(useCenterPreviewToggle);
		general.addEntry(useCenterPreviewDescriptionToggle);
		general.addEntry(allowMovementToggle);
		general.addEntry(prioritySortToggle);
		general.addEntry(excludeHotbarToggle);
		general.addEntry(onlyShowConsumablesToggle);
		general.addEntry(showConsumablesFromHotbarToggle);
		general.addEntry(showMenuBackgroundEffect);

        return builder.build();
    }
}
