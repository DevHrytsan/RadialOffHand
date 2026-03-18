package github.devhrytsan.radialoffhand.menu;

import github.devhrytsan.radialoffhand.RadialOffHandMod;

import github.devhrytsan.radialoffhand.config.FileConfigHandler;
import github.devhrytsan.radialoffhand.utils.ClientPlayerUtils;
import github.devhrytsan.radialoffhand.utils.KeyInputUtils;
import net.minecraft.client.Minecraft;


public class MenuController {

	public static final MenuController INSTANCE = new MenuController();

	private boolean isMenuOpen = false;
	private boolean wasKeyPressed = false;
	private boolean lastRadialMenuState;

	public void HandleUpdate(Minecraft client) {
		boolean isEnabled = FileConfigHandler.CONFIG_INSTANCE.modEnabled;

		var clientWindow = client.getWindow();

		// Seriously,
		// due how Minecraft handles inputs when a Screen is open. So when a Menu opens, Minecraft stops updating gameplay keys.
		// It assumes you are typing in chat or searching inventory, so it forces all gameplay keys to False(aka prevents walking)
		// So for it, I need a hardware check of the button.
		// Thanks to mod called "MineMenu" I learnt how to deal with it.
		if (isEnabled) {
				boolean radialMenuKeyDown = KeyInputUtils.isHardwareKeyPressed(RadialOffHandMod.OPEN_RADIAL_OFFHAND_MENU_KEY, clientWindow);

				if (radialMenuKeyDown != lastRadialMenuState) {
				if(FileConfigHandler.CONFIG_INSTANCE.toggleMode) {
					if (radialMenuKeyDown) {
						if (MenuScreen.INSTANCE.active) {
							MenuScreen.INSTANCE.deactivate(0,0);
						} else {
							if (client.screen == null || client.screen instanceof MenuScreen) {
								MenuScreen.INSTANCE.activate();
							}
						}
					}
				}
				else{
					if (radialMenuKeyDown != MenuScreen.INSTANCE.active) {

						if (radialMenuKeyDown) {
							if ((client.screen == null || client.screen instanceof MenuScreen)) {
								MenuScreen.INSTANCE.activate();
							}
						} else {
							MenuScreen.INSTANCE.selectAndDeactivate();
						}
					}
				}
			}

			lastRadialMenuState = radialMenuKeyDown;

		} else {
			if (MenuScreen.INSTANCE.active) MenuScreen.INSTANCE.deactivate(0, 0);
		}
	}
}
