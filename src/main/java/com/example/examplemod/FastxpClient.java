package com.example.examplemod;

import com.example.examplemod.config.FastxpConfig;
import com.example.examplemod.config.ModMenuIntegration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Fastxp.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Fastxp.MODID, value = Dist.CLIENT)
public class FastxpClient {
    private static boolean serverDisabled = false;
    private static FastxpConfig config = new FastxpConfig();

    public static FastxpConfig getConfig() {
        return config;
    }

    public static boolean isServerDisabled() {
        return serverDisabled;
    }

    public static void setServerDisabled(boolean disabled) {
        serverDisabled = disabled;
    }

    public FastxpClient(ModContainer container) {
        config.load();

        // Allows NeoForge to create a config screen for this mod's configs.
        container.registerExtensionPoint(IConfigScreenFactory.class, new ModMenuIntegration());
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        //Fastxp.LOGGER.info("HELLO FROM CLIENT SETUP");
        //Fastxp.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}