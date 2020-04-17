package com.xiaoma.bedwarsrank.command;

import com.xiaoma.bedwarsrank.ranable.mysqlupdate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class rankgui implements CommandExecutor {
    //声明lang.yml的路径
    private final File langfile = new File("plugins\\bedwarsrank", "\\lang.yml");
    //加载lang.yml
    private final YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);
    @Override
    public boolean onCommand(CommandSender sender , Command command , String label , String[] args) {
        //检测是否有权限
        if(sender.hasPermission("bedwarsrank.basic.rank")) {
            //检测是否是玩家（控制台无法打开GUI）
            if (sender instanceof Player) {
                //为玩家打开GUI
                ((Player) sender).openInventory(mysqlupdate.gui);
            } else {
                //发送只有玩家可以执行该命令的信息
                sender.sendMessage(ChatColor.RED + lang.getString("NotPlayer"));
            }
            //发送没有权限的信息
        }else sender.sendMessage(ChatColor.RED +lang.getString( "NotPermission"));
        return true;
    }
}
