package name.modid.client;

import name.modid.client.config.FastXPConfig;
import name.modid.client.input.FastXPInputHandler;
import name.modid.client.network.FastXPNetwork;
import name.modid.client.network.FastXPNetwork.JoinPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FastXpClient implements ClientModInitializer {
    private static FastXPConfig config;
    private static boolean serverDisabled = false;

    public static FastXPConfig getConfig() {
        return config;
    }

    public static boolean isServerDisabled() {
        return serverDisabled;
    }

    public static void setServerDisabled(boolean disabled) {
        serverDisabled = disabled;
    }

    @Override
    public void onInitializeClient() {
        config = new FastXPConfig();
        config.load();

        FastXPNetwork network = new FastXPNetwork();
        network.register();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            serverDisabled = false;
            ClientPlayNetworking.send(new JoinPayload());
        });

        FastXPInputHandler inputHandler = new FastXPInputHandler();
        inputHandler.register();
    }
}
