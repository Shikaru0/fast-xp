package name.modid.client.config;

import name.modid.client.FastXpClient;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FastXpConfigScreen extends Screen {
    private static final int ENTRY_HEIGHT = 22;

    private final Screen parent;
    private boolean enabled;
    private int throwDelay;
    private Set<String> enabledProjectiles;
    private List<String> allProjectiles;
    private final List<Button> toggleButtons = new ArrayList<>();
    private int scrollOffset;
    private int maxVisible;
    private boolean changed;

    protected FastXpConfigScreen(Screen parent) {
        super(Component.literal("FastXP Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        enabled = FastXpClient.getConfig().isEnabled();
        throwDelay = FastXpClient.getConfig().getThrowDelay();

        List<String> saved = FastXpClient.getConfig().getProjectiles();
        enabledProjectiles = new HashSet<>(saved);
        changed = false;
        scrollOffset = 0;

        allProjectiles = discoverProjectiles();
        if (saved.isEmpty()) {
            enabledProjectiles.addAll(allProjectiles);
        }

        maxVisible = Math.max(1, (height - 170) / ENTRY_HEIGHT);
        if (maxVisible >= allProjectiles.size()) {
            maxVisible = allProjectiles.size();
        }

        int cx = width / 2;

        addRenderableWidget(Button.builder(
                toggleText("Enabled: ", enabled), button -> {
                    enabled = !enabled;
                    button.setMessage(toggleText("Enabled: ", enabled));
                    changed = true;
                }
        ).bounds(cx - 100, 30, 200, 20).build());

        addRenderableWidget(new AbstractSliderButton(cx - 100, 55, 200, 20,
                delayText(throwDelay), (throwDelay - 1) / 19.0) {
            @Override
            protected void updateMessage() {
                int val = (int) Math.round(value * 19) + 1;
                setMessage(delayText(val));
            }
            @Override
            protected void applyValue() {
                throwDelay = (int) Math.round(value * 19) + 1;
                changed = true;
            }
        });

        addRenderableWidget(Button.builder(
                Component.literal("Select All"), button -> {
                    enabledProjectiles.addAll(allProjectiles);
                    updateToggleMessages();
                    changed = true;
                }
        ).bounds(cx - 100, 80, 95, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Deselect All"), button -> {
                    enabledProjectiles.clear();
                    updateToggleMessages();
                    changed = true;
                }
        ).bounds(cx + 5, 80, 95, 20).build());

        buildToggleList();

        addRenderableWidget(Button.builder(
                CommonComponents.GUI_DONE, button -> saveAndClose()
        ).bounds(cx - 100, height - 28, 200, 20).build());
    }

    private void buildToggleList() {
        toggleButtons.forEach(this::removeWidget);
        toggleButtons.clear();
        int listY = 106;
        int end = Math.min(scrollOffset + maxVisible, allProjectiles.size());
        int cx = width / 2;
        for (int i = scrollOffset; i < end; i++) {
            String id = allProjectiles.get(i);
            boolean on = enabledProjectiles.contains(id);
            Button btn = addRenderableWidget(Button.builder(
                    entryText(id, on), button -> {
                        if (enabledProjectiles.contains(id)) {
                            enabledProjectiles.remove(id);
                        } else {
                            enabledProjectiles.add(id);
                        }
                        button.setMessage(entryText(id, enabledProjectiles.contains(id)));
                        changed = true;
                    }
            ).bounds(cx - 100, listY + (i - scrollOffset) * ENTRY_HEIGHT, 200, 20).build());
            toggleButtons.add(btn);
        }
    }

    private void updateToggleMessages() {
        for (Button btn : toggleButtons) {
            String id = allProjectiles.get(toggleButtons.indexOf(btn) + scrollOffset);
            btn.setMessage(entryText(id, enabledProjectiles.contains(id)));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0 && scrollOffset > 0) {
            scrollOffset--;
            buildToggleList();
            return true;
        }
        if (verticalAmount < 0 && scrollOffset + maxVisible < allProjectiles.size()) {
            scrollOffset++;
            buildToggleList();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void close() {
        if (changed) save();
        if (minecraft != null) minecraft.setScreenAndShow(parent);
    }

    private void saveAndClose() {
        save();
        if (minecraft != null) minecraft.setScreenAndShow(parent);
    }

    private void save() {
        FastXpConfig cfg = FastXpClient.getConfig();
        cfg.setEnabled(enabled);
        cfg.setThrowDelay(throwDelay);
        cfg.setProjectiles(new ArrayList<>(enabledProjectiles));
        cfg.save();
    }

    private static List<String> discoverProjectiles() {
        List<String> result = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof ProjectileItem) {
                Identifier id = BuiltInRegistries.ITEM.getKey(item);
                if (id != null) {
                    result.add(id.toString());
                }
            }
        }
        result.sort(String::compareTo);
        return result;
    }

    private static Component toggleText(String label, boolean value) {
        return Component.literal(label + (value ? "ON" : "OFF"));
    }

    private static Component delayText(int ticks) {
        return Component.literal("Delay: " + ticks + " tick" + (ticks != 1 ? "s" : ""));
    }

    private static Component entryText(String id, boolean on) {
        String name = id.replace("minecraft:", "");
        return Component.literal((on ? "§a✓ " : "§7  ") + name);
    }
}