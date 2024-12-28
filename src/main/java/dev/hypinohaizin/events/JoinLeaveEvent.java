package dev.hypinohaizin.events;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dev.hypinohaizin.AnniPunishments;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveEvent implements Listener {
   @EventHandler
   public void onLeave(PlayerQuitEvent event) {
      String uuid = event.getPlayer().getUniqueId().toString();
      File playerfile = new File(((AnniPunishments) AnniPunishments.getPlugin(AnniPunishments.class)).getDataFolder() + File.separator, "punishments.yml");
      FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerfile);
      if (playerData.contains(uuid) && playerData.getBoolean(uuid + ".ban.isbanned")) {
         event.setQuitMessage((String)null);
      }

   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      File playerfile = new File(((AnniPunishments) AnniPunishments.getPlugin(AnniPunishments.class)).getDataFolder() + File.separator, "punishments.yml");
      FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerfile);
      String uuid = event.getPlayer().getUniqueId().toString();
      long unixTime = System.currentTimeMillis() / 1000L;
      if (playerData.contains(uuid) && playerData.getBoolean(uuid + ".ban.isbanned")) {
         if ((long)playerData.getInt(uuid + ".ban.length") <= unixTime && playerData.getInt(uuid + ".ban.length") != -1) {
            try {
               playerData.set(uuid + ".ban.isbanned", false);
               playerData.set(uuid + ".ban.reason", "");
               playerData.set(uuid + ".ban.length", 0);
               playerData.set(uuid + ".ban.id", "");
               playerData.save(playerfile);
            } catch (IOException var9) {
               var9.printStackTrace();
            }
         }

         event.setJoinMessage((String)null);
         if (playerData.getInt(uuid + ".ban.length") == -1) {
            event.getPlayer().kickPlayer("§c§lBANNED!" );
         } else {
            if (playerData.getInt(uuid + ".ban.length") == 0) {
               return;
            }

            event.getPlayer().kickPlayer("§c§lBANNED\n" +
                    "§cThis ban will expire in: §e" + calculateTime(playerData.getInt(uuid + ".ban.length") - unixTime) + "\n");
         }
      }

      if (!playerData.contains(uuid)) {
         try {
            playerData.createSection(uuid);
            playerData.set(uuid + ".name", event.getPlayer().getName());
            playerData.createSection(uuid + ".ban");
            playerData.createSection(uuid + ".mute");
            playerData.set(uuid + ".ban.isbanned", false);
            playerData.set(uuid + ".ban.reason", "");
            playerData.set(uuid + ".ban.length", 0);
            playerData.set(uuid + ".ban.id", "");
            playerData.set(uuid + ".mute.ismuted", false);
            playerData.set(uuid + ".mute.reason", "");
            playerData.set(uuid + ".mute.length", 0);
            playerData.set(uuid + ".mute.id", "");
            playerData.save(playerfile);
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }

   }

   public static String calculateTime(long seconds) {
      int days = (int)TimeUnit.SECONDS.toDays(seconds);
      long hours = TimeUnit.SECONDS.toHours(seconds) - (long)(days * 24);
      long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
      long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
      String time = (" " + days + "d " + hours + "h " + minute + "m " + second + "s").toString().replace(" 0d", "").replace(" 0h", "").replace(" 0m", "").replace(" 0s", "").replaceFirst(" ", "");
      return time;
   }
}
