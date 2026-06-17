package name.modid.client.config;

import name.modid.client.FastXpClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FastXPConfigScreen extends Screen {
    private static final int ENTRY_HEIGHT = 22;

    private final Screen parent;
    private boolean enabled;
    private int throwDelay;
    private Set<String> enabledProjectiles;
    private List<String> allProjectiles;
    private final List<ButtonWidget> toggleButtons = new ArrayList<>();
    private int scrollOffset;
    private int maxVisible;
    private boolean changed;

    protected FastXPConfigScreen(Screen parent) {
        super(Text.literal("FastXP Config"));
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

        addDrawableChild(ButtonWidget.builder(
            toggleText("Enabled: ", enabled), button -> {
                enabled = !enabled;
                button.setMessage(toggleText("Enabled: ", enabled));
                changed = true;
            }
        ).dimensions(cx - 100, 30, 200, 20).build());

        addDrawableChild(new SliderWidget(cx - 100, 55, 200, 20,
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

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Select All"), button -> {
                enabledProjectiles.addAll(allProjectiles);
                updateToggleMessages();
                changed = true;
            }
        ).dimensions(cx - 100, 80, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Deselect All"), button -> {
                enabledProjectiles.clear();
                updateToggleMessages();
                changed = true;
            }
        ).dimensions(cx + 5, 80, 95, 20).build());

        buildToggleList();

        addDrawableChild(ButtonWidget.builder(
            ScreenTexts.DONE, button -> saveAndClose()
        ).dimensions(cx - 100, height - 28, 200, 20).build());
    }

    private void buildToggleList() {
        toggleButtons.forEach(this::remove);
        toggleButtons.clear();
        int listY = 106;
        int end = Math.min(scrollOffset + maxVisible, allProjectiles.size());
        int cx = width / 2;
        for (int i = scrollOffset; i < end; i++) {
            String id = allProjectiles.get(i);
            boolean on = enabledProjectiles.contains(id);
            ButtonWidget btn = addDrawableChild(ButtonWidget.builder(
                entryText(id, on), button -> {
                    if (enabledProjectiles.contains(id)) {
                        enabledProjectiles.remove(id);
                    } else {
                        enabledProjectiles.add(id);
                    }
                    button.setMessage(entryText(id, enabledProjectiles.contains(id)));
                    changed = true;
                }
            ).dimensions(cx - 100, listY + (i - scrollOffset) * ENTRY_HEIGHT, 200, 20).build());
            toggleButtons.add(btn);
        }
    }

    private void updateToggleMessages() {
        for (ButtonWidget btn : toggleButtons) {
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

    @Override
    public void close() {
        if (changed) save();
        if (client != null) client.setScreen(parent);
    }

    private void saveAndClose() {
        save();
        if (client != null) client.setScreen(parent);
    }

    private void save() {
        FastXPConfig cfg = FastXpClient.getConfig();
        cfg.setEnabled(enabled);
        cfg.setThrowDelay(throwDelay);
        cfg.setProjectiles(new ArrayList<>(enabledProjectiles));
        cfg.save();
    }

    private static List<String> discoverProjectiles() {
        List<String> result = new ArrayList<>();
        for (Item item : Registries.ITEM) {
            if (item instanceof ProjectileItem) {
                Identifier id = Registries.ITEM.getId(item);
                if (id != null) {
                    result.add(id.toString());
                }
            }
        }
        result.sort(String::compareTo);
        return result;
    }

    private static Text toggleText(String label, boolean value) {
        return Text.literal(label + (value ? "ON" : "OFF"));
    }

    private static Text delayText(int ticks) {
        return Text.literal("Delay: " + ticks + " tick" + (ticks != 1 ? "s" : ""));
    }

    private static Text entryText(String id, boolean on) {
        String name = id.replace("minecraft:", "");
        return Text.literal((on ? "§a✓ " : "§7  ") + name);
    }
}
