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
