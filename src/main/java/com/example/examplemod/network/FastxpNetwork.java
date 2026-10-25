package com.example.examplemod.network;

import com.example.examplemod.Fastxp;
import com.example.examplemod.FastxpClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class FastxpNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                JoinPayload.TYPE,
                net.minecraft.network.codec.StreamCodec.unit(new JoinPayload()),
                (payload, context) -> {
                    // No message
                }
        );

        registrar.playToClient(
                OptOutPayload.TYPE,
                OptOutPayload.CODEC,
                ((optOutPayload, iPayloadContext) -> {
                    Minecraft minecraft = Minecraft.getInstance();

                    switch (optOutPayload.action()) {
                        case 0 -> {
                            FastxpClient.setServerDisabled(true);
                            if (optOutPayload.message() != null && !optOutPayload.message().isEmpty() && iPayloadContext.player() != null) {
                                iPayloadContext.player().displayClientMessage(Component.literal(optOutPayload.message()), false);
                            }
                        }
                        case 1 -> {
                            var connection = minecraft.getConnection();
                            if (connection != null) {
                                Component reason = optOutPayload.message() != null && !optOutPayload.message().isEmpty()
                                        ? Component.literal(optOutPayload.message())
                                        : Component.literal("Disconnected by server");

                                connection.getConnection().disconnect(reason);
                            }
                        }
                    }
                })
        );
    }

    public record JoinPayload() implements CustomPacketPayload {
        public static final Type<JoinPayload> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath("fastxp", "join"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record OptOutPayload(int action, String message) implements CustomPacketPayload {
        public static final Type<OptOutPayload> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath("fastxp", "opt_out"));

        public static final StreamCodec<RegistryFriendlyByteBuf, OptOutPayload> CODEC =
                StreamCodec.of(
                        (buffer, value) -> {
                            buffer.writeVarInt(value.action());
                            buffer.writeUtf(value.message() != null ? value.message() : "");
                        },
                        buffer -> new OptOutPayload(
                                buffer.readVarInt(),
                                buffer.readUtf(32767)
                        )
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
