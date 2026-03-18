package github.devhrytsan.radialoffhand.platform.fabric;

//? fabric {

import github.devhrytsan.radialoffhand.RadialOffHandMod;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		RadialOffHandMod.onInitializeClient();
		RadialOffHandMod.InitializeModFabric();
	}

}
//?}
