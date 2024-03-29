package com.ranull.graves.listener;

import com.ranull.graves.Graves;
import com.ranull.graves.type.Grave;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerInteractListener implements Listener {
    private final Graves plugin;

    public PlayerInteractListener(Graves plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if ((!plugin.getVersionManager().hasSecondHand() || (event.getHand() != null
                && event.getHand() == EquipmentSlot.HAND))
                && (plugin.getVersionManager().is_v1_7() || player.getGameMode() != GameMode.SPECTATOR)) {
            // Grave
            if (event.getClickedBlock() != null && event.useInteractedBlock() != Event.Result.DENY
                    && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                Grave grave = plugin.getBlockManager().getGraveFromBlock(block);

                if (grave == null) {
                    Block blockRelative = block.getRelative(event.getBlockFace());

                    if (!blockRelative.getType().isSolid()) {
                        grave = plugin.getBlockManager().getGraveFromBlock(blockRelative);
                    }
                }

                if (grave != null) {
                    event.setCancelled(plugin.getGraveManager().openGrave(player, block.getLocation(), grave));
                }
            }

            // Compass
            if (event.getItem() != null) {
                ItemStack itemStack = event.getItem();
                UUID uuid = plugin.getEntityManager().getGraveUUIDFromItemStack(itemStack);

                if (uuid != null) {
                    if (plugin.getCacheManager().getGraveMap().containsKey(uuid)) {
                        Grave grave = plugin.getCacheManager().getGraveMap().get(uuid);
                        List<Location> locationList = plugin.getGraveManager()
                                .getGraveLocationList(player.getLocation(), grave);

                        if (!locationList.isEmpty()) {
                            Location location = locationList.get(0);

                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(),
                                    plugin.getEntityManager().createGraveCompass(player, location, grave));

                            if (player.getWorld().equals(location.getWorld())) {
                                plugin.getEntityManager().sendMessage("message.distance", player,
                                        location, grave);
                            } else {
                                plugin.getEntityManager().sendMessage("message.distance-world", player,
                                        location, grave);
                            }
                        } else {
                            player.getInventory().remove(itemStack);
                        }
                    } else {
                        player.getInventory().remove(itemStack);
                    }
                }
            }
        }
    }
}
