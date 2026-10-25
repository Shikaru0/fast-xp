package com.example.examplemod;

import com.example.examplemod.network.FastxpNetwork;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(Fastxp.MODID)
public class Fastxp {
    public static final String MODID = "fastxp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Fastxp(IEventBus modEventBus, ModContainer modContainer) {
        // Register the FastXP network payloads (JoinPayload + OptOutPayload).
        modEventBus.addListener(FastxpNetwork::register);

        // Register ourselves for server events we care about.
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("server starting");
    }
}