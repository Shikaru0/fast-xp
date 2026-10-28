package name.modid.client.input;

import name.modid.client.FastXpClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.component.SwingAnimation;
import org.lwjgl.sdl.SDLMouse;

import java.util.List;

public class FastXpInputHandler {
    private int tickCounter = 0;

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FastXpClient.getConfig().isEnabled() || FastXpClient.isServerDisabled()) return;
            if (client.player == null || client.gameMode == null) return;

            long window = client.getWindow().handle();
            if (window == 0L) return;

            int buttons = SDLMouse.SDL_GetMouseState(null, null);
            boolean isHeld = (buttons & (1 << (3-1))) != 0; // third button
            //boolean isHeld = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_TRUE;

            if (!isHeld) {
                tickCounter = 0;
                return;
            }

            if (tickCounter < FastXpClient.getConfig().getThrowDelay()) {
                tickCounter++;
                return;
            }
            tickCounter = 0;

            LocalPlayer player = client.player;
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            boolean mainIsProjectile = isProjectile(mainHand);
            boolean offIsProjectile = isProjectile(offHand);

            if (mainIsProjectile) {
                client.gameMode.useItem(player, InteractionHand.MAIN_HAND);
                player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
            } else if (offIsProjectile) {
                client.gameMode.useItem(player, InteractionHand.OFF_HAND);
                player.swing(InteractionHand.OFF_HAND, SwingAnimation.DEFAULT, false);
            }
        });
    }

    private boolean isProjectile(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof ProjectileItem)) return false;
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem()); //Registries.ITEM.getId(stack.getItem());
        List<String> enabled = FastXpClient.getConfig().getProjectiles();
        return enabled.isEmpty() || enabled.contains(id.toString());
    }
}