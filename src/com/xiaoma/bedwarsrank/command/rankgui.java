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
    //����lang.yml��·��
    private final File langfile = new File("plugins\\bedwarsrank", "\\lang.yml");
    //����lang.yml
    private final YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);
    @Override
    public boolean onCommand(CommandSender sender , Command command , String label , String[] args) {
        //����Ƿ���Ȩ��
        if(sender.hasPermission("bedwarsrank.basic.rank")) {
            //����Ƿ�����ң�����̨�޷���GUI��
            if (sender instanceof Player) {
                //Ϊ��Ҵ�GUI
                ((Player) sender).openInventory(mysqlupdate.gui);
            } else {
                //����ֻ����ҿ���ִ�и��������Ϣ
                sender.sendMessage(ChatColor.RED + lang.getString("NotPlayer"));
            }
            //����û��Ȩ�޵���Ϣ
        }else sender.sendMessage(ChatColor.RED +lang.getString( "NotPermission"));
        return true;
    }
}
