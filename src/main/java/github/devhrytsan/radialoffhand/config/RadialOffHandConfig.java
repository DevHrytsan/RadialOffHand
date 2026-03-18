package github.devhrytsan.radialoffhand.config;

public class RadialOffHandConfig {
    public boolean modEnabled = true;       // Mod switch
    public int scaleFactor = 3; // Scales up radial menu
	public boolean toggleMode = false; // The radial menu stays open after pressing the key and closes when pressed again.
    //public boolean showItemNames = true;    // Shows names of the items
    public boolean hideEmptySlots = true;    // Whether to hide empty hotbar slots in the radial menu, instead showing them as prelocated.
    public boolean useCenterItemPreview = true; // Shows on center of radial menu a item.
	public boolean useCenterPreviewDescription = true; // Shows the selected item's description.
	public boolean allowMovementWhileOpen = true; // Allows the player to move while the radial menu is open.
	public boolean usePrioritySort = true;

}
