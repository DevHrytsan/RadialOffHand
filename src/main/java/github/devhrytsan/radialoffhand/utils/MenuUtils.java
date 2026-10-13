package github.devhrytsan.radialoffhand.utils;

import java.util.List;

import github.devhrytsan.radialoffhand.config.FileConfigHandler;
import github.devhrytsan.radialoffhand.config.RadialOffHandConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.network.chat.Component;

//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
//? }

public class MenuUtils {

	/**
	 * Assigns a priority index based on requested order:
	 * 0: Totem
	 * 1: Defense
	 * 2: Mobility
	 * 3: Torches
	 * 4: Food
	 * 5: Construction
	 * 6: Combat Utility
	 * 7: Miscellaneous
	 */
	public static int getItemCategoryOrder(ItemStack stack) {
		if (stack.isEmpty()) return 7;

		var item = stack.getItem();

		if (item == Items.TOTEM_OF_UNDYING) {
			return 0;
		}
		if (item instanceof ShieldItem) {
			return 1;
		}
		if (item == Items.FIREWORK_ROCKET) {
			return 2;
		}

        // Torches
		if (item instanceof BlockItem blockItem) {
			var block = blockItem.getBlock();
			if (block == Blocks.TORCH || block == Blocks.SOUL_TORCH || block == Blocks.REDSTONE_TORCH) {
				return 3;
			}

			//? if >=1.21.9 {
			if (block == Blocks.COPPER_TORCH) {
				return 3;
			}
			//? }
		}

		//? if >=1.20.5 {

		if (stack.has(DataComponents.FOOD)) {
			return 4;
		}

		//? } else {
		/*
		if (item.isEdible()) {
			return 4;
		}
        */
		//? }


		// BlockItem check (works in all versions)
		if (item instanceof BlockItem) {
			return 5;
		}

		return 6;
	}

	// Whether two stacks can merge into one (same item and same data)
	public static boolean canStack(ItemStack a, ItemStack b) {
		//? if >=1.20.5 {
		return ItemStack.isSameItemSameComponents(a, b);
		//? } else {
		/*return ItemStack.isSameItemSameTags(a, b);
		*///? }
	}

	public static String getItemId(ItemStack stack) {
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
	}

	// The player's own lists win over the built-in rules
	public static boolean isConsumable(ItemStack stack) {
		RadialOffHandConfig config = FileConfigHandler.CONFIG_INSTANCE;
		String id = getItemId(stack);

		if (config.getConsumableBlacklist().contains(id)) return false;
		if (config.getConsumableWhitelist().contains(id)) return true;

		return isDefaultConsumable(stack);
	}

	// Flips whether the item counts as a consumable, saves the config and returns the new state
	public static boolean toggleConsumable(ItemStack stack) {
		RadialOffHandConfig config = FileConfigHandler.CONFIG_INSTANCE;
		String id = getItemId(stack);
		boolean wasConsumable = isConsumable(stack);

		if (wasConsumable) {
			if (!config.getConsumableWhitelist().remove(id)) config.getConsumableBlacklist().add(id);
		} else {
			if (!config.getConsumableBlacklist().remove(id)) config.getConsumableWhitelist().add(id);
		}

		FileConfigHandler.saveConfig();
		return !wasConsumable;
	}

	// Whether the player changed this item away from the mod's default
	public static boolean isConsumableChanged(ItemStack stack) {
		RadialOffHandConfig config = FileConfigHandler.CONFIG_INSTANCE;
		String id = getItemId(stack);
		return config.getConsumableWhitelist().contains(id) || config.getConsumableBlacklist().contains(id);
	}

	// Puts one item back to the mod's default
	public static void resetConsumable(ItemStack stack) {
		RadialOffHandConfig config = FileConfigHandler.CONFIG_INSTANCE;
		String id = getItemId(stack);
		config.getConsumableWhitelist().remove(id);
		config.getConsumableBlacklist().remove(id);
		FileConfigHandler.saveConfig();
	}

	// Puts every item back to the mod's default
	public static void resetAllConsumables() {
		RadialOffHandConfig config = FileConfigHandler.CONFIG_INSTANCE;
		config.getConsumableWhitelist().clear();
		config.getConsumableBlacklist().clear();
		FileConfigHandler.saveConfig();
	}

	private static boolean isDefaultConsumable(ItemStack stack) {

		Item item = stack.getItem();

		//? if >=1.20.5 {
		boolean isFood = stack.has(DataComponents.FOOD);
		//? } else {
		/*
		boolean isFood = item.isEdible();
        */
		//? }

		boolean isDrinkable = stack.is(Items.POTION)
				|| stack.is(Items.SPLASH_POTION)
				|| stack.is(Items.LINGERING_POTION)
				|| stack.is(Items.MILK_BUCKET)
				|| stack.is(Items.HONEY_BOTTLE);


		//? if >=1.20.3 {
		boolean isThrowable = item instanceof SnowballItem
				|| item instanceof EggItem
				|| item instanceof WindChargeItem
				|| item instanceof FireworkRocketItem
				|| stack.is(Items.ENDER_PEARL);
		//? } else {

		/*
			boolean isThrowable = item instanceof SnowballItem
				|| item instanceof EggItem
				|| item instanceof FireworkRocketItem
				|| stack.is(Items.ENDER_PEARL);
		*///? }

		boolean isCombatUtility = item instanceof ShieldItem
				|| item instanceof BowItem
				|| item instanceof CrossbowItem
				|| item instanceof TridentItem
				|| stack.is(Items.TOTEM_OF_UNDYING);

		boolean isUtilityTool = item instanceof FishingRodItem
				|| item instanceof LeadItem
				|| item instanceof FlintAndSteelItem
				|| item instanceof MapItem
				|| stack.is(Items.MAP)
				|| stack.is(Items.RECOVERY_COMPASS)
				|| item instanceof SpyglassItem
				|| item instanceof CompassItem
				|| stack.is(Items.CLOCK);

		boolean isLightSource = stack.is(Items.TORCH)
				|| stack.is(Items.SOUL_TORCH)
				|| stack.is(Items.REDSTONE_TORCH)
				|| stack.is(Items.LANTERN)
				|| stack.is(Items.SOUL_LANTERN);

		boolean isBucket = item instanceof BucketItem;

		return isFood || isDrinkable || isThrowable || isCombatUtility
				|| isUtilityTool || isLightSource || isBucket;
	}

	public static boolean canBeEquipped(ItemStack stack) {
		if (stack.isEmpty()) return false;

		//? if >=1.21.5 {
		// Modern logic
		if (stack.has(DataComponents.EQUIPPABLE)) {
			return true;
		}
		return false;

		//? } else {

		/*// Legacy logic
		var item = stack.getItem();
		return item instanceof ArmorItem || item instanceof ElytraItem;

		*///? }

	}

	public static int getArmorSlot(EquipmentSlot slot) {
		int armorSlotId = -1;
		switch (slot) {
			case HEAD -> armorSlotId = 5;
			case CHEST -> armorSlotId = 6;
			case LEGS -> armorSlotId = 7;
			case FEET -> armorSlotId = 8;
			case OFFHAND -> armorSlotId = 45;
			default -> armorSlotId = -1;
		}
		return armorSlotId;
	}

	public static List<Component> getTooltipLines(ItemStack itemStack, Minecraft client){
		var player = client.player;

        //? if >=1.20.5 {
		return itemStack.getTooltipLines(Item.TooltipContext.of(client.level), player, TooltipFlag.NORMAL);
		//? } else {
		/*
        return itemStack.getTooltipLines(player, TooltipFlag.Default.NORMAL);
        */
		//? }
	}

}
