package name.modid.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FastXPConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("fastxp.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean enabled = true;
    private int throwDelay = 2;
    private List<String> projectiles = new ArrayList<>();

    public void load() {
        if (!CONFIG_PATH.toFile().exists()) {
            save();
            return;
        }

        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            if (json.has("enabled"))
                enabled = json.get("enabled").getAsBoolean();
            if (json.has("throwDelay"))
                throwDelay = json.get("throwDelay").getAsInt();
            if (json.has("projectiles")) {
                JsonArray arr = json.getAsJsonArray("projectiles");
                projectiles = new ArrayList<>();
                for (JsonElement e : arr) {
                    projectiles.add(e.getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        JsonObject json = new JsonObject();
        json.addProperty("enabled", enabled);
        json.addProperty("throwDelay", throwDelay);

        JsonArray arr = new JsonArray();
        for (String p : projectiles) {
            arr.add(p);
        }
        json.add("projectiles", arr);

        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; save(); }
    public int getThrowDelay() { return Math.max(1, throwDelay); }
    public void setThrowDelay(int delay) { this.throwDelay = delay; }
    public List<String> getProjectiles() { return projectiles; }
    public void setProjectiles(List<String> list) { this.projectiles = list; }
}
