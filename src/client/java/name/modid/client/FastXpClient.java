package name.modid.client;

import name.modid.client.network.FastXpNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FastXpClient implements ClientModInitializer {
	private static boolean serverDisabled = false;

	public static void setServerDisabled(boolean disabled) {
		serverDisabled = disabled;
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		FastXpNetwork network = new FastXpNetwork();
		network.register();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			serverDisabled = false;
			ClientPlayNetworking.send(new FastXpNetwork.JoinPayload());
		});
	}
}