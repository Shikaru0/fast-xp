package name.modid.client;

import name.modid.client.config.FastXpConfig;
import name.modid.client.input.FastXpInputHandler;
import name.modid.client.network.FastXpNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FastXpClient implements ClientModInitializer {
	private static FastXpConfig config;
	private static boolean serverDisabled = false;

	public static FastXpConfig getConfig() {
		return config;
	}

	public static void setServerDisabled(boolean disabled) {
		serverDisabled = disabled;
	}

	public static boolean isServerDisabled() {
		return serverDisabled;
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		config = new FastXpConfig();
		config.load();

		FastXpNetwork network = new FastXpNetwork();
		network.register();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			serverDisabled = false;
			ClientPlayNetworking.send(new FastXpNetwork.JoinPayload());
		});

		FastXpInputHandler inputHandler = new FastXpInputHandler();
		inputHandler.register();
	}
}