package github.devhrytsan.radialoffhand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.devhrytsan.radialoffhand.RadialOffHandMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileConfigHandler {
    public static RadialOffHandConfig CONFIG_INSTANCE = new RadialOffHandConfig();
	protected static File settingsFile = RadialOffHandMod.PLATFORM.getConfigDirectory().resolve("radialoffhand.json").toFile();
    protected static Gson baseGson = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        if (settingsFile.exists()) {
            try (FileReader reader = new FileReader(settingsFile)) {
                CONFIG_INSTANCE = baseGson.fromJson(reader, RadialOffHandConfig.class);
            } catch (IOException e) {
                RadialOffHandMod.MAIN_LOGGER.error("Could not load Radial Off-Hand config!", e);
            }
        } else {
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(settingsFile)) {
            baseGson.toJson(CONFIG_INSTANCE, writer);
        } catch (IOException e) {
			RadialOffHandMod.MAIN_LOGGER.error("Could not save Radial Off-Hand config!", e);
        }
    }

}
