package github.devhrytsan.radialoffhand.utils;

import java.util.List;

import net.minecraft.client.Minecraft;
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

	public static boolean isConsumable(ItemStack stack) {

		Item item = stack.getItem();

		//? if >=1.20.5 {
		boolean isFood = stack.has(DataComponents.FOOD);
		//? } else {
		/*
		boolean isFood = item.isEdible();
        */
		//? }

		boolean isDrinkable = stack.is(Items.POTION)
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
				|| item instanceof SpyglassItem
				|| item instanceof CompassItem
				|| stack.is(Items.CLOCK);

		boolean isLightSource = stack.is(Items.TORCH)
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
