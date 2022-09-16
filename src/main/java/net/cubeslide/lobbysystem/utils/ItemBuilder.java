package net.cubeslide.lobbysystem.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {


    private final ItemMeta itemMeta;
    private final ItemStack itemStack;

    public ItemBuilder(Material mat) {
        itemStack = new ItemStack(mat);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayname(String s) {
        itemMeta.setDisplayName(s);
        return this;
    }

    public ItemBuilder setLore(List<String> s) {
        itemMeta.setLore(s);
        return this;
    }
    @Override
    public String toString() {
        return "ItemBuilder{" + "itemMeta=" + itemMeta + ", itemStack=" + itemStack + '}';
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}