package com.shdwlf.soundboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class soundboard extends JavaPlugin implements Listener {

    ArrayList<Sound> sounds = new ArrayList<>();

    //Screenshot: http://i.imgur.com/4Uf80GL.png

	@Override
	public void onEnable() {
        sounds.addAll(Arrays.asList(Sound.values()));

        Collections.sort(sounds, new Comparator<Sound>() {
            public int compare(Sound v1, Sound v2) {
                return v1.toString().compareTo(v2.toString());
            }
        });

        Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {	

	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("soundboard")) {
            //Open soundboard menu
            openInventory((Player) sender, 1);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getInventory().getName().split(" ")[0].equalsIgnoreCase("soundboard") && event.getCurrentItem() != null) {
            event.setCancelled(true);
            ItemStack is = event.getCurrentItem();
            if(is.getType().equals(Material.ARROW)) {
               if(is.getItemMeta().getDisplayName().contains("Next Page")) {
                   int page = Integer.parseInt(event.getInventory().getName().split("#")[1]);
                   openInventory((Player) event.getWhoClicked(), Integer.min(sounds.size() / 45 + 1, page + 1));
               }else if(is.getItemMeta().getDisplayName().contains("Previous Page")) {
                   int page = Integer.parseInt(event.getInventory().getName().split("#")[1]);
                   openInventory((Player) event.getWhoClicked(), Integer.max(1, page-1));
               }
            }else {
                Player player = (Player) event.getWhoClicked();
                player.playSound(player.getLocation(), Sound.valueOf(is.getItemMeta().getDisplayName().split(ChatColor.AQUA.toString())[1]), 1, 1);
                event.setCancelled(true);
            }
        }
    }

    /**
     * @param page Page to open, 1 = default page
     */
    private void openInventory(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Soundboard Page #" + page);
        addGUIElements(inventory);

        ItemStack is = new ItemStack(Material.COAL_BLOCK);
        ItemMeta im = is.getItemMeta();
        for(int i = 45 * (page - 1); i < 45 * page && i >= 0 && i < sounds.size(); i++) {
            im.setDisplayName(ChatColor.GRAY + "Play Sound: " + ChatColor.AQUA + sounds.get(i).toString());
            is.setItemMeta(im);

            inventory.setItem(i % 45, is);
        }

        player.openInventory(inventory);
    }

    private void addGUIElements(Inventory inventory) {
        ItemStack is = new ItemStack(Material.ARROW);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Next Page");
        is.setItemMeta(im);
        inventory.setItem(50, is);

        im.setDisplayName(ChatColor.GOLD + "Previous Page");
        is.setItemMeta(im);
        inventory.setItem(48, is);
    }
}