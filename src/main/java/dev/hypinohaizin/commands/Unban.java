package dev.hypinohaizin.commands;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import dev.hypinohaizin.AnniPunishments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Unban implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender.hasPermission("punishments.unban")) {
         if (args.length >= 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            File playerfile = new File(((AnniPunishments) AnniPunishments.getPlugin(AnniPunishments.class)).getDataFolder() + File.separator, "punishments.yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerfile);
            String uuid = null;
            if (target != null) {
               uuid = target.getPlayer().getUniqueId().toString();
            }

            if (uuid == null) {
               Iterator var10 = playerData.getKeys(false).iterator();

               while(var10.hasNext()) {
                  String key = (String)var10.next();
                  if (playerData.getString(key + ".name").equalsIgnoreCase(args[0])) {
                     uuid = key;
                  }
               }
            }

            if (uuid == null) {
               sender.sendMessage("§cそのプレイヤーは存在しません。");
               return false;
            }

            if (playerData.contains(uuid)) {
               if (playerData.getBoolean(uuid + ".ban.isbanned")) {
                  try {
                     playerData.set(uuid + ".ban.isbanned", false);
                     playerData.set(uuid + ".ban.reason", "");
                     playerData.set(uuid + ".ban.length", 0);
                     playerData.set(uuid + ".ban.id", "");
                     playerData.save(playerfile);
                     if (target != null) {
                        sender.sendMessage("§a" + Bukkit.getPlayer(args[0]).getName() + "のBANが解除されました。");
                     } else {
                        sender.sendMessage("§a" + args[0] + "のBANが解除されました。");
                     }
                  } catch (IOException var11) {
                     var11.printStackTrace();
                  }
               } else {
                  sender.sendMessage("§cそのプレイヤーはBANされてません。");
               }
            }
         } else {
            sender.sendMessage("§c無効なコマンド構文: /unban <name>");
         }
      } else {
         sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n" + "§c十分な権限があるのに実行できない場合はDevに報告してください。");
      }

      return false;
   }
}
