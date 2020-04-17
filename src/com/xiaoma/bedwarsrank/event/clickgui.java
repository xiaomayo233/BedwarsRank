package com.xiaoma.bedwarsrank.event;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class clickgui implements Listener {
    //声明lang.yml的位置
    private final File langfile = new File("plugins\\bedwarsrank", "\\lang.yml");
    //加载lang.yml
    private final YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);

    @EventHandler
    //监听玩家点击GUI事件
    public void clickgui(InventoryClickEvent event){
        //检测GUI标题是否是/rank的GUI标题
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + lang.getString("RankGUIName"))){
            //设置事件取消
            event.setCancelled(true);
        }
    }
}
