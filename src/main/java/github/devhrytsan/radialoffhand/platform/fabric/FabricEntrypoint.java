package github.devhrytsan.radialoffhand.platform.fabric;

//? fabric {

import github.devhrytsan.radialoffhand.RadialOffHandMod;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		RadialOffHandMod.onInitialize();
	}
}
//?}
