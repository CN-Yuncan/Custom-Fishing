package net.momirealms.customfishing.gui.page.property;

import net.momirealms.customfishing.adventure.AdventureManagerImpl;
import net.momirealms.customfishing.adventure.component.ShadedAdventureComponentWrapper;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import net.momirealms.customfishing.gui.YamlPage;
import net.momirealms.customfishing.gui.icon.BackGroundItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.AnvilWindow;

import java.util.ArrayList;
import java.util.List;

public class ItemFlagEditor {

    private final Player player;
    private final YamlPage parentPage;
    private final List<String> flags;
    private final ConfigurationSection section;

    public ItemFlagEditor(Player player, YamlPage parentPage, ConfigurationSection section) {
        this.player = player;
        this.parentPage = parentPage;
        this.section = section;
        int index = 0;
        this.flags = section.getStringList("item-flags");
        reOpen();
    }

    public void reOpen() {
        Gui upperGui = Gui.normal()
                .setStructure(
                        "# a #"
                )
                .addIngredient('a', new ItemBuilder(CustomFishingPlugin.get().getItemManager().getItemBuilder(section, "item", "id").build(player)))
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.AIR)))
                .build();

        var gui = PagedGui.items()
                .setStructure(
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "# # # # c # # # #"
                )
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('c', parentPage.getBackItem())
                .addIngredient('#', new BackGroundItem())
                .setContent(getContents())
                .build();

        var window = AnvilWindow.split()
                .setViewer(player)
                .setTitle(new ShadedAdventureComponentWrapper(
                        AdventureManagerImpl.getInstance().getComponentFromMiniMessage("Edit Item Flag")
                ))
                .setUpperGui(upperGui)
                .setLowerGui(gui)
                .build();

        window.open();
    }

    public List<Item> getContents() {
        ArrayList<Item> items = new ArrayList<>();
        for (ItemFlag itemFlag : ItemFlag.values()) {
            items.add(new ItemFlagToggleItem(itemFlag.name()));
        }
        return items;
    }

    public class ItemFlagToggleItem extends AbstractItem {

        private final String flag;

        public ItemFlagToggleItem(String flag) {
            this.flag = flag;
        }

        @Override
        public ItemProvider getItemProvider() {
            if (flags.contains(flag)) {
                return new ItemBuilder(Material.GREEN_BANNER).setDisplayName(new ShadedAdventureComponentWrapper(AdventureManagerImpl.getInstance().getComponentFromMiniMessage(
                        "<green>" + flag
                )));
            } else {
                return new ItemBuilder(Material.RED_BANNER).setDisplayName(new ShadedAdventureComponentWrapper(AdventureManagerImpl.getInstance().getComponentFromMiniMessage(
                        "<red>" + flag
                )));
            }
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            if (flags.contains(flag)) {
                flags.remove(flag);
            } else {
                flags.add(flag);
            }
            if (flags.size() != 0) {
                section.set("item-flags", flags);
            } else {
                section.set("item-flags", null);
            }
            parentPage.save();
            reOpen();
        }
    }
}
