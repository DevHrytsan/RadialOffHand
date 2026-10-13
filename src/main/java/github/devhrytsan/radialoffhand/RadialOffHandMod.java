package github.devhrytsan.radialoffhand;

import com.mojang.blaze3d.platform.InputConstants;
import github.devhrytsan.radialoffhand.config.FileConfigHandler;
import github.devhrytsan.radialoffhand.config.RadialOffHandConfigScreen;
import github.devhrytsan.radialoffhand.menu.MenuController;
import github.devhrytsan.radialoffhand.menu.MenuScreen;
import github.devhrytsan.radialoffhand.menu.MenuMovementController;

import github.devhrytsan.radialoffhand.platform.Platform;
import github.devhrytsan.radialoffhand.utils.ClientPlayerUtils;
import github.devhrytsan.radialoffhand.utils.KeyInputUtils;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import github.devhrytsan.radialoffhand.platform.fabric.FabricPlatform;
import github.devhrytsan.radialoffhand.compatibility.FabricKeyHelperBridge;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

//?} neoforge {
/*import github.devhrytsan.radialoffhand.platform.neoforge.NeoforgePlatform;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import github.devhrytsan.radialoffhand.platform.neoforge.NeoforgeEntrypoint;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
		*///?} forge {
/*import github.devhrytsan.radialoffhand.platform.forge.ForgePlatform;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import github.devhrytsan.radialoffhand.platform.forge.ForgeEntrypoint;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
*///?}

@SuppressWarnings("LoggingSimilarMessage")
public class RadialOffHandMod {

	// General
	public static final String MOD_ID = /*$ mod_id*/ "radialoffhand";
	public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Radial Offhand";
	public static final Logger MAIN_LOGGER = LoggerFactory.getLogger(MOD_ID);
	// Keys
	public static KeyMapping OPEN_RADIAL_OFFHAND_MENU_KEY;
	public static KeyMapping SHOW_ALL_ITEMS_KEY; // Held while the menu is open to temporarily show every item

	//? if >=1.21.5 && neoforge {
	/*// NeoForge registers modded categories through RegisterKeyMappingsEvent instead
	public static final KeyMapping.Category KEYBIND_CATEGORY = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(RadialOffHandMod.MOD_ID, "general"));
	*///? } elif >=1.21.5 {
	public static final KeyMapping.Category KEYBIND_CATEGORY = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(RadialOffHandMod.MOD_ID, "general"));
	//? } else {

	/*public static final String KEYBIND_CATEGORY = "key.category." + MOD_ID + ".general";

	 *///? }

	public static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		MAIN_LOGGER.info("Initializing {} on {}", MOD_ID, RadialOffHandMod.xplat().loader());
		MAIN_LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		MAIN_LOGGER.info("Initializing {} Client on {}", MOD_ID, RadialOffHandMod.xplat().loader());
		MAIN_LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);

		FileConfigHandler.loadConfig();
	}

	//? fabric {
	public static void InitializeModFabric() {
		OPEN_RADIAL_OFFHAND_MENU_KEY = new KeyMapping(
				"key.category.radialoffhand.openkey",
				KeyInputUtils.KEYBOARD_TYPE,
				KeyInputUtils.DEFAULT_OPEN_MENU_KEY,
				KEYBIND_CATEGORY
		);

		SHOW_ALL_ITEMS_KEY = createShowAllItemsKey();

		FabricKeyHelperBridge.register(OPEN_RADIAL_OFFHAND_MENU_KEY);
		FabricKeyHelperBridge.register(SHOW_ALL_ITEMS_KEY);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			MenuController.INSTANCE.HandleUpdate(client);

			if (ClientPlayerUtils.getScreen(client) instanceof MenuScreen) {
				if(FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen) MenuMovementController.INSTANCE.handleMovement();
			}
		});
	}
	//?} neoforge {

	/*public static void RegisterKeysNeoForge(RegisterKeyMappingsEvent event) {
		OPEN_RADIAL_OFFHAND_MENU_KEY = new KeyMapping(
				"key.category.radialoffhand.openkey",
				KeyInputUtils.KEYBOARD_TYPE,
				KeyInputUtils.DEFAULT_OPEN_MENU_KEY,
				KEYBIND_CATEGORY
		);

		SHOW_ALL_ITEMS_KEY = createShowAllItemsKey();

		//? if >=1.21.5 {
		event.registerCategory(KEYBIND_CATEGORY);
		//? }
		event.register(OPEN_RADIAL_OFFHAND_MENU_KEY);
		event.register(SHOW_ALL_ITEMS_KEY);
	}

	public static void InitializeModNeoForge(FMLClientSetupEvent setupEvent) {

		Minecraft clientI = Minecraft.getInstance();

		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
			// Call your update logic here
			if (clientI.player != null) {
				MenuController.INSTANCE.HandleUpdate(clientI);
			}

			boolean moveInMenu = ClientPlayerUtils.getScreen(clientI) instanceof MenuScreen
					&& MenuScreen.INSTANCE.active
					&& FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen;

			// Runs every tick so the original key contexts are restored once the menu closes
			MenuMovementController.INSTANCE.updateMovementKeyContextNeoForge(moveInMenu);
			if (moveInMenu) {
				MenuMovementController.INSTANCE.handleMovement();
			}
		});

		NeoforgeEntrypoint.CONTAINER.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory) (client, parent) -> {
			return RadialOffHandConfigScreen.createConfigScreen(parent);
		});
	}

	*///?} forge {

	/*public static void RegisterKeysForge(RegisterKeyMappingsEvent event) {
	    OPEN_RADIAL_OFFHAND_MENU_KEY = new KeyMapping(
				"key.category.radialoffhand.openkey",
				KeyInputUtils.KEYBOARD_TYPE,
				KeyInputUtils.DEFAULT_OPEN_MENU_KEY,
				KEYBIND_CATEGORY
		);
		SHOW_ALL_ITEMS_KEY = createShowAllItemsKey();

		event.register(OPEN_RADIAL_OFFHAND_MENU_KEY);
		event.register(SHOW_ALL_ITEMS_KEY);
	}

	public static void InitializeModForge(FMLClientSetupEvent setupEvent) {

		Minecraft clientI = Minecraft.getInstance();

		MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
			if (event.phase == TickEvent.Phase.END) {
				MenuController.INSTANCE.HandleUpdate(clientI);

				boolean moveInMenu = ClientPlayerUtils.getScreen(clientI) instanceof MenuScreen
						&& MenuScreen.INSTANCE.active
						&& FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen;

				// Runs every tick so the original key contexts are restored once the menu closes
				MenuMovementController.INSTANCE.updateMovementKeyContextNeoForge(moveInMenu);
				if (moveInMenu) {
					MenuMovementController.INSTANCE.handleMovement();
				}
			}
		});

		ForgeEntrypoint.CONTEXT.registerExtensionPoint(
				ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> {
					return RadialOffHandConfigScreen.createConfigScreen(parent);
				})
		);
	}

	*///?}

	private static KeyMapping createShowAllItemsKey() {
		return new KeyMapping(
				"key.category.radialoffhand.showallitems",
				KeyInputUtils.KEYBOARD_TYPE,
				KeyInputUtils.DEFAULT_SHOW_ALL_ITEMS_KEY,
				KEYBIND_CATEGORY
		);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		 //?} neoforge {
		/*return new NeoforgePlatform();
		*///?} forge {
		/*return new ForgePlatform();
		 *///?}
	}
}
