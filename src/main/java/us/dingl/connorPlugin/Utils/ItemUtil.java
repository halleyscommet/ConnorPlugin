package us.dingl.connorPlugin.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating custom items in the game.
 */
public class ItemUtil {

    /**
     * Creates and returns a custom bow item with specific properties.
     *
     * @return The custom bow item.
     */
    public ItemStack giveBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        List<Component> lore = new ArrayList<>();

        // Custom name
        Component customName = Component.text("§e§kXIX§r §6Splintered Soulbow §e§kXIX§r");
        meta.displayName(customName);

        // Lore
        lore.add(Component.text("§b§oA Starfall Blessed Weapon."));
        lore.add(Component.text(""));
        lore.add(Component.text("§r§3A recurve longbow that needs"));
        lore.add(Component.text("§r§3no drawstring to fire. This"));
        lore.add(Component.text("§r§3blessed weapon shoots arcane"));
        lore.add(Component.text("§r§3magic at the speed of light."));
        meta.lore(lore);

        // Enchantment glint override
        meta.setCustomModelData(1);
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        bow.setItemMeta(meta);

        return bow;
    }

    /**
     * Creates and returns a custom sword item with specific properties.
     *
     * @return The custom sword item.
     */
    public ItemStack giveSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        List<Component> lore = new ArrayList<>();

        // Custom name
        Component customName = Component.text("§c§kXIX§r §4Murasama §c§kXIX§r");
        meta.displayName(customName);

        // Lore
        lore.add(Component.text("§b§oA Starfall Blessed Weapon."));
        lore.add(Component.text(""));
        lore.add(Component.text("§r§cA carmine Vesselite blade."));
        lore.add(Component.text("§r§cBy transferring kinetic energy"));
        lore.add(Component.text("§r§cfrom the blade into its hilt,"));
        lore.add(Component.text("§r§cpowerful arcs of magic can"));
        lore.add(Component.text("§r§cbe unleashed towards foes."));
        meta.lore(lore);

        // Enchantment glint override
        meta.setCustomModelData(1);
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.SHARPNESS, 8, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        sword.setItemMeta(meta);

        return sword;
    }

    /**
     * Gets the item details as a Component for hover event.
     *
     * @param item The item stack.
     * @return The item details as a Component.
     */
    public Component getItemDetails(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        Component itemName = meta.displayName();
        List<Component> lore = meta.lore();

        if (itemName == null) {
            itemName = Component.text("Unknown Item");
        }

        TextComponent.Builder details = Component.text()
                .append(itemName)
                .append(Component.text("\n"));

        if (lore != null) {
            for (Component line : lore) {
                details.append(line).append(Component.text("\n"));
            }
        }

        return details.build();
    }
}