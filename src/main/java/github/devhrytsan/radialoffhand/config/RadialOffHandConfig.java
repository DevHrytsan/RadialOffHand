package github.devhrytsan.radialoffhand.config;

import java.util.ArrayList;
import java.util.List;

public class RadialOffHandConfig {
    public boolean modEnabled = true;       // Mod switch
    public int scaleFactor = 3; // Scales up radial menu
	public boolean toggleMode = false; // The radial menu stays open after pressing the key and closes when pressed again.
    //public boolean showItemNames = true;    // Shows names of the items
    public boolean hideEmptySlots = true;    // Whether to hide empty hotbar slots in the radial menu, instead showing them as prelocated.
    public boolean useCenterItemPreview = true; // Shows on center of radial menu a item.
	public boolean useCenterPreviewDescription = true; // Shows the selected item's description.
	public boolean allowMovementWhileOpen = true; // Allows the player to move while the radial menu is open.
	public boolean usePrioritySort = true; // Sorts items by category: Totem, Defense, Mobility(Fireworks), Torches, Food, Blocks.
	public boolean excludeHotbar = false; // Excludes hotbar slots
	public boolean onlyShowConsumables = false; // Show only consumables
	public boolean showConsumablesFromHotbar = true; // Include consumables from the hotbar even if it's excluded
	public boolean useMenuBackgroundEffects = true; // Enables or disables the background tint(blur) behind menu.
	public boolean showSlotBackground = false; // Draws inventory slot borders around each item in the radial menu.
	public boolean useRightClickUnequip = true; // Right click while the radial menu is open moves the offhand item back into the inventory.
	public UnequipPriority unequipPriority = UnequipPriority.INVENTORY_FIRST; // Where the unequipped item goes first: main inventory or hotbar.

	public List<String> consumableWhitelist = new ArrayList<>(); // Item ids that always count as consumables (e.g. "minecraft:spyglass")
	public List<String> consumableBlacklist = new ArrayList<>(); // Item ids that never count as consumables

	public List<String> getConsumableWhitelist() {
		if (consumableWhitelist == null) consumableWhitelist = new ArrayList<>();
		return consumableWhitelist;
	}

	public List<String> getConsumableBlacklist() {
		if (consumableBlacklist == null) consumableBlacklist = new ArrayList<>();
		return consumableBlacklist;
	}

	public UnequipPriority getUnequipPriority() {
		// Missing or unknown values (e.g. older or hand-edited config files) fall back to the default
		return unequipPriority != null ? unequipPriority : UnequipPriority.INVENTORY_FIRST;
	}
}
