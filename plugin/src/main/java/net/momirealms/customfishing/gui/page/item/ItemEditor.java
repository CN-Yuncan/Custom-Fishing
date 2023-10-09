package net.momirealms.customfishing.gui.page.item;

import net.momirealms.customfishing.adventure.AdventureManagerImpl;
import net.momirealms.customfishing.adventure.component.ShadedAdventureComponentWrapper;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import net.momirealms.customfishing.gui.ItemPage;
import net.momirealms.customfishing.gui.icon.BackGroundItem;
import net.momirealms.customfishing.gui.icon.BackToPageItem;
import net.momirealms.customfishing.gui.icon.NextPageItem;
import net.momirealms.customfishing.gui.icon.PreviousPageItem;
import net.momirealms.customfishing.gui.icon.property.CMDItem;
import net.momirealms.customfishing.gui.icon.property.DisplayNameItem;
import net.momirealms.customfishing.gui.icon.property.LoreItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
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

public class ItemEditor implements ItemPage {

    private final Player player;
    private final ItemSelector itemSelector;
    private final ConfigurationSection section;
    private final String key;

    public ItemEditor(Player player, String key, ItemSelector itemSelector, ConfigurationSection section) {
        this.player = player;
        this.section = section;
        this.itemSelector = itemSelector;
        this.key = key;
        this.reOpen();
    }

    @Override
    public void reOpen() {
        Item border = new SimpleItem(new ItemBuilder(Material.AIR));
        Gui upperGui = Gui.normal()
                .setStructure(
                        "# a #"
                )
                .addIngredient('a', new RefreshExample())
                .addIngredient('#', border)
                .build();

        var gui = PagedGui.items()
                .setStructure(
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "# # a # c # b # #"
                )
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('#', new BackGroundItem())
                .addIngredient('a', new PreviousPageItem())
                .addIngredient('b', new NextPageItem())
                .addIngredient('c', new BackToPageItem(itemSelector))
                .setContent(getItemList())
                .build();

        var window = AnvilWindow.split()
                .setViewer(player)
                .setTitle(new ShadedAdventureComponentWrapper(
                        AdventureManagerImpl.getInstance().getComponentFromMiniMessage("Editing " + key)
                ))
                .setUpperGui(upperGui)
                .setLowerGui(gui)
                .build();

        window.open();
    }

    @Override
    public void save() {
        itemSelector.save();
    }

    public class RefreshExample extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            return new ItemBuilder(CustomFishingPlugin.get().getItemManager().getItemBuilder(section, "item", key).build(player));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            notifyWindows();
        }
    }

    public List<Item> getItemList() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new DisplayNameItem(this));
        items.add(new LoreItem(this));
        items.add(new CMDItem(this));
        return items;
    }

    @Override
    public ConfigurationSection getSection() {
        return section;
    }
}
