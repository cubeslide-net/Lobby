package net.cubeslide.lobbysystem.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {


    private final ItemMeta itemMeta;
    private final ItemStack itemStack;

    public ItemBuilder(Material mat, int count) {
        itemStack = new ItemStack(mat, count);
        itemStack.setAmount(count);
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

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}