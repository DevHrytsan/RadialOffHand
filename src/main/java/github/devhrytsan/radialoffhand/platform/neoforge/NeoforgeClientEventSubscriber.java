package github.devhrytsan.radialoffhand.platform.neoforge;

//? neoforge {

/*import github.devhrytsan.radialoffhand.RadialOffHandMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = RadialOffHandMod.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		RadialOffHandMod.onInitializeClient();
		RadialOffHandMod.InitializeModNeoForge(event);
	}

	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		RadialOffHandMod.RegisterKeysNeoForge(event);
	}
}
*///?}
