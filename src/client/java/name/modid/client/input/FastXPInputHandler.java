package name.modid.client.input;

import name.modid.client.FastXpClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.List;

public class FastXPInputHandler {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private int tickCounter = 0;

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FastXpClient.getConfig().isEnabled() || FastXpClient.isServerDisabled()) return;
            if (client.player == null || client.interactionManager == null) return;

            long window = client.getWindow().getHandle();
            if (window == 0L) return;

            boolean isHeld = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_TRUE;

            if (!isHeld) {
                tickCounter = 0;
                return;
            }

            if (tickCounter < FastXpClient.getConfig().getThrowDelay()) {
                tickCounter++;
                return;
            }
            tickCounter = 0;

            ClientPlayerEntity player = client.player;
            ItemStack mainHand = player.getMainHandStack();
            ItemStack offHand = player.getOffHandStack();

            boolean mainIsProjectile = isProjectile(mainHand);
            boolean offIsProjectile = isProjectile(offHand);

            if (mainIsProjectile) {
                client.interactionManager.interactItem(player, Hand.MAIN_HAND);
                player.swingHand(Hand.MAIN_HAND);
            } else if (offIsProjectile) {
                client.interactionManager.interactItem(player, Hand.OFF_HAND);
                player.swingHand(Hand.OFF_HAND);
            }
        });
    }

    private boolean isProjectile(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof ProjectileItem)) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id == null) return false;
        List<String> enabled = FastXpClient.getConfig().getProjectiles();
        return enabled.isEmpty() || enabled.contains(id.toString());
    }
}
