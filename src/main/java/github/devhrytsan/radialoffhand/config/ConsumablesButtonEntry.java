package github.devhrytsan.radialoffhand.config;

import github.devhrytsan.radialoffhand.compatibility.GuiGraphicsLayer;
import github.devhrytsan.radialoffhand.utils.ClientPlayerUtils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

//? if >= 26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///? } else {
import net.minecraft.client.gui.GuiGraphics;
//? }

import java.util.List;
import java.util.Optional;

// A Cloth Config row with a label on the left and a button on the right that opens the consumables picker.
// It holds no value of its own: the picker saves its changes immediately.
public class ConsumablesButtonEntry extends AbstractConfigListEntry<Object> {

	private static final int BUTTON_WIDTH = 150;
	private static final int LABEL_COLOR = 0xFFFFFFFF;
	private static final int DISABLED_LABEL_COLOR = 0xFF808080;

	private final Button button;
	private final GuiGraphicsLayer guiContextLayer = new GuiGraphicsLayer();

	public ConsumablesButtonEntry(Component fieldName, Component buttonText) {
		super(fieldName, false);

		this.button = Button.builder(buttonText, pressed -> {
			Minecraft client = Minecraft.getInstance();
			ClientPlayerUtils.setScreen(client, new ConsumablesScreen(ClientPlayerUtils.getScreen(client)));
		}).bounds(0, 0, BUTTON_WIDTH, 20).build();
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public Optional<Object> getDefaultValue() {
		return Optional.empty();
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return List.of(button);
	}

	@Override
	public List<? extends NarratableEntry> narratables() {
		return List.of(button);
	}

	//? if >= 26.1 {
	/*@Override
	public void extractRenderState(GuiGraphicsExtractor context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
		super.extractRenderState(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
		placeButton(context, x, y, entryWidth);
		button.extractRenderState(context, mouseX, mouseY, delta);
	}
	*///? } else {
	@Override
	public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
		placeButton(context, x, y, entryWidth);
		button.render(context, mouseX, mouseY, delta);
	}
	//? }

	private void placeButton(Object context, int x, int y, int entryWidth) {
		boolean isEditable = isEditable();
		button.active = isEditable;

		guiContextLayer.setContext(context);
		guiContextLayer.drawString(Minecraft.getInstance().font, getFieldName(), x, y + 6, isEditable ? LABEL_COLOR : DISABLED_LABEL_COLOR, true);

		button.setX(x + entryWidth - BUTTON_WIDTH);
		button.setY(y);
	}
}
