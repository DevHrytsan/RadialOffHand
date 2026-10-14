package github.devhrytsan.radialoffhand.config;

import github.devhrytsan.radialoffhand.compatibility.GuiGraphicsLayer;
import github.devhrytsan.radialoffhand.utils.ClientPlayerUtils;
import github.devhrytsan.radialoffhand.utils.MenuUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

//? if >= 26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///? } else {
import net.minecraft.client.gui.GuiGraphics;
//? }

//? if >=1.21.10 {
import net.minecraft.client.input.MouseButtonEvent;
//? }

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Lets the player pick which items count as consumables by clicking them in a searchable grid of every item
public class ConsumablesScreen extends Screen {

	private static final int CELL_SIZE = 20;
	private static final int MAX_COLUMNS = 18;
	private static final int GRID_TOP = 48;

	private static final int NORMAL_FILL_COLOR = 0x66000000;
	private static final int NORMAL_BORDER_COLOR = 0xFF555555;
	private static final int CONSUMABLE_FILL_COLOR = 0x5532CC32;
	private static final int CONSUMABLE_BORDER_COLOR = 0xFF3FBF3F;
	private static final int HOVERED_BORDER_COLOR = 0xFFFFFFFF;
	private static final int CHANGED_MARKER_COLOR = 0xFFFFD040;
	private static final int TEXT_COLOR = 0xFFFFFFFF;
	private static final int HINT_COLOR = 0xFFA0A0A0;

	private enum Filter {
		ALL, CONSUMABLES, OTHERS;

		Filter next() {
			return values()[(ordinal() + 1) % values().length];
		}
	}

	private final Screen parent;
	private final GuiGraphicsLayer guiContextLayer = new GuiGraphicsLayer();

	private final List<ItemStack> allItems = new ArrayList<>();
	private final List<String> searchTexts = new ArrayList<>(); // Lowercase "name id" per item, same order as allItems
	private final List<ItemStack> visibleItems = new ArrayList<>();

	private EditBox searchBox;
	private Button filterButton;
	private String query = "";
	private Filter filter = Filter.ALL;

	private int columns = 1;
	private int visibleRows = 1;
	private int gridLeft = 0;
	private int scrollRow = 0;
	private int hoveredIndex = -1;

	public ConsumablesScreen(Screen parent) {
		super(Component.translatable("config.radialoffhand.consumables.title"));
		this.parent = parent;

		for (Item item : BuiltInRegistries.ITEM) {
			if (item == Items.AIR) continue;

			ItemStack stack = new ItemStack(item);
			allItems.add(stack);
			searchTexts.add((stack.getHoverName().getString() + " " + MenuUtils.getItemId(stack)).toLowerCase(Locale.ROOT));
		}
	}

	@Override
	protected void init() {
		Font font = Minecraft.getInstance().font;

		searchBox = new EditBox(font, this.width / 2 - 100, 22, 200, 20, Component.translatable("config.radialoffhand.consumables.search"));
		searchBox.setHint(Component.translatable("config.radialoffhand.consumables.search"));
		searchBox.setValue(query);
		searchBox.setResponder(value -> {
			query = value.trim().toLowerCase(Locale.ROOT);
			refreshVisibleItems();
		});
		addRenderableWidget(searchBox);
		setInitialFocus(searchBox);

		int buttonsY = this.height - 26;
		int buttonsLeft = this.width / 2 - 171;

		filterButton = addRenderableWidget(Button.builder(getFilterText(), button -> {
			filter = filter.next();
			button.setMessage(getFilterText());
			refreshVisibleItems();
		}).bounds(buttonsLeft, buttonsY, 134, 20).build());

		addRenderableWidget(Button.builder(Component.translatable("config.radialoffhand.consumables.reset"), button -> confirmReset())
				.bounds(buttonsLeft + 138, buttonsY, 100, 20).build());

		addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
				.bounds(buttonsLeft + 242, buttonsY, 100, 20).build());

		int gridBottom = getInfoLineY() - 4;
		columns = Math.max(1, Math.min(MAX_COLUMNS, (this.width - 40) / CELL_SIZE));
		visibleRows = Math.max(1, (gridBottom - GRID_TOP) / CELL_SIZE);
		gridLeft = (this.width - columns * CELL_SIZE) / 2;

		refreshVisibleItems();
	}

	private int getInfoLineY() {
		return this.height - 26 - 14;
	}

	private Component getFilterText() {
		return Component.translatable("config.radialoffhand.consumables.filter." + filter.name().toLowerCase(Locale.ROOT));
	}

	private void refreshVisibleItems() {
		visibleItems.clear();

		for (int i = 0; i < allItems.size(); i++) {
			ItemStack stack = allItems.get(i);
			if (!query.isEmpty() && !searchTexts.get(i).contains(query)) continue;

			if (filter != Filter.ALL) {
				boolean isConsumable = MenuUtils.isConsumable(stack);
				if ((filter == Filter.CONSUMABLES) != isConsumable) continue;
			}

			visibleItems.add(stack);
		}

		scrollRow = Math.max(0, Math.min(scrollRow, getMaxScrollRow()));
	}

	private int getTotalRows() {
		return (visibleItems.size() + columns - 1) / columns;
	}

	private int getMaxScrollRow() {
		return Math.max(0, getTotalRows() - visibleRows);
	}

	private void confirmReset() {
		Minecraft client = Minecraft.getInstance();

		ClientPlayerUtils.setScreen(client, new ConfirmScreen(confirmed -> {
			if (confirmed) {
				MenuUtils.resetAllConsumables();
				refreshVisibleItems();
			}
			ClientPlayerUtils.setScreen(client, this);
		}, Component.translatable("config.radialoffhand.consumables.reset.title"), Component.translatable("config.radialoffhand.consumables.reset.message")));
	}

	@Override
	public void onClose() {
		ClientPlayerUtils.setScreen(Minecraft.getInstance(), parent);
	}

	//? if >= 26.1 {
	/*@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		super.extractRenderState(context, mouseX, mouseY, delta);

		guiContextLayer.setContext(context);
		handleRender(guiContextLayer, mouseX, mouseY);
	}
	*///? } else {
	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		//? if <1.20.2 {
		/*this.renderBackground(context);
		*///? }
		super.render(context, mouseX, mouseY, delta);

		guiContextLayer.setContext(context);
		handleRender(guiContextLayer, mouseX, mouseY);
	}
	//? }

	private void handleRender(GuiGraphicsLayer context, int mouseX, int mouseY) {
		Font font = Minecraft.getInstance().font;

		context.drawString(font, this.title, (this.width - font.width(this.title)) / 2, 8, TEXT_COLOR, true);

		hoveredIndex = -1;
		int firstIndex = scrollRow * columns;

		for (int i = 0; i < visibleRows * columns; i++) {
			int index = firstIndex + i;
			if (index >= visibleItems.size()) break;

			int x = gridLeft + (i % columns) * CELL_SIZE;
			int y = GRID_TOP + (i / columns) * CELL_SIZE;
			ItemStack stack = visibleItems.get(index);

			boolean isHovered = mouseX >= x && mouseX < x + CELL_SIZE && mouseY >= y && mouseY < y + CELL_SIZE;
			if (isHovered) hoveredIndex = index;

			boolean isConsumable = MenuUtils.isConsumable(stack);
			int borderColor = isHovered ? HOVERED_BORDER_COLOR : isConsumable ? CONSUMABLE_BORDER_COLOR : NORMAL_BORDER_COLOR;

			context.fill(x + 1, y + 1, x + CELL_SIZE - 1, y + CELL_SIZE - 1, isConsumable ? CONSUMABLE_FILL_COLOR : NORMAL_FILL_COLOR);
			renderFrame(context, x, y, x + CELL_SIZE, y + CELL_SIZE, borderColor);
			context.renderItem(stack, x + 2, y + 2);

			if (MenuUtils.isConsumableChanged(stack)) {
				// Marks items the player changed from the default
				context.fill(x + CELL_SIZE - 4, y + 1, x + CELL_SIZE - 1, y + 4, CHANGED_MARKER_COLOR);
			}
		}

		if (visibleItems.isEmpty()) {
			Component empty = Component.translatable("config.radialoffhand.consumables.empty");
			context.drawString(font, empty, (this.width - font.width(empty)) / 2, GRID_TOP + 10, HINT_COLOR, true);
		}

		renderScrollbar(context);
		renderInfoLine(context, font);
	}

	private void renderScrollbar(GuiGraphicsLayer context) {
		int maxScrollRow = getMaxScrollRow();
		if (maxScrollRow == 0) return;

		int x = gridLeft + columns * CELL_SIZE + 4;
		int trackHeight = visibleRows * CELL_SIZE;
		int thumbHeight = Math.max(8, trackHeight * visibleRows / getTotalRows());
		int thumbY = GRID_TOP + (trackHeight - thumbHeight) * scrollRow / maxScrollRow;

		context.fill(x, GRID_TOP, x + 4, GRID_TOP + trackHeight, 0x40FFFFFF);
		context.fill(x, thumbY, x + 4, thumbY + thumbHeight, 0xFFAAAAAA);
	}

	// Shows what the hovered item is set to, or how to use the screen when nothing is hovered
	private void renderInfoLine(GuiGraphicsLayer context, Font font) {
		Component line;
		int color;

		if (hoveredIndex != -1) {
			ItemStack stack = visibleItems.get(hoveredIndex);
			boolean isConsumable = MenuUtils.isConsumable(stack);
			boolean isChanged = MenuUtils.isConsumableChanged(stack);

			String status = isConsumable ? "consumable" : "not_consumable";
			if (isChanged) status += "_changed";

			line = Component.translatable("config.radialoffhand.consumables.status." + status, stack.getHoverName());
			color = TEXT_COLOR;
		} else {
			line = Component.translatable("config.radialoffhand.consumables.hint");
			color = HINT_COLOR;
		}

		context.drawString(font, line, (this.width - font.width(line)) / 2, getInfoLineY(), color, true);
	}

	private static void renderFrame(GuiGraphicsLayer context, int minX, int minY, int maxX, int maxY, int color) {
		context.fill(minX, minY, maxX, minY + 1, color);
		context.fill(minX, maxY - 1, maxX, maxY, color);
		context.fill(minX, minY + 1, minX + 1, maxY - 1, color);
		context.fill(maxX - 1, minY + 1, maxX, maxY - 1, color);
	}

	// Left click flips the item, right click puts it back to the mod's default
	private boolean handleGridClick(double mouseX, double mouseY, int button) {
		if (mouseX < gridLeft || mouseX >= gridLeft + columns * CELL_SIZE) return false;
		if (mouseY < GRID_TOP || mouseY >= GRID_TOP + visibleRows * CELL_SIZE) return false;

		int column = (int) ((mouseX - gridLeft) / CELL_SIZE);
		int row = (int) ((mouseY - GRID_TOP) / CELL_SIZE);
		int index = (scrollRow + row) * columns + column;
		if (index >= visibleItems.size()) return false;

		ItemStack stack = visibleItems.get(index);
		if (button == 0) {
			MenuUtils.toggleConsumable(stack);
		} else if (button == 1) {
			MenuUtils.resetConsumable(stack);
		} else {
			return false;
		}

		// With a filter on, the clicked item may no longer belong in the list
		if (filter != Filter.ALL) refreshVisibleItems();
		return true;
	}

	//? if >=1.21.10 {
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (handleGridClick(event.x(), event.y(), event.button())) return true;
		return super.mouseClicked(event, doubleClick);
	}
	//? } else {
	/*@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (handleGridClick(mouseX, mouseY, button)) return true;
		return super.mouseClicked(mouseX, mouseY, button);
	}
	*///? }

	//? if >=1.20.2 {
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		scrollBy(scrollY);
		return true;
	}
	//? } else {
	/*@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		scrollBy(delta);
		return true;
	}
	*///? }

	private void scrollBy(double amount) {
		int step = amount > 0 ? -1 : amount < 0 ? 1 : 0;
		scrollRow = Math.max(0, Math.min(getMaxScrollRow(), scrollRow + step));
	}
}
