package github.devhrytsan.radialoffhand;

import com.mojang.blaze3d.platform.InputConstants;
import github.devhrytsan.radialoffhand.config.FileConfigHandler;
import github.devhrytsan.radialoffhand.config.RadialOffHandConfigScreen;
import github.devhrytsan.radialoffhand.menu.MenuController;
import github.devhrytsan.radialoffhand.menu.MenuScreen;
import github.devhrytsan.radialoffhand.menu.MenuMovementController;

import github.devhrytsan.radialoffhand.platform.Platform;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import github.devhrytsan.radialoffhand.platform.fabric.FabricPlatform;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

//?} neoforge {
/*import github.devhrytsan.radialoffhand.platform.neoforge.NeoforgePlatform;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
		*///?} forge {
/*import github.devhrytsan.radialoffhand.platform.forge.ForgePlatform;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
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

	//? if >=1.21.5 {
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
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Z,
				KEYBIND_CATEGORY
		);

		KeyBindingHelper.registerKeyBinding(OPEN_RADIAL_OFFHAND_MENU_KEY);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			MenuController.INSTANCE.HandleUpdate(client);

			if (client.screen instanceof MenuScreen) {
				if(FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen) MenuMovementController.INSTANCE.handleMovement();
			}
		});
	}
	//?} neoforge {

	/*public static void RegisterKeysNeoForge(RegisterKeyMappingsEvent event) {
		OPEN_RADIAL_OFFHAND_MENU_KEY = new KeyMapping(
				"key.category.radialoffhand.openkey",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Z,
				KEYBIND_CATEGORY
		);

		event.register(OPEN_RADIAL_OFFHAND_MENU_KEY);
	}

	public static void InitializeModNeoForge(FMLClientSetupEvent setupEvent) {

		Minecraft clientI = Minecraft.getInstance();

		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
			// Call your update logic here
			if (clientI.player != null) {
				MenuController.INSTANCE.HandleUpdate(clientI);
			}

			if (clientI.screen instanceof MenuScreen) {
				if (FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen) {
					boolean isMenuActive = MenuScreen.INSTANCE.active;
					MenuMovementController.INSTANCE.updateMovementKeyContextNeoForge(isMenuActive);
					MenuMovementController.INSTANCE.handleMovement();
				}
			}
		});

		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> {
			return RadialOffHandConfigScreen.createConfigScreen(parent);
		}); //TODO: Rewrite that because of deprecated API.
	}

	*///?} forge {

	/*public static void RegisterKeysForge(RegisterKeyMappingsEvent event) {
	    OPEN_RADIAL_OFFHAND_MENU_KEY = new KeyMapping(
				"key.category.radialoffhand.openkey",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Z,
				KEYBIND_CATEGORY
		);
		event.register(OPEN_RADIAL_OFFHAND_MENU_KEY);
	}

	public static void InitializeModForge(FMLClientSetupEvent setupEvent) {

		Minecraft clientI = Minecraft.getInstance();

		MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
			if (event.phase == TickEvent.Phase.END) {
				MenuController.INSTANCE.HandleUpdate(clientI);

				if (clientI.screen instanceof MenuScreen) {
				if (FileConfigHandler.CONFIG_INSTANCE.allowMovementWhileOpen) {
					boolean isMenuActive = MenuScreen.INSTANCE.active;
					MenuMovementController.INSTANCE.updateMovementKeyContextNeoForge(isMenuActive);
					MenuMovementController.INSTANCE.handleMovement();
				}
			}
			}
		});

		ModLoadingContext.get().registerExtensionPoint(
				ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> {
					return RadialOffHandConfigScreen.createConfigScreen(parent);
				})
		); //TODO: Rewrite that because of deprecated API.
	}

	*///?}

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
