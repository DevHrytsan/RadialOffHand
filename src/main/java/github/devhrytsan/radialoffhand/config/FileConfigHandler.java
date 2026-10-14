package github.devhrytsan.radialoffhand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import github.devhrytsan.radialoffhand.Constants;
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
                RadialOffHandConfig loaded = baseGson.fromJson(reader, RadialOffHandConfig.class);
                if (loaded != null) {
                    CONFIG_INSTANCE = loaded;
                } else {
                    // Empty file. Defaults are used and the file is left untouched
                    RadialOffHandMod.MAIN_LOGGER.warn("Radial Off-Hand config is empty, using defaults.");
                }
            } catch (IOException | JsonParseException e) {
                // Broken JSON. Defaults are used and the file is left untouched so it can be fixed by hand
                RadialOffHandMod.MAIN_LOGGER.error("Could not load Radial Off-Hand config, using defaults!", e);
            }
            CONFIG_INSTANCE.scaleFactor = Math.max(Constants.MIN_SCALE_FACTOR, Math.min(Constants.MAX_SCALE_FACTOR, CONFIG_INSTANCE.scaleFactor));
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
