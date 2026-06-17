package name.modid.client.network;

import name.modid.client.FastXpClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FastXPNetwork {

    public void register() {
        PayloadTypeRegistry.playC2S().register(JoinPayload.ID, JoinPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OptOutPayload.ID, OptOutPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(OptOutPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                switch (payload.action()) {
                    case 0 -> {
                        FastXpClient.setServerDisabled(true);
                        if (payload.message() != null && !payload.message().isEmpty() && context.player() != null) {
                            context.player().sendMessage(Text.literal(payload.message()), false);
                        }
                    }
                    case 1 -> {
                        if (context.player() != null && context.player().networkHandler != null) {
                            Text reason = payload.message() != null && !payload.message().isEmpty()
                                ? Text.literal(payload.message())
                                : Text.literal("Disconnected by server");
                            context.player().networkHandler.getConnection().disconnect(reason);
                        }
                    }
                }
            });
        });
    }

    public record JoinPayload() implements CustomPayload {
        public static final CustomPayload.Id<JoinPayload> ID =
            new CustomPayload.Id<>(Identifier.of("fastxp", "join"));

        public static final PacketCodec<RegistryByteBuf, JoinPayload> CODEC = new PacketCodec<>() {
            @Override
            public void encode(RegistryByteBuf buffer, JoinPayload value) {
            }

            @Override
            public JoinPayload decode(RegistryByteBuf buffer) {
                return new JoinPayload();
            }
        };

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record OptOutPayload(int action, String message) implements CustomPayload {
        public static final CustomPayload.Id<OptOutPayload> ID =
            new CustomPayload.Id<>(Identifier.of("fastxp", "opt_out"));

        public static final PacketCodec<RegistryByteBuf, OptOutPayload> CODEC = new PacketCodec<>() {
            @Override
            public void encode(RegistryByteBuf buffer, OptOutPayload value) {
                buffer.writeVarInt(value.action);
                buffer.writeString(value.message != null ? value.message : "");
            }

            @Override
            public OptOutPayload decode(RegistryByteBuf buffer) {
                return new OptOutPayload(buffer.readVarInt(), buffer.readString(32767));
            }
        };

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
