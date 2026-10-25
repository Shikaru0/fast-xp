package com.example.examplemod.input;

import com.example.examplemod.Fastxp;
import com.example.examplemod.FastxpClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@EventBusSubscriber(modid = Fastxp.MODID, value = Dist.CLIENT)
public class FastxpInputHandler {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (!FastxpClient.getConfig().isEnabled() || FastxpClient.isServerDisabled()) {
            tickCounter = 0;
            return;
        }

        if (mc.player == null || mc.gameMode == null || mc.screen != null) {
            tickCounter = 0;
            return;
        }

        long window = mc.getWindow().handle();
        if (window == 0L) return;

        boolean isHeld = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_TRUE;

        if (!isHeld) {
            tickCounter = 0;
            return;
        }

        if (tickCounter < FastxpClient.getConfig().getThrowDelay()) {
            tickCounter++;
            return;
        }
        tickCounter = 0;

        var player = mc.player;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();

        if (isProjectile(mainHand)) {
            mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            player.swing(InteractionHand.MAIN_HAND);
        } else if (isProjectile(offhand)) {
            mc.gameMode.useItem(player, InteractionHand.OFF_HAND);
            player.swing(InteractionHand.OFF_HAND);
        }
    }

    private static boolean isProjectile(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof ProjectileItem)) return false;

        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) return false;

        List<String> enabled = FastxpClient.getConfig().getProjectiles();
        return enabled.isEmpty() || enabled.contains(id.toString());
    }
}
