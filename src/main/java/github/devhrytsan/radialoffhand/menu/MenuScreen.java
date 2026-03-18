package github.devhrytsan.radialoffhand.menu;

import github.devhrytsan.radialoffhand.config.FileConfigHandler;
import github.devhrytsan.radialoffhand.utils.ClientPlayerUtils;
import github.devhrytsan.radialoffhand.utils.GuiGraphicsUtils;
import github.devhrytsan.radialoffhand.utils.KeyInputUtils;
import github.devhrytsan.radialoffhand.utils.MathUtils;

//? if >=1.20.5 {
//? }

//? if <1.21.5 {

/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

*///? }

import github.devhrytsan.radialoffhand.utils.MenuUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"ConstantConditions", "DataFlowIssue", "FieldMayBeFinal", "unused"})
public class MenuScreen extends Screen {

	protected static final float GLOBAL_UI_SCALE_FACTOR = 1f;
	protected static final float BASE_ITEM_RADIUS = 35f;
	protected static final float BASE_ITEM_SCALE_FACTOR = 1.5f;
	protected static final float CENTER_ITEM_SCALE_FACTOR = 3f;

	protected static final float NOT_SELECTED_ITEM_SCALE_FACTOR = 1f;
	protected static final float SELECTED_ITEM_SCALE_FACTOR = 1.5f;

	protected static final float MIN_RADIUS_IGNORE_MOUSE_FACTOR = 0.2f;
	protected static final float MAX_RADIUS_IGNORE_MOUSE_FACTOR = 6f;

	protected static final int MAX_SLOTS_COUNT = 35;
	protected static final int MIN_SLOTS_COUNT_FOR_SPACING = 14;
	protected static final int MAX_SLOTS_COUNT_FOR_SPACING = 25;

	protected static final int OFFHAND_SLOT_ID = 40;

	private static float[] SPACING_PUSH = {0.0f, 8.0f, 5.0f, 4.0f, 2.0f, 1.0f};

	private List<Integer> slotsToDraw;
	private int totalItemsToDraw = 0;

	private boolean wasLeftMousePressed = false;

	private final Minecraft client = Minecraft.getInstance();


	public static final MenuScreen INSTANCE = new MenuScreen();

	public boolean active = false;

	public MenuScreen() {
		super(Component.translatable("main.radialoffhand.title"));
		slotsToDraw = new ArrayList<>(MAX_SLOTS_COUNT);
	}

	@Override
	public void init() {

	}

	@Override
	public void tick() {
		super.tick();

	 if(FileConfigHandler.CONFIG_INSTANCE.toggleMode){
		 handleMouseClick();
	 }
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		//boolean isEnabled = FileConfigHandler.CONFIG_INSTANCE.modEnabled;
		boolean hasScreen = client.screen != null;
		boolean isPaused = !client.isPaused();

		if (hasScreen && isPaused) {
			super.render(context, mouseX, mouseY, delta);

			prepareSlots(context, mouseX, mouseY, delta);
			renderBackgrounds(context, mouseX, mouseY, delta);
			renderItems(context, mouseX, mouseY, delta);
		}
	}

	@Override
	public void removed() { // Cleanup logic that runs automatically when Radial Menu closes

		super.removed();
		active = false;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	public void activate() {

		if (client.screen == null) {
			if (client != null && client.player != null) {
				int currentSlot = ClientPlayerUtils.getPlayerSelectedSlot(client.player);
			}

			// So basically it works it only allows when the player is playing the game and not looking at another menu.
			active = true;
			client.setScreen(INSTANCE);
		}
	}

	public void deactivate(int mouseX, int mouseY) {
		active = false;

		if (client.screen == INSTANCE) {
			client.setScreen(null);
		}
	}

	public void selectAndDeactivate(){
		int scaledMouseX = (int)ClientPlayerUtils.getScaledMouseX(client);
		int scaledMouseY = (int)ClientPlayerUtils.getScaledMouseY(client);

		MenuScreen.INSTANCE.selectItem(scaledMouseX, scaledMouseY, 0);
		MenuScreen.INSTANCE.deactivate(scaledMouseX, scaledMouseY);
	}

	public void selectItem(int mouseX, int mouseY, int button) {
		if (client == null || client.player == null) return;

		int centerX = client.getWindow().getGuiScaledWidth() / 2;
		int centerY = client.getWindow().getGuiScaledHeight() / 2;

		float radius = GLOBAL_UI_SCALE_FACTOR * FileConfigHandler.CONFIG_INSTANCE.scaleFactor * BASE_ITEM_RADIUS;
		float minRadiusIgnore = radius * MIN_RADIUS_IGNORE_MOUSE_FACTOR;
		float maxRadiusIgnore = radius * MAX_RADIUS_IGNORE_MOUSE_FACTOR;

		if (totalItemsToDraw == 0 || slotsToDraw.isEmpty()) {
			return;
		}

		float anglePerItem = 360.0f / totalItemsToDraw;
		float halfAnglePerItem = anglePerItem * 0.5f;

		for (int i = 0; i < totalItemsToDraw; i++) {

			float angleDeg = anglePerItem * i - 90.0f;

			int realSlotIndex = slotsToDraw.get(i);

			float checkStart = MathUtils.normalizeAngle(angleDeg - halfAnglePerItem);
			float checkEnd = MathUtils.normalizeAngle(checkStart + anglePerItem);
			float adjustedMouseAngle = MathUtils.relativeAngle(centerX, centerY, mouseX, mouseY);

			float distanceFromCenter = MathUtils.calculateDistanceBetweenPoints(centerX, centerY, mouseX, mouseY);

			boolean mouseIn = (MathUtils.betweenTwoValues(distanceFromCenter, minRadiusIgnore, maxRadiusIgnore)) ? MathUtils.isAngleBetween(adjustedMouseAngle, checkStart, checkEnd) : false;

			if (mouseIn) {
				handleOffhandSelection(realSlotIndex);
				break;
			}

		}
	}

	private void prepareSlots(GuiGraphics context, int mouseX, int mouseY, float delta) {
		Player player = this.client.player;
		Inventory inventory = player.getInventory();

		slotsToDraw.clear();

		for (int i = 0; i < MAX_SLOTS_COUNT; i++) {
			if (!FileConfigHandler.CONFIG_INSTANCE.hideEmptySlots || !inventory.getItem(i).isEmpty()) {
				slotsToDraw.add(i);
			}
		}

		if (FileConfigHandler.CONFIG_INSTANCE.usePrioritySort) {
			slotsToDraw.sort((slotIndexA, slotIndexB) -> {
				ItemStack stackA = inventory.getItem(slotIndexA);
				ItemStack stackB = inventory.getItem(slotIndexB);

				// Get category priority (lower number = appears first)
				int categoryA = MenuUtils.getItemCategoryOrder(stackA);
				int categoryB = MenuUtils.getItemCategoryOrder(stackB);

				if (categoryA != categoryB) {
					return Integer.compare(categoryA, categoryB);
				}
				// If categories are the same, sort by item name to keep it
				return stackA.getHoverName().getString().compareTo(stackB.getHoverName().getString());
			});
		}
		totalItemsToDraw = slotsToDraw.size();
	}

	private void renderBackgrounds(GuiGraphics context, int mouseX, int mouseY, float delta) {
		//? if <1.21.1 {


			/*int color = 0x80000000;

			GuiGraphicsUtils.pushMatrix(context);
	        context.fill(0, 0, width, height, color);
			GuiGraphicsUtils.popMatrix(context);

		*///? }
	}

	private void renderItems(GuiGraphics context, int mouseX, int mouseY, float delta) {
		var clientWindow = client.getWindow();
		Player player = this.client.player;
		Inventory inventory = player.getInventory();
		var textRenderer = client.font;

		int centerX = clientWindow.getGuiScaledWidth() / 2;
		int centerY = clientWindow.getGuiScaledHeight() / 2;

		float radius = GLOBAL_UI_SCALE_FACTOR * FileConfigHandler.CONFIG_INSTANCE.scaleFactor * BASE_ITEM_RADIUS;
		float minRadiusIgnore = radius * MIN_RADIUS_IGNORE_MOUSE_FACTOR;
		float maxRadiusIgnore = radius * MAX_RADIUS_IGNORE_MOUSE_FACTOR;

		float anglePerItem = 360f / totalItemsToDraw;
		float halfAnglePerItem = anglePerItem * 0.5f;

		int selectedIndex = -1;
		ItemStack selectedStack = ItemStack.EMPTY;

		float adjustedMouseAngle = MathUtils.relativeAngle(centerX, centerY, mouseX, mouseY);
		float distanceFromCenter = MathUtils.calculateDistanceBetweenPoints(centerX, centerY, mouseX, mouseY);
		boolean isInRadius = MathUtils.betweenTwoValues(distanceFromCenter, minRadiusIgnore, maxRadiusIgnore);

		if (isInRadius) {
			for (int i = 0; i < totalItemsToDraw; i++) {
				float angleDeg = anglePerItem * i - 90.0f;
				float checkStart = MathUtils.normalizeAngle(angleDeg - halfAnglePerItem);
				float checkEnd = MathUtils.normalizeAngle(checkStart + anglePerItem);

				if (MathUtils.isAngleBetween(adjustedMouseAngle, checkStart, checkEnd)) {
					selectedIndex = i;
					selectedStack = inventory.getItem(slotsToDraw.get(i));
					break;
				}
			}
		}

		float radialPopOut = 8f;

		float settingsScale = 3.0f / FileConfigHandler.CONFIG_INSTANCE.scaleFactor;
		float itemCountScale = MathUtils.inverseLerp(MIN_SLOTS_COUNT_FOR_SPACING, MAX_SLOTS_COUNT_FOR_SPACING, totalItemsToDraw);
		float scaleFactor = settingsScale * itemCountScale;

		for (int i = 0; i < totalItemsToDraw; i++) {
			float angleDeg = anglePerItem * i - 90.0f;
			boolean isSelected = (i == selectedIndex);

			float currentRadius = radius;

			if (selectedIndex != -1) {
				if (isSelected) {
					currentRadius += radialPopOut;
				} else {
					if(MIN_SLOTS_COUNT_FOR_SPACING < totalItemsToDraw) {
						// Creates a "gap" for the selected item.
						int diff = i - selectedIndex;
						int halfTotal = totalItemsToDraw / 2;

						// Handle wrap-around
						if (diff > halfTotal) diff -= totalItemsToDraw;
						if (diff < -halfTotal) diff += totalItemsToDraw;

						int absDiff = Math.abs(diff);

						if (absDiff > 0 && absDiff < SPACING_PUSH.length) {
							float push = SPACING_PUSH[absDiff] * scaleFactor;

							if (diff > 0) {
								angleDeg += push;
							} else if (diff < 0) {
								angleDeg -= push;
							}
						}
					}
				}
			}
			int realSlotIndex = slotsToDraw.get(i);
			ItemStack stack = inventory.getItem(realSlotIndex);

			double angleRad = Math.toRadians(angleDeg);
			int x = (int) (centerX + currentRadius * Math.cos(angleRad));
			int y = (int) (centerY + currentRadius * Math.sin(angleRad));

			int renderX = x - 8;
			int renderY = y - 8;

			float scale = isSelected ? BASE_ITEM_SCALE_FACTOR * SELECTED_ITEM_SCALE_FACTOR : BASE_ITEM_SCALE_FACTOR * NOT_SELECTED_ITEM_SCALE_FACTOR;

			GuiGraphicsUtils.pushMatrix(context);
			GuiGraphicsUtils.translateMatrix(context, renderX + 8, renderY + 8, 0);
			GuiGraphicsUtils.scaleMatrix(context, scale, scale, 1);
			GuiGraphicsUtils.translateMatrix(context, -8, -8, 0);

			GuiGraphicsUtils.renderItem(context, stack, 0, 0);
			GuiGraphicsUtils.renderItemDecoration(context, textRenderer, stack, 0, 0);

			GuiGraphicsUtils.popMatrix(context);
		}

		if (FileConfigHandler.CONFIG_INSTANCE.useCenterItemPreview && !selectedStack.isEmpty()) {
			renderCenterItem(context, selectedStack);
		}
	}

	private void renderCenterItem(GuiGraphics context, ItemStack itemStack) {
		if (itemStack.isEmpty()) return;

		boolean showDescription = FileConfigHandler.CONFIG_INSTANCE.useCenterPreviewDescription;

		var clientWindow = client.getWindow();
		Player player = this.client.player;
		var textRenderer = client.font;

		float centerScale = CENTER_ITEM_SCALE_FACTOR;
		float itemSize = centerScale * 16;
		float halfItemSize = itemSize * 0.5f;

		int centerX = clientWindow.getGuiScaledWidth() / 2;
		int screenCenterY = clientWindow.getGuiScaledHeight() / 2;

		List<Component> tooltip = MenuUtils.getTooltipLines(itemStack, client);

		int fontHeight = textRenderer.lineHeight;

		int maxLinesToDraw = Math.min(tooltip.size(), 4);
		int descriptionLines = Math.max(0, maxLinesToDraw - 1);

		// Total Item Height(aka block)
		float totalHeight = itemSize;
		// Gap (5) + Height of the Item Name
		totalHeight += 5 + fontHeight;

		if (showDescription && descriptionLines > 0) {
			// Gap (4) + Height of all text + Spacing between lines (2)
			totalHeight += 4 + (descriptionLines * fontHeight) + ((descriptionLines - 1) * 2);
		}

		// Shift the starting point up by half the total height
		float startY = screenCenterY - (totalHeight / 2f);

		// Render the item
		float itemCenterY = startY + halfItemSize;

		GuiGraphicsUtils.pushMatrix(context);
		GuiGraphicsUtils.translateMatrix(context, centerX, itemCenterY, 0);
		GuiGraphicsUtils.scaleMatrix(context, centerScale, centerScale, 1);
		GuiGraphicsUtils.translateMatrix(context, -8, -8, 0);

		GuiGraphicsUtils.renderItem(context, itemStack, 0, 0);
		GuiGraphicsUtils.renderItemDecoration(context, textRenderer, itemStack, 0, 0);

		GuiGraphicsUtils.popMatrix(context);

		// Render name
		String itemName = itemStack.getHoverName().getString();
		int textWidth = textRenderer.width(itemName);

		// Cast to int  so the text rendering stays pixel perfect
		int nameY = (int) (itemCenterY + halfItemSize + 5);

		GuiGraphicsUtils.drawString(context, textRenderer, itemName, centerX - (textWidth / 2), nameY, 0xFFFFFFFF, true);

		// Render Description
		if (showDescription && descriptionLines > 0) {
			int currentDescriptionY = nameY + fontHeight + 4;

			for (int i = 1; i < maxLinesToDraw; i++) {
				Component line = tooltip.get(i);
				int lineWidth = textRenderer.width(line);

				GuiGraphicsUtils.drawString(context, textRenderer, line, centerX - (lineWidth / 2), currentDescriptionY, 0xFFFFFFFF, true);

				currentDescriptionY += fontHeight + 2;
			}
		}
	}


	private void handleOffhandSelection(int sourceSlot) {
		if (sourceSlot >= 0 && sourceSlot < 9) {

			int previousSelected = ClientPlayerUtils.getPlayerSelectedSlot(client.player);
			ClientPlayerUtils.setPlayerSelectedSlot(client.player, sourceSlot);
			client.getConnection().send(new ServerboundSetCarriedItemPacket(sourceSlot));

			client.getConnection().send(new ServerboundPlayerActionPacket(
					ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,
					BlockPos.ZERO,
					Direction.DOWN
			));

			ClientPlayerUtils.setPlayerSelectedSlot(client.player, previousSelected);
			client.getConnection().send(new ServerboundSetCarriedItemPacket(previousSelected));

		} else {
			ClientPlayerUtils.handleMouseClickSwap(client, OFFHAND_SLOT_ID, sourceSlot);
		}
	}

	private void handleMouseClick() {
		var clientWindow = client.getWindow();

		var keyAttack = Minecraft.getInstance().options.keyAttack;
		boolean isLeftMousePressed = KeyInputUtils.isHardwareKeyPressed(keyAttack, clientWindow);

		// This is not the best approach.
        // It could be done using Screen events (like mousePressed, etc.),
        // but due to differences between versions, it becomes a backward compatibility nightmare in code.

		if (isLeftMousePressed && !wasLeftMousePressed) {
			MenuScreen.INSTANCE.selectAndDeactivate();
		}

		wasLeftMousePressed = isLeftMousePressed;
	}
}
