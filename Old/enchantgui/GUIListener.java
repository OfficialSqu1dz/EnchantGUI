package dk.dennist.enchantgui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class GUIListener implements Listener {
    ArrayList<String> tableClick = new ArrayList<String>();
    JavaPlugin plugin;
    Main main;

    GUIListener(JavaPlugin plugin, Main main) {
        this.main = main;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInvClickEvent(InventoryClickEvent e) {
        if (e.getSlotType() == InventoryType.SlotType.CONTAINER && e.getInventory().getName().equalsIgnoreCase("enchant gui")) {
            e.setCancelled(true);
            ItemStack item = e.getWhoClicked().getItemInHand();

            // Arrow Damage
            if (e.getWhoClicked() instanceof Player && e.getCurrentItem().hasItemMeta()) {
                String[] parts = e.getCurrentItem().getItemMeta().getDisplayName().split(" ");
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("next level")) {
                    int newPage = main.menuPage.get(e.getWhoClicked().getName()) + 1;
                    main.menuPage.put(e.getWhoClicked().getName(), newPage);
                    main.gm.openEnchantShop((Player) e.getWhoClicked());
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("previous level")) {
                    int newPage = main.menuPage.get(e.getWhoClicked().getName()) - 1;
                    main.menuPage.put(e.getWhoClicked().getName(), newPage);
                    main.gm.openEnchantShop((Player) e.getWhoClicked());
                } else if (parts[0].equalsIgnoreCase("level")) {
                    main.menuPage.put(e.getWhoClicked().getName(), (Integer.parseInt(parts[1])-1));
                    main.gm.openEnchantShop((Player) e.getWhoClicked());
                } else {
                    for (Enchantment ent : Enchantment.values()) {
                        if (main.getDisplayName(ent).equalsIgnoreCase(e.getCurrentItem().getItemMeta().getDisplayName())) {
                            Player p = (Player) e.getWhoClicked();
                            if (item.getType().getMaxDurability() > 25 && ent.canEnchantItem(item) && !item.containsEnchantment(ent)) {
                                if (ent.getMaxLevel() > main.menuPage.get(p.getName())) {
                                    if (main.currency == 1) {
                                        if (main.takeMoneyFromPlayer(p, ent, main.menuPage.get(p.getName()))) {
                                            item.addEnchantment(ent, (ent.getStartLevel() + main.menuPage.get(p.getName())));
                                            item.addEnchantment(ent, (ent.getStartLevel() + main.menuPage.get(p.getName())));
                                            p.sendMessage(ChatColor.AQUA + "[EnchantGUI] " + ChatColor.WHITE + "Your item has been enchanted with " + ChatColor.GREEN + main.getDisplayName(ent));
                                        } else {
                                            p.sendMessage(ChatColor.AQUA + "[EnchantGUI] " + ChatColor.WHITE + "You don't have enough money!");
                                        }
                                    } else {
                                        if (main.takeExpFromPlayer(p, ent, main.menuPage.get(p.getName()))) {
                                            item.addEnchantment(ent, (ent.getStartLevel() + main.menuPage.get(p.getName())));
                                            item.addEnchantment(ent, (ent.getStartLevel() + main.menuPage.get(p.getName())));
                                            p.sendMessage(ChatColor.AQUA + "[EnchantGUI] " + ChatColor.WHITE + "Your item has been enchanted with " + ChatColor.GREEN + main.getDisplayName(ent));
                                        } else {
                                            p.sendMessage(ChatColor.AQUA + "[EnchantGUI] " + ChatColor.WHITE + "You don't have enough experience!");
                                        }
                                    }
                                } else {
                                    p.sendMessage(ChatColor.AQUA + "[EnchantGUI] " + ChatColor.WHITE + "This enchant does not have such a high level!");
                                }
                            } else {
                                p.sendMessage(ChatColor.AQUA + "[EnchantGUI] " + ChatColor.WHITE + "This enchant can't be put on that item!");
                            }
                            break;
                        }
                    }
                }
            }
        } else if (e.getSlotType() == InventoryType.SlotType.QUICKBAR && e.getInventory().getName().equalsIgnoreCase("enchant gui")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR && e.getPlayer().getItemInHand().getType() == Material.ENCHANTMENT_TABLE && e.getPlayer().hasPermission("eshop.table")) {
            tableClick.add(e.getPlayer().getName());
            e.getPlayer().sendMessage(ChatColor.AQUA + "[EnchantGUI] " + ChatColor.WHITE + "Okay! Please right click with the tool/weapon/armour you wish to enchant.");
        }
        else if (e.getAction() == Action.RIGHT_CLICK_AIR && tableClick.contains(e.getPlayer().getName())) {
            tableClick.remove(e.getPlayer().getName());
            main.gm.openEnchantShop(e.getPlayer());
        }
    }
}