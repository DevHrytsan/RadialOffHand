package github.devhrytsan.radialoffhand.platform.fabric;
//? fabric {
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import github.devhrytsan.radialoffhand.config.RadialOffHandConfigScreen;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RadialOffHandConfigScreen::createConfigScreen;
    }
}
//?}
